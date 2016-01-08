package org.trader.demo.rest;

import java.util.ArrayList;
import java.util.List;

import org.trader.demo.model.jpa.Product;
import org.trader.demo.model.jpa.ShopOrder;
import org.trader.demo.model.jpa.Stock;
import org.trader.demo.model.jpa.FXRate;
import org.trader.demo.model.jpa.Marketplace;

import javax.ws.rs.core.Response;

public class OrderService {
	
	static WIRestCallerRST wirc = new WIRestCallerRST();
	
	// ProductEndpoint     pep  = new ProductEndpoint();
	// MarketplaceEndpoint mpep = new MarketplaceEndpoint();
	// StockEndpoint       step = new StockEndpoint();
	// FXRateEndpoint      fxep = new FXRateEndpoint();
	
	Long calculatePriceLocal( ShopOrder order )
	{
		order.setShippingCosts(0);
		order.setMarketIdTgt(order.getMarketIdSrc());
		order.setRate(1.0);
		
		Product prd = (Product)wirc.getByID("localhost:8180", "Product", new Long(order.getProductID()) );
		// Product    prd = (Product)pep.findById(new Long(order.getProductID())).getEntity();
		
		Long itemPrice = prd.getPrice();
		
		System.out.println  ( "localhostItemPrice: " + itemPrice );
			
		return new Long( itemPrice ); 
	}
	
	Long calculatePriceRemote( ShopOrder order, Stock stock )
	{
		long result = 0;
	
		order.setShippingCosts(0);
		order.setMarketIdTgt(order.getMarketIdSrc());
		order.setRate(1.0);
		
		Long itemPrice = null;
		System.out.println  ( "localhostItemPrice: " + 0 );
		
		order.setFrgPrice((long) (order.getQty() * 1) );
		
		return new Long( result ); 
	}
	

	public List<ShopOrder> getCompoundOrders( ShopOrder localOrder, int localQty ) {
		
		List<ShopOrder> partialOrders = new ArrayList<ShopOrder>();
			
		List<Marketplace> marketplaces = wirc.getAll( "localhost:8180", "Marketplace" );
		// List<Marketplace> marketplaces = (List<Marketplace>)mpep.listAll(0, Integer.MAX_VALUE);
		
	
		System.out.println ( "marketlaces" + marketplaces );
		
		int itemsToOrder = localOrder.getQty()-localQty;
		
		for ( Marketplace mp : marketplaces ) {
			
			Stock foreigenStock = (Stock)wirc.getByPath(mp.getURL(), "Stock", "prdid", localOrder.getProductID());

			//---------------------------------------
			if ( foreigenStock == null ) continue;
			//---------------------------------------
			
			int items4ThisOrder = (itemsToOrder > foreigenStock.getQty() )?foreigenStock.getQty() : itemsToOrder;
			
			if ( items4ThisOrder > 0  ) {

				ShopOrder po = new ShopOrder();
				
				po.setMarketIdSrc(localOrder.getMarketIdSrc());
				po.setMarketIdTgt(mp.getId().toString());
				po.setProductID(localOrder.getProductID());
				po.setQty(items4ThisOrder);
				
				partialOrders.add(po);
				
				itemsToOrder -= po.getQty();
				
			}
			
		}
		
		return partialOrders;
	}
	
	public List<ShopOrder> priceOrders( List<ShopOrder> allOrders ) {
		
		Marketplace lmp=null;
		
		for ( ShopOrder o : allOrders ) {
			
			if ( lmp == null ) lmp=(Marketplace)wirc.getByID("localhost:8180", "Marketplace", new Long( o.getMarketIdSrc()));
			
			Marketplace fmp = (Marketplace)wirc.getByID("localhost:8180", "Marketplace", new Long( o.getMarketIdTgt()));
			
			FXRate rt = ( FXRate )wirc.getByPath(
					"localhost:8180", FXRate.class.getSimpleName(), "convert/"+lmp.getCCY(), fmp.getCCY() 
			);
			
			double rate = rt.getRate();
			
			o.setRate(rate);
			o.setFrgPrice( new Long((long)(o.getQty()*rate)));
			o.setPrice( new Long(0));
			o.setShippingCosts((int)fmp.getShippingCosts());
			o.setTotalPrice(
					(int)(o.getFrgPrice()+o.getPrice()+o.getShippingCosts())
			);
			
		}
		
		return allOrders;
	}
	
	public static List<ShopOrder> assembleOffer( ShopOrder lo ) {
		
		System.out.println ( "assembleOffer:" + lo );
		
		OrderService os = new OrderService();
		
		Stock ls   = (Stock)   wirc.getByPath("localhost:8180", "Stock",  "prdid", lo.getProductID());
		Product lp = (Product) wirc.getByID(  "localhost:8180", "Product", new Long(lo.getProductID()));
		
		int itemsLocal = ( lo.getQty() > ls.getQty() )? ls.getQty() : lo.getQty();
		
		ShopOrder nol = new ShopOrder();
		
		nol.setProductID(lo.getProductID());
		nol.setMarketIdSrc(lo.getMarketIdSrc());
		nol.setMarketIdTgt(lo.getMarketIdSrc()); // eq.
		nol.setQty(itemsLocal);
		nol.setPrice(lp.getPrice()*itemsLocal);
		nol.setFrgPrice((long)0);
		nol.setRate(1);
		
		List folist = os.getCompoundOrders( lo, nol.getQty()  );
		
		os.priceOrders(folist);
		
		folist.add(nol);
		
		return folist;
	}
	
	
	public static Boolean buyProductxxx( ShopOrder order, Boolean execOrder ) {
		
		boolean result = false;
		
		OrderService os = new OrderService();
		
		Stock stock = (Stock)wirc.getByPath("localhost:8180", "Stock", "prdid", order.getProductID());
		
		System.out.println ( "O.qty" + order.getQty() );
		System.out.println ( "s.qty" + stock.getQty() );
		
		if ( order.getQty() <= stock.getQty()  ) {
			result = true;
			if ( execOrder ) { 
			    stock.setQty(stock.getQty()-order.getQty());
			    order.setPrice( os.calculatePriceLocal( order ) * order.getQty() );
			    order.setTotalPrice(order.getPrice().intValue() );
			    wirc.putObject("localhost:8180", stock, stock.getId() );
			}
			order.setStatus("OK");
		} else {
			order.setStatus("FAIL:QTY");
			result = false;
		}
		
		if ( execOrder ) {
			Long id = wirc.putObject("localhost:8180", order, null );
			order.setId(id);
		}
		
		return new Boolean (result);
	}
	
	private void testFX() {

		wirc.getByPath(
				"localhost:8180", FXRate.class.getSimpleName(), "convert/"+"USD", "USD"
		);
		
	}
	
	
	public static void main( String[] args ) {
		OrderService os = new OrderService();
		
		os.testFX();
		ShopOrder newOrder = new org.trader.demo.model.jpa.ShopOrder();

		newOrder.setMarketIdSrc( "1" );
		newOrder.setMarketIdTgt( "1" );
		newOrder.setUser("USU1");
		newOrder.setUserEmail("User1@us.shopdemo.org");
		newOrder.setQty(3);
		newOrder.setProductID( "1" );
		

		List<ShopOrder> orders = os.getCompoundOrders(newOrder, 0);
	    os.priceOrders(orders);
		
		
		for ( ShopOrder fo : orders ) System.out.println ( fo );
		
	}
}
