package org.trader.demo.wi;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.type.JavaType;
import org.drools.core.runtime.help.impl.XStreamXML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trader.demo.model.jpa.Allocation;
import org.trader.demo.model.jpa.ShopOrder;
import org.trader.demo.model.jpa.Stock;

import com.thoughtworks.xstream.XStream;

public class WIRestCallerWIH {

	ObjectMapper mapper = new ObjectMapper();

	public <ReturnedObject> List<ReturnedObject> getObjectList(String jtxt, Class<ReturnedObject> returnedObjectClass) {
		List<ReturnedObject> objectList = new ArrayList<>();

		final CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class,
				returnedObjectClass);
		try {
			objectList = mapper.readValue(jtxt, collectionType);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return objectList;
	}

	public static <ReturnedObject> ReturnedObject getObject(String jtxt, Class<ReturnedObject> returnedObjectClass) {
		ReturnedObject object = null;

		ObjectMapper mapper = new ObjectMapper();
		final JavaType type = mapper.getTypeFactory().constructType(returnedObjectClass);

		try {
			object = mapper.readValue(jtxt, type);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return object;
	}
	
	XStream xStream = new XStream();
	
	public String ObjectToXSTREAM (Object object ) {
		return xStream.toXML(object);
	}
	
    public static String ObjectToJSON(Object object ) {
    	
        ObjectMapper mapper = new ObjectMapper();
        try {
        	
        	ByteArrayOutputStream baOS = new ByteArrayOutputStream();
        	
            OutputStreamWriter osw = new OutputStreamWriter(baOS);
            Writer writer = new BufferedWriter(osw);
            
            // mapper.writerFor(object.getClass()).writeValue( writer, object);
            
            mapper.writeValue(writer,object);
            
            baOS.close();
            osw.close();
            writer.close();
            
            return baOS.toString();
            
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    protected String getHostPort(String HostPort ) {
    	
    	// tring portOffset=java.util.Properties.getProperty("");
    	
    	String hp[] = HostPort.split(":");
    	
    	
    	
    	return hp[0];
    }

	protected URL getUrl(String HostPort, String msClass, String service ) {

		String urlString = "http://" + HostPort + "/restServices/shopdemo/" + msClass.toLowerCase();

		if (service != null) {
			urlString = urlString + service;
		}

		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	public List getAll(String HostPort, String msClass ) {

		URL url = getUrl(HostPort, msClass, "s");

		System.out.println("URL:" + url.toString());

		String jstring = getResponse("GET", url, null,null );

		// System.out.println ( "jstring:" + jstring );

		try {
			return (List) getObjectList(jstring, Class.forName("org.trader.demo.model.jpa."+msClass));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	String getResponse( String method, URL url, String output, String contentType ) {

		System.out.println ("URL:" + url.toString());

		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
			conn.setRequestProperty("Accept", "application/json");
			
	        if( output != null ){
	        	System.out.println ("To Server...." + method );
	        	conn.setRequestProperty(
	        			"Content-Type", (contentType == null)? "application/json" : contentType );
	        	System.out.println ("Content-Type:" + conn.getRequestProperty("Content-Type"));
	        	conn.setDoInput(true);
	        	conn.setDoOutput(true);
                DataOutputStream out = new  DataOutputStream(conn.getOutputStream());
                out.writeBytes(output);
                out.flush();
                out.close();
                // conn.connect();
            }
	        
	        /*
	        Map hfm = conn.getHeaderFields();
	        
	        for ( Object o : hfm.keySet() ) {
	        	System.out.println ( "Key:" + o );
	        	System.out.println ( "Val:" + hfm.get(o).getClass().getCanonicalName() );
	        }
	        */
	        
	        int rc = conn.getResponseCode();
	        
	        System.out.println ( "HttpURLConnection:rc:" + rc );
	        
	        String createLocation = null;
	        
	        if ( "POST".equals(method) && conn.getHeaderFields().containsKey("Location") ) {
	        	createLocation = conn.getHeaderFields().get( "Location" ).get(0);
	        	System.out.println ( "HttpURLConnection:createLocation->" + createLocation );
	        }
	        	
			if ( !( conn.getResponseCode() == 200 || conn.getResponseCode() == 201 ) ) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringWriter sw = new StringWriter();
			while ((output = br.readLine()) != null) {
				sw.append(output);
			}
			conn.disconnect();
			
			System.out.println ("output:" + sw.toString() );
			
			return (createLocation == null)? sw.toString(): createLocation;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	public Object getByID(String HostPort, String msClass, Long id) {

		URL url = getUrl(HostPort, msClass, "s/" + id);
		
		System.out.println ( "getByID:url:" + url );

		String jstring = getResponse("GET", url, null, null );

		// System.out.println ( "jstring:" + jstring );

		try {
			return getObject(jstring, Class.forName("org.trader.demo.model.jpa."+msClass));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	public Object getByPath( String HostPort, String msClass, String path, String id) {
		
 		
 		URL url = getUrl(HostPort, msClass, "s/" + path + '/' + id );
 		
 	    System.out.println ( "getByPath:url:" + url );
 
 		String jstring = getResponse("GET", url, null, null );

		System.out.println ( "jstring:" + jstring );

 	    try {
 		    return getObject(jstring, Class.forName("org.trader.demo.model.jpa."+msClass));
 	    } catch (ClassNotFoundException e) {
 		    // TODO Auto-generated catch block
 		    e.printStackTrace();
 	    }
	
	return null;

		
	}
	
	public Object postByPath( String HostPort, String msClass, String path, Object obj ) {
						
 		URL url = getUrl(HostPort, msClass, "s/" + path );
 		
 	    System.out.println ( "postByPath:url:" + url );
 	    
 	    String jstring = ObjectToJSON(obj);
 
 		String repsonse = getResponse("POST", url,jstring, "text/plain" );

		System.out.println ( "jstring:" + jstring );

	 	try {
	 		return getObject(jstring, Class.forName("org.trader.demo.model.jpa."+msClass));
	 	} catch (ClassNotFoundException e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	}
		
		return null;
	}
	
	public Object postToService ( String HostPort, String ServiceName, String verb, Object obj ) {
		
 		URL url = getUrl(HostPort, ServiceName, "/" + verb );
 		
 	    System.out.println ( "postToService:url:" + url );
 	    
 	   String jstring = ObjectToJSON(obj);
 
 	   String repsonse = null;
 		
 	   repsonse = getResponse("POST", url,jstring, "application/json" );
	   //repsonse = getResponse("POST", url, jstring, "text/plain" );

		// System.out.println ( "repsonse:" + repsonse );

	
		String[] subDirs = repsonse.split("/");
		
		return new Long ( subDirs[ subDirs.length-1 ] );
	}
	
	
	public Boolean getFromService ( String HostPort, String ServiceName, String verb, String ID ) {
		
 		URL url = getUrl(HostPort, ServiceName, '/' + verb + '/' + ID );
 		
 	    System.out.println ( "getFromService:url:" + url );
 	     
 	    String repsonse = null;
 		
 	     repsonse = getResponse("GET", url,null, null );

		System.out.println ( "repsonse:" + repsonse );

	
		return new Boolean (repsonse );
	}
	
	protected static Long getIdFromObject(Object o ) {
		
		try {
			Method m = o.getClass().getMethod("getId");
					
			if (m != null) {
				
				Long result = (Long)m.invoke(o);
				
				System.out.println("getIdFromObject:got ID" + result );
				
				return result;
			}
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
		
		return null;
		
	}
	
	protected static int getVersionFromObject(Object o ) {
		
		try {
			Method m = o.getClass().getMethod("getVersion");
					
			if (m != null) {
				
				int result = (int)m.invoke(o);
				
				System.out.println("getVersionFromObject vers:" + result );
				
				return result;
			}
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
		return -1;
	}
	
	
	protected static void setVersionFromObject(Object o, int v ) {
		
		try {
			Method m = o.getClass().getMethod("setVersion", int.class);
					
			if (m != null) {
				
				m.invoke(o,v);
				
				System.out.println("setIdFromObject:setv" + v );
			}
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
	}
	
	public Long putObject(String HostPort, Object obj, Long id ) {
		
		String extention = "s";
		String method = "POST";
		
		id = getIdFromObject( obj );
		
		if ( id != null) {
			extention = "s/"+id;
			method = "PUT";
		}
			
		URL url = getUrl( HostPort, obj.getClass().getSimpleName(), extention  );
		
		String jstring = ObjectToJSON(obj);

		String response = getResponse(method, url, jstring, null );

		System.out.println ( "response:" + response );
				
		if ( id != null ) {
			return id;
		} else {
			String[] subDirs = response.split("/");
			return new Long ( subDirs[ subDirs.length-1 ] );
		}
	}
	
	
	public Long putObjectByPath(String HostPort, Object obj, Long id ) {
		
		String extention = "s";
		String method = "POST";
		
		if ( id != null) {
			extention = "s/"+id;
			method = "PUT";
		}
		
		URL url = getUrl( HostPort, obj.getClass().getSimpleName(), extention  );
		
		String jstring = ObjectToJSON(obj);

		String response = getResponse(method, url, jstring, null );

		System.out.println ( "response:" + response );
				
		if ( id != null ) {
			return id;
		} else {
			String[] subDirs = response.split("/");
			return new Long ( subDirs[ subDirs.length-1 ] );
		}
	}
	
	public Long postObjectByURL(String HostPort, String URL , Object obj) {
		
		
		URL url = getUrl( HostPort, URL, "" );
		
		String jstring = ObjectToJSON(obj);

		String response = getResponse("POST", url, jstring, null );

		System.out.println ( "response:" + response );
				
			String[] subDirs = response.split("/");
		return new Long ( subDirs[ subDirs.length-1 ] );
	}
	
	public Long startProcess( Object parameters ) {
		
		String extention = "";
		String method = "POST";
		
		
		String urlString = "http://localhost:8180/restServices/shopdemo/processstarter";

		System.out.println ( "EAP:startProcess:XSTREAM: URL:" + urlString );
		
		URL url = null;
		
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// String jstring = ObjectToJSON(parameters);
		String xstring = ObjectToXSTREAM(parameters);		
		System.out.println ( "EAP:jstring"  + xstring );

		String response = getResponse(method, url, xstring, "text/plain" );
		// String response = getResponse(method, url, jstring, "application/json" );

		System.out.println ( "EAP:response:" + response );
		
		return new Long (0);
				
	}
	
	
	void buyTest() {
		
	ShopOrder newOrder;
		
		newOrder = new org.trader.demo.model.jpa.ShopOrder();

		newOrder.setBusinessKey(new Date().getTime());
		newOrder.setMarketIdSrc( "1" );
		newOrder.setMarketIdTgt( "1" );
		newOrder.setUser("US-U1");
		newOrder.setUserEmail("User1@us.shopdemo.org");
		newOrder.setQty(3);
		newOrder.setProductID( "1" );
		
		boolean success = OrderService.checkProduct(newOrder, false );
		
		System.out.println ( success );
	}
	

	void allocTest() {
		
		ShopOrder order = new org.trader.demo.model.jpa.ShopOrder();

		order.setMarketIdSrc( "1" );
		order.setMarketIdTgt( "1" );
		order.setUser("US-U1");
		order.setUserEmail("User1@us.shopdemo.org");
		order.setQty(3);
		order.setProductID( "1" );
		
		
		Allocation a = new Allocation();
		
		a.setOrder(order.getId());
		a.setPrdid(new Long(order.getProductID()));
		a.setStatus("NEW");
		a.setQty(order.getQty());
			
		// String IDS = (String)postToService("localhost:8180", "stockservice", "alloc", a );
		
		Long id = (Long)postToService("localhost:8180", "stockservice", "alloc", a );
		
		if ( id != -1 ) {
				a.setId(id);
				
				Object o = getFromService("localhost:8180", "stockservice", "alloc", id.toString());
				
				System.out.println (o);
		}
		
		
	}
	
	
	void getbyPathTest() {
		Stock sget;
		List<Stock> StockRep = getAll("localhost:8180", "Stock");
		sget = (Stock)getByID("localhost:8180", "Stock", (long)1);
		System.out.println("getById:"+sget);
		sget = (Stock) getByPath("localhost:8180", "Stock", "prdid", "0815");
		System.out.print("getByPath:"+sget);
	}
	
	public static void main(String[] args) {

		WIRestCallerWIH rc = new WIRestCallerWIH();
		
		
		// rc.getbyPathTest(); System.exit(0);
		
		// rc.buyTest(); System.exit(0);
		
		// rc.allocTest(); System.exit(0);
		
 		
		String deploymentId       = "DT_BOERSE:ShopDemo:1.0";
		String processName        = "ShopDemo.OrderProcess";
		// String processName        = "ShopDemo.CaptureOrder";

		String userId = "bpmsAdmin";
		String password = "_Admin1!";
		
		Map<String, Object> pa = getProcessArgs();
		
		Map<String, Object> pstrtargs = new HashMap<String, Object>();
		
		pstrtargs.put ( "url",          "localhost:8080" );  // TgtUrl !!!!
		pstrtargs.put ( "deploymentId", deploymentId );
		pstrtargs.put ( "processName",  processName );
		pstrtargs.put ( "userId",       userId );
		pstrtargs.put ( "password",     password );
		pstrtargs.put ( "processArgs",  pa );
		
		rc.startProcess ( pstrtargs  );
		
		System.exit(0);
				
		List<Stock> StockRep = rc.getAll("localhost:8180", "Stock");

		System.out.println("List Stocks:");
		for (Stock s : StockRep) {
			System.out.println("Stock:" + s);
			System.exit(0);
		}

		Stock s = StockRep.get(0);

		System.out.println("Get Stock 1:");
		Stock sget = (Stock) rc.getByID("localhost:8180", "Stock", s.getId());

		System.out.println("Stock:" + s.getId() + ":" + sget);
		
		// System.out.println("Stock-JSON:" + s.getId() + ":" + rc.ObjectToJSON( sget ));
		
		System.out.println("Update Stock 1:");
		
		sget.setQty(sget.getQty()-2);
		
		Long putResult;
		
		putResult = rc.putObject("localhost:8180", sget, sget.getId() );
		
		sget = (Stock) rc.getByID("localhost:8180", "Stock", s.getId());

		System.out.println("Stock:" + s.getId() + ":" + sget);
		
		System.out.println("Create Stock 44:");
		
		Stock newStock = new Stock();
		
		newStock.setId((long)44);
		newStock.setId(null);
		newStock.setMarketID("M44");
		newStock.setProductID("PID1");
		newStock.setQty(666);
		newStock.setVersion(1);

		putResult = rc.putObject("localhost:8180", newStock, null );
		
		System.out.println("newStock:putResult:" + putResult );
		
		newStock.setId(putResult);
		
		System.out.println("newStock:" + newStock.getMarketID() );

		newStock.setQty(sget.getQty()-6);

		System.out.println("Update New Stock :" + newStock.getId() );

		putResult = rc.putObject("localhost:8180", newStock, newStock.getId() );
		
		System.out.println("Get Stock :" + newStock.getId() );

		Stock newUpdateStock =  (Stock) rc.getByID("localhost:8180", "Stock", newStock.getId());

		System.out.println("Get Stock :" + newUpdateStock  );

	}
	
	private static Map<String, Object> getProcessArgs() {
		Map<String, Object> processVariables = new HashMap<String, Object>();
		ShopOrder newOrder;
		
		newOrder = new org.trader.demo.model.jpa.ShopOrder();

		newOrder.setBusinessKey(new Date().getTime());
		newOrder.setMarketIdSrc( "1" );
		newOrder.setMarketIdTgt( "1" );
		newOrder.setUser("US-U1");
		newOrder.setUserEmail( WIRestCallerWIH.getIP() + ":8180" );
		newOrder.setQty(3);
		newOrder.setProductID( "1" );
		
		processVariables.put("order", newOrder);
		// Equivalent of
		// http://localhost:8180/jbpm-console/rest/runtime/com.redhat.bpms.examples:mortgage:1/process/com.redhat.bpms.examples.mortgage.MortgageApplication/start?map_name=Babak&map_address=12300%20Wilshire&map_ssn=333224449i&map_income=200000i&map_price=1000000i&map_downPayment=200000i&map_amortization=30i
		// "http://localhost:8180/restServices/shopdemo/shoporders/status/FAIL:QTY"
		return processVariables;
	}
	
	public static String getIP () {
		   
	        InetAddress ip;
	            try {
					ip = InetAddress.getLocalHost();
					return ip.getHostAddress();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       return null;
	}
	       
}
