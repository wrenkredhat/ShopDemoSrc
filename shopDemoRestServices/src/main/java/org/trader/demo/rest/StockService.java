package org.trader.demo.rest;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.trader.demo.model.jpa.Allocation;
import org.trader.demo.model.jpa.Stock;

@Stateless
@Path("/stockservice")
public class StockService {
	
	@PersistenceContext(unitName = "datamodel-persistence-unit")
	private EntityManager em;
	
	static WIRestCallerRST WIRC = new WIRestCallerRST();
	
	@POST
	@Path("/alloc")
	@Produces("application/json")
	@Consumes("application/json")
	public Response allocate( Allocation  a ) {
	System.out.println ("allocate" + a );	
	
    // @Consumes("text/plain")
	// public Response allocate( String  as ) {
	// System.out.println ("allocate<" + as.substring( 3, as.length()) );	
	// Allocation a =(Allocation)WIRestCallerRST.getObject(as, Allocation.class);
		
	    System.out.println ("xxallocate" + a );	
		
		Long result = new Long(-1);
		
		Long prdid = a.getPrdid();
		
		Stock stock = (Stock)WIRC.getByPath("localhost:8180", "Stock", "prdid", prdid.toString() );
		
		System.out.println ("Stock4alloc:" + stock );
		
		if (  a.getQty() <=  stock.getQty() ) {
			a.setStockid(stock.getId());
			Long ID = WIRC.putObject("localhost:8180", a, null );
			result = ID; 
			a.setId(ID);
		}
		
		return Response.created(
				UriBuilder.fromResource(StockService.class)
				.path(String.valueOf(result)).build()).build();
	}
	
	/*
	@GET
	@Path("/alloc/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public String processAllocation( @PathParam("id") Long id ) {
		
		Boolean result = false;
		
		
		Allocation alloc = (Allocation)WIRC.getByID("localhost:8180", "Allocation", id );

		String PRDS = new Long(alloc.getStockid()).toString();
		
		Stock      stock = (Stock)WIRC.getByPath("localhost:8180", "Stock", "prdid", PRDS );
		
		stock.setQty( (int)(stock.getQty() - alloc.getId()) );
		
		
		alloc.setStatus("done");
		
		WIRC.putObject("localhost:8180", alloc, alloc.getId());
		WIRC.putObject("localhost:8180", stock, stock.getId());
		
		return result.toString();
	}
	*/
	
	
	@GET
	@Path("/alloc/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public String processAllocation( @PathParam("id") Long id ) {
		
		Boolean result = false;
		
		Allocation alloc;
		
		TypedQuery<Allocation> findByIdQueryAlloc = em
				.createQuery(
						"SELECT DISTINCT a FROM Allocation a WHERE a.id = :entityId ORDER BY a.id",
						Allocation.class);
		findByIdQueryAlloc.setParameter("entityId", id);
		
		try {
			alloc = findByIdQueryAlloc.getSingleResult();
		} catch (NoResultException nre) {
			alloc = null;
		}
		
		if (alloc == null) {
			return "-1";
		}
		
		
		TypedQuery<Stock> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT s FROM Stock s WHERE s.id = :entityId ORDER BY s.id",
						Stock.class);
		findByIdQuery.setParameter("entityId", alloc.getStockid());
		
		Stock stock;
		try {
			stock = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			stock = null;
		}
		
		if (stock == null) {
			return "false";
		}
		
			
		try {
			alloc = findByIdQueryAlloc.getSingleResult();
		} catch (NoResultException nre) {
			alloc = null;
		}
		if (alloc == null) {
			return "false";
		}
		
		
		alloc.setStatus("done");
		stock.setQty(stock.getQty()-alloc.getQty());
		
		
		try {
			alloc = em.merge(alloc);
		} catch (OptimisticLockException e) {
			return "false";
			// Response.status(Response.Status.CONFLICT)
			//		.entity(e.getEntity()).build();
		}
		
		try {
			stock = em.merge(stock);
		} catch (OptimisticLockException e) {
			return "false";
			// Response.status(Response.Status.CONFLICT)
			//		.entity(e.getEntity()).build();
		}
		
		result = true;
		
		return result.toString();
	}
}
