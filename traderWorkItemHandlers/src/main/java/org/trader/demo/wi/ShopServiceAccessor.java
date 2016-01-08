package org.trader.demo.wi;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.trader.demo.model.jpa.Allocation;
import org.trader.demo.model.jpa.Marketplace;
import org.trader.demo.model.jpa.Product;
import org.trader.demo.model.jpa.ShopOrder;

public class ShopServiceAccessor implements WorkItemHandler {
		
	/*
    <workItemHandlers>
        <workItemHandler type="org.trader.demo.wi.RestProcessStarter" name="RestProcessStarter"/>
    </workItemHandlers>
	*/
	
	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		System.out.println("Oh no, my item aborted..");

	}
	
	static Class<?>[] signature = { WorkItem.class, WorkItemManager.class  };
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager ) {

	    // extract parameters
		
		  String  method      = (String) workItem.getParameter("Method");
		  
		  System.out.println( "zCalling Method:" + method  );
		  
		  Object result = null ;
		  
		  boolean found = false;
		  
		  
		  if (         "startProcess".equals(method) ) {
			  result =  startProcess ( workItem, manager );   found = true;
		  } else if (  "createItem".equals(method) ) {
			  result =  createItem ( workItem, manager );   found = true;
		  } else if (  "listItems".equals(method) ) {
			  result =  listItems ( workItem, manager );   found = true;
		  }  else if (  "getItem".equals(method) ) {
			  result =   getItem ( workItem, manager );   found = true;
		  } else if (  "updateItem".equals(method) ) {
			  result =  updateItem ( workItem, manager );   found = true;
		  } else if (  "buyProduct".equals(method) ) {
			  result =  buyProduct ( workItem, manager );   found = true;
		  } else if (  "checkProduct".equals(method) ) {
			  result =  checkProduct ( workItem, manager );   found = true;
		  } else if (  "generateOffer".equals(method) ) {
			  result =  generateOffer ( workItem, manager );   found = true;
		  } else if (  "sendRemoteOrder".equals(method) ) {
			  result =  sendRemoteOrder ( workItem, manager );   found = true;
		  } else if (  "storeOrder".equals(method) ) {
			  result =  storeOrder ( workItem, manager );   found = true;
		  } else if (  "allocateStock".equals(method) ) {
			  result =  allocateStock ( workItem, manager );   found = true;
		  } else if (  "processAllocation".equals(method) ) {
			  result =  processAllocation ( workItem, manager );   found = true;
		  } else if (  "advertiseProducts".equals(method) ) {
			  result =  advertiseProducts ( workItem, manager );   found = true;
		  } else if (  "sendUserNotification".equals(method) ) {
			  result =  sendUserNotification ( workItem, manager );   found = true;
		  } 
		  
		  if ( !found ) {
			  throw new RuntimeException( "NoMethod"  + method );
		  }
		    
		  
		  Map<String, Object> rm = new HashMap<String, Object>();
		  
		  rm.put( "Result",     result );
		  rm.put( "ResultItem", workItem.getParameter("Item") );
		  rm.put( "ResultProcessAllocationOrder", workItem.getParameter("processAllocationOrder") );
		  
		  
		  manager.completeWorkItem(workItem.getId(), rm );
	}
	
	public Boolean buyProduct(WorkItem workItem, WorkItemManager manager) {

		ShopOrder  order     = (ShopOrder)  workItem.getParameter("Item");
		
		return OrderService.checkProduct(order, true );

	}
	
	public Allocation allocateStock(WorkItem workItem, WorkItemManager manager ) {
		
		Allocation alloc = new Allocation();

		System.out.println ("allocateStock:" +  alloc );
		ShopOrder  order = (ShopOrder)  workItem.getParameter("Item");
		System.out.println ("allocateStock:" +  alloc );
		
		alloc.setOrder(order.getId());
		alloc.setPrdid(new Long(order.getProductID()));
		alloc.setStatus("NEW");
		alloc.setQty(order.getQty());
		
		System.out.println ("allocateStock:" +  alloc );
		
		Long id = (Long)WIRC.postToService("localhost:8180", "stockservice", "alloc", alloc );
		
		alloc=(Allocation)WIRC.getByID("localhost:8180", "Allocation", id);
		
		if ( id != -1 ) {
			return alloc;
		} else {
			return null;
		}
	}
	
	public Boolean processAllocation(WorkItem workItem, WorkItemManager manager ) {

		Allocation  alloc = (Allocation)  workItem.getParameter("Item");
		ShopOrder   order = (ShopOrder)   workItem.getParameter("Order");
		
		Long OrderId = alloc.getOrderid();
				
		Boolean result = (Boolean)WIRC.getFromService("localhost:8180", "stockservice", "alloc", alloc.getId().toString() );
		
		System.out.println ("processAllocation:orderid:"  +  OrderId );
		
		ShopOrder UpdOrd = (ShopOrder)WIRC.getByID("localhost:8180", "ShopOrder", OrderId );
		
		order.setVersion(UpdOrd.getVersion());
		
		return ( result );
	}
	
	
	public boolean storeOrder (WorkItem workItem, WorkItemManager manager) {

		ShopOrder  order     = (ShopOrder)  workItem.getParameter("Item");
		
		order.setStatus("NEW");
		
		Long ID= WIRC.putObject("localhost:8180", order, null );
		
		order.setId(ID);
		
		return true;

	}
	
	public List<ShopOrder> generateOffer(WorkItem workItem, WorkItemManager manager) {

		ShopOrder  order     = (ShopOrder)  workItem.getParameter("Item");
		
		WIRC.getByPath("localhost:8180", "ShopOrder", "notify",Long.toString(order.getBusinessKey())  );
		
		return OrderService.assembleOffer(order);

	}
	
	public Boolean checkProduct(WorkItem workItem, WorkItemManager manager) {

		ShopOrder  order     = (ShopOrder)  workItem.getParameter("Item");
		
		return OrderService.checkProduct(order, false );
		
	}
	
	
	public Void sendRemoteOrder( WorkItem workItem, WorkItemManager manager) {
		
		ShopOrder  order     = (ShopOrder)  workItem.getParameter("Item");
		
		Marketplace mp = (Marketplace)WIRC.getByID( "localhost:8180", "Marketplace", new Long(order.getMarketIdTgt()));
		
		System.out.println ( "sendRemoteOrder.mp:"+ mp  );
		
		workItem.getParameters().put("URL", mp.getURL() );
		
		return startProcess( workItem, manager );
	}
	
	public Void startProcess( WorkItem workItem, WorkItemManager manager) {

	    // extract parameters

		  ShopOrder  order     = (ShopOrder)  workItem.getParameter("Item");
		  String URL           = (String)     workItem.getParameter("URL");
		  String processName   = (String)     workItem.getParameter("ProcessName");
		  String deploymentId  = (String)     workItem.getParameter("ID");

		  System.out.println ( "asynch-order:" + order);
		  System.out.println ( "asynch-URL:"   + URL );
		  
		    //  Just 4 Demo
			String userId = "bpmsAdmin";
			String password = "_Admin1!";

			String applicationContext = "http://"+URL+"/business-central";
			
			final Map<String, Object> pstrtargs = new HashMap<String, Object>();
			
			Map pa = new HashMap();
			
			pa.put( "order", order );
			
			pstrtargs.put ( "url",          URL );  // TgtUrl !!!!
			pstrtargs.put ( "deploymentId", deploymentId );
			pstrtargs.put ( "processName",  processName );
			pstrtargs.put ( "userId",       userId );
			pstrtargs.put ( "password",     password );
			pstrtargs.put ( "processArgs",  pa );
			
			
			new Thread(new Runnable() {
			      public void run() {
						WIRC.startProcess(pstrtargs);

			      }
			    }).start();
			
			return ( null );
	}
	
	static WIRestCallerWIH WIRC = new WIRestCallerWIH();
	
	public Void createItem (WorkItem workItem, WorkItemManager manager) {

	    // extract parameters

		  Object item       =  ( Object) workItem.getParameter("Item");
		  String URL         = (String)  workItem.getParameter("URL");
		  
		  Long ID = WIRC.putObject(URL, item, null);
		  
		  try {
			Method M = item.getClass().getMethod("setId", new Class[] { Long.class });
			  
			  M.invoke(item, ID );
			  
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ( null );
		  	  
	}
	
	
	public List<Object> listItems (WorkItem workItem, WorkItemManager manager) {

	    // extract parameters

		String itemClass    =  (String) workItem.getParameter("Item");
		String URL          =  (String) workItem.getParameter("URL");
		  
		@SuppressWarnings("unchecked")
		List<Object> result = WIRC.getAll(URL, itemClass);
		  
		return ( result ); 	  
	}
	
	public Object getItem (WorkItem workItem, WorkItemManager manager) {

	    // extract parameters

		String itemClass   =  (String) workItem.getParameter("Item");
		String URL         =  (String) workItem.getParameter("URL");
		String ID          =  (String) workItem.getParameter("ID");
		  
		@SuppressWarnings("unchecked")
		Object result = WIRC.getByID(URL, itemClass, new Long(ID));
		  
		return ( result ); 	  
	}
	
	public Object getItemByPath (WorkItem workItem, WorkItemManager manager) {

	    // extract parameters

		String itemClass    = (String)    workItem.getParameter("Item");
		String URL          = (String)    workItem.getParameter("URL");
		String extention    = (String)    workItem.getParameter("Extention");
		  
		@SuppressWarnings("unchecked")
		List<Object> result = WIRC.getAll(URL, itemClass);
		  
		return ( result ); 	  
	}
	
	public Long updateItem (WorkItem workItem, WorkItemManager manager) {

	    // extract parameters
		
		System.out.println ( "updateItem:Item" );

		Object item  = (Object)    workItem.getParameter("Item");
		
		System.out.println ( "updateItem:Item:" + item.toString()  );
		
		String URL   = (String)    workItem.getParameter("URL");
		  
		Long ID = WIRestCallerWIH.getIdFromObject(item);
		
		workItem.getParameters().put("ID", ID.toString() );
		
		// workItem.getParameters().put("Item", item.getClass().getSimpleName() );
		
		
		@SuppressWarnings("unchecked")
		Long result = WIRC.putObject(URL, item, ID );

		Object updItem = WIRC.getByID("localhost:8180", item.getClass().getSimpleName() ,  ID );
		
		int newVersion = WIRestCallerWIH.getVersionFromObject(updItem);
		
		WIRestCallerWIH.setVersionFromObject( item, newVersion );
		
		return ( result );
		  	  
	}
	
	public Void advertiseProducts (WorkItem workItem, WorkItemManager manager) {
		
		System.out.println ( "advertiseProducts" );

		ShopOrder  so = (ShopOrder) workItem.getParameter("Item");
		
		String URL = so.getUserEmail();
		
		List<Product> lop= WIRC.getAll("localhost:8180", "Product" );
		
		String mail = "user:"   + so.getUser() + '\n';
		
		mail += "conent:\n";
		
		mail += "Products Avalible@MP"+ so.getMarketIdTgt() + ":\n";
		
		for ( Product p : lop ) {
			mail += p.toString();
		}
		
		WIRC.postObjectByURL(URL, "mails", mail);
		
		return null;
		
	}
	
	public Void sendUserNotification (WorkItem workItem, WorkItemManager manager) {
		
		System.out.println ( "sendUserNotification" );

		ShopOrder  so = (ShopOrder) workItem.getParameter("Item");
		
		String URL = so.getUserEmail();
		
		System.out.println ( "sendUserNotification:URL:" + URL );
		
		String mail = "user:"   + so.getUser() + '\n';
		
		mail += "conent:\n";
		
		mail += "Order executed:"+ so + ":\n";
		
		WIRC.postObjectByURL(URL, "mails", mail);
		
		return null;
		
	}
}