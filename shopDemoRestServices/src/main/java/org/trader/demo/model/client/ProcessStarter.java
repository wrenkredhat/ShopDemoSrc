package org.trader.demo.model.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.services.client.api.RemoteRestRuntimeEngineFactory;
import org.trader.demo.model.jpa.ShopOrder;

public class ProcessStarter {
	
	long PID = -1;

	public static void main(String[] args) {
		
		String userId = "bpmsAdmin";
		String password = "_Admin1!";

		String url = "localhost:8080";
		String deploymentId       = "DT_BOERSE:ShopDemo:1.0";
		String processName        = "ShopDemo.CaptureOrder";
		// processName        = "ShopDemo.testporcess";
		
		System.out.println ("------------------------------------------------");
		System.out.println ("--------          REST-MAIN            ---------");
		System.out.println ("------------------------------------------------");
		
		ProcessStarter pstr = new ProcessStarter();

		pstr.startProcess (userId, password, url, deploymentId, processName, getProcessArgs() );
	}
	
	public void startProcess(String userId, String password, String url, String deploymentId, String ProcessName, Map processVariables ) {
		System.out.println ( "EAP:XXX:startProcess:url"  + url );
		ShopHTManager shtm = new ShopHTManager ( userId, password,  url, deploymentId, ProcessName );
		PID = shtm.startProcessAndHandleTaskViaRestRemoteJavaAPI(processVariables, true);
	}
	
	public void startProcessRESTx (String userId, String password, String url, String deploymentId, String ProcessName, Map processVariables ) {
		
		url = url.replace(":8180", ":8080" );
		
		url = "http://"+url+"/business-central";
		
		System.out.println ( "EAP:startProcess:url"  + url );
		
		RuntimeEngine runtimeEngine = getRuntimeEngine(url, deploymentId, userId, password);
		
		System.out.println ( "EAP:runtimeEngine:"  + runtimeEngine );
		
		KieSession kieSession = runtimeEngine.getKieSession();

		System.out.println ( "EAP:ProcessVariables:"  + processVariables );
		
		kieSession.startProcess( ProcessName, processVariables );
		
		//kieSession.dispose();
		//kieSession.destroy();
	}

	private RuntimeEngine getRuntimeEngine(String applicationContext, String deploymentId, String userId,
			String password) {
		try {
			URL jbpmURL = new URL(applicationContext);
			RuntimeEngine remoteRestSessionFactory = RemoteRestRuntimeEngineFactory.newBuilder()
					.addDeploymentId(deploymentId).addUrl(jbpmURL).addUserName(userId).addPassword(password)
					.addExtraJaxbClasses(new Class[] {org.drools.core.xml.jaxb.util.JaxbMapAdapter.class})
					.addTimeout(10000)
					.build();
			
			return remoteRestSessionFactory;
		} catch (MalformedURLException e) {
			throw new IllegalStateException("This URL is always expected to be valid!", e);
		}
	}

	private static Map<String, Object> getProcessArgs() {
		Map<String, Object> processVariables = new HashMap<String, Object>();
		ShopOrder newOrder;
		
		newOrder = new org.trader.demo.model.jpa.ShopOrder();

		newOrder.setMarketIdSrc( "US" );
		newOrder.setMarketIdTgt( "US" );
		newOrder.setUser("US-U1");
		newOrder.setUserEmail("User1@us.shopdemo.org");
		newOrder.setQty(3);
		newOrder.setProductID( "0815" );
		
		processVariables.put("order", newOrder);
		// Equivalent of
		// http://localhost:8080/jbpm-console/rest/runtime/com.redhat.bpms.examples:mortgage:1/process/com.redhat.bpms.examples.mortgage.MortgageApplication/start?map_name=Babak&map_address=12300%20Wilshire&map_ssn=333224449i&map_income=200000i&map_price=1000000i&map_downPayment=200000i&map_amortization=30i
		return processVariables;
	}

}
