package org.trader.demo.rest;

import java.io.IOException;
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

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.trader.demo.model.client.ProcessStarter;
import org.trader.demo.model.jpa.FXRate;
import org.trader.demo.model.jpa.ShopOrder;
import org.trader.demo.model.jpa.User;

import com.thoughtworks.xstream.XStream;

/**
 * 
 */
@Stateless
@Path("/processstarter")
public class StartProcessEndpoint {
	
	XStream xStream = new XStream();
	
	@GET
	@Produces("text/plain")
	public String ping() {
		return "OK";
	}
	
	@POST
	@Consumes("text/plain")
	// @Consumes("application/json")
	public Response create( String parameters )
	
	{
		System.out.println ("###################################################################" );
		//CreateProcesses.main(new String[]{});
		//System.out.println ("###################################################################" );

		System.out.println ("*** EAP:parameters:" + parameters );
		
		Map<String,Object> parameterMap = null;
		
		
		parameterMap = (Map<String, Object>) xStream.fromXML(parameters);
		
		/*
		JAXBContext jaxbContext = JAXBContext.newInstance(Person.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Person person = (Person) unmarshaller.unmarshal("xml string here");
		*/
		
		String url          = (String)parameterMap.get("url");
		String processName  = (String)parameterMap.get("processName");
		String deploymentId = (String)parameterMap.get("deploymentId");
        String userId       = (String)parameterMap.get("userId");
		String password     = (String)parameterMap.get("password");
		Map    processArgs  = (Map)   parameterMap.get("processArgs");
						
		System.out.println  ("'*'*'*'*'*'* EAP: processstarter:" + processName + "\n" + deploymentId + "\n" + processArgs );
		
		new ProcessStarter().startProcess(userId, password, url, deploymentId, processName,processArgs );
		
		return Response.ok().build();
	}
}
