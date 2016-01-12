package org.trader.demo.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.trader.demo.model.client.ShopHTManager;
import org.trader.demo.model.jpa.Allocation;
import org.trader.demo.model.jpa.ShopOrder;

/**
 * 
 */
@Stateless
@Path("/shoporders")
public class ShopOrderEndpoint {
	@PersistenceContext(unitName = "datamodel-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json")
	public Response create(ShopOrder entity) {
		System.out.println ("shoporder:POST:" + entity );
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(ShopOrderEndpoint.class)
						.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		ShopOrder entity = em.find(ShopOrder.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") Long id) {
		TypedQuery<ShopOrder> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT s FROM ShopOrder s WHERE s.id = :entityId ORDER BY s.id",
						ShopOrder.class);
		findByIdQuery.setParameter("entityId", id);
		ShopOrder entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
	}
	
	@GET
	@Path("/status/{status}")
	@Produces("application/json")
	public List<ShopOrder> findByStatus(@PathParam("status") String status ) {
		
		System.out.println ("ShopOrder:findByStatus:" + status );
		TypedQuery<ShopOrder> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT s FROM ShopOrder s WHERE s.Status = :status ORDER BY s.id",
						ShopOrder.class);
		findByIdQuery.setParameter("status", status);

		final List<ShopOrder> results = findByIdQuery.getResultList();

		return results;
	}

	

	@GET
	@Produces("application/json")
	public List<ShopOrder> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<ShopOrder> findAllQuery = em.createQuery(
				"SELECT DISTINCT s FROM ShopOrder s ORDER BY s.id",
				ShopOrder.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<ShopOrder> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@PathParam("id") Long id, ShopOrder entity) {
		
		System.out.println ("shoporder:PUT:" + id + ':' + entity );
		
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(entity.getId())) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(ShopOrder.class, id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			entity = em.merge(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
	
	
	String userId = "bpmsAdmin";
	String password = "_Admin1!";

	String url = "localhost:8080";
	String deploymentId       = "DT_BOERSE:ShopDemo:1.0";
	String processName        = "ShopDemo.ExecuteLocalOrderV3";
	
	
	@GET
	@Path("/notify/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response displayTasks(@PathParam("id") final Long id) {
		
		
		System.out.println ("displayTasks:BusinessKey:" + id );
		
		final ShopHTManager sHTM = new ShopHTManager(userId, password, url, deploymentId, null );
				
		new Thread(new Runnable() {
		      public void run() {
		    	  sHTM.displayOrder(  id  );

		      }
		    }).start();
		
		return Response.ok(new ShopOrder()).build();
	}
	
	@GET
	@Path("/release/{id:[0-9][0-9]*}/{released:[TFtf]}")
	@Produces("application/json")
	public Response releaseOrder(  @PathParam("id") Long id,
			                       @PathParam("released") String releaseds ) {
		
		Boolean released = Boolean.FALSE;
		
		if (releaseds.equalsIgnoreCase("T")) {  released = Boolean.TRUE; }
		
		ShopHTManager sHTM = new ShopHTManager(userId, password, url, deploymentId, null );
		
		sHTM.releaseOrder(  id, released  );

		return Response.ok().build();
	}
	
	@GET
	@Path("/restart/{status}")
	@Produces("application/json")
	public Response restartOrder( @PathParam("status") String status ) {
		
		System.out.println ("restartOrder:status:" + status );

		List<ShopOrder> loo = findByStatus(status);
		
		TypedQuery<Allocation> findAllocationByIdQuery = em
				.createQuery(
						"SELECT DISTINCT a FROM Allocation a WHERE a.orderid = :orderId ORDER BY a.id",
						Allocation.class);

		for ( ShopOrder so : loo ) {
			
			System.out.println ("restartOrder:Starting Order" + so );
			
			Map variables = new HashMap();

			Allocation allocation;

			findAllocationByIdQuery.setParameter("orderId", so.getId());

			try {
				allocation = findAllocationByIdQuery.getSingleResult();
				variables.put( "allocation", allocation);
				System.out.println ("restartOrder:found Allocation" + allocation );
			} catch (NoResultException nre) {
				System.out.println ("restartOrder:no Allocation" );
				allocation = null;
			}
			
			variables.put( "order4p",    so );
					
			processName = "ShopDemo.ExecuteLocalOrderV3";
			
			final ShopHTManager sHTM = new ShopHTManager(userId, password, url, deploymentId, this.processName );
			
			sHTM.startProcessAndHandleTaskViaRestRemoteJavaAPI( variables, false );
			
		}
			
		return Response.ok().build();
	}
	
		
}
