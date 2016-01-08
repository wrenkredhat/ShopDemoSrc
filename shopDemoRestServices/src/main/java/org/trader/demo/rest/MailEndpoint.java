package org.trader.demo.rest;

import java.util.List;

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
import org.trader.demo.model.jpa.Product;

/**
 * 
 */
@Stateless
@Path("/mails")
public class MailEndpoint {

	@POST
	@Consumes("text/plain")
	public Response receive(String  mail ) {
		
		System.out.println ( "---------------------------------------------------------" );
		System.out.println ( "Mail received:" + mail );
		System.out.println ( "---------------------------------------------------------" );
		
		return Response.created(
				
				UriBuilder.fromResource(MailEndpoint.class)
						.path(String.valueOf(Long.MAX_VALUE)).build()).build();
	}


}
