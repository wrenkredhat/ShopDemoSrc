

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class restcaller {
	
	  public static void getTaskSummaryList() throws Exception {
	    String status = "Reserved";
	    String actor = "krisv";

	    String addr = "http://localhost:8080/jbpm-console/rest/task/query?status="
	    											+ status + "&potentialOwner=" + actor;
	    
	    try {
	      HttpClient client = HttpClientBuilder.create().build();
	      HttpGet get = new HttpGet(addr);

	      String authData = "krisv" + ":" + "krisv";
	      String encoded = new sun.misc.BASE64Encoder().encode(authData.getBytes());
	      get.setHeader("Content-Type", "application/json");
	      get.setHeader("Authorization", "Basic " + encoded);
	      get.setHeader("ACCEPT", "application/xml");

	      HttpResponse cgResponse = client.execute(get);
	      String content = EntityUtils.toString(cgResponse.getEntity());
	      System.out.println(content);
	    } catch (Exception e) {
	      throw new Exception("Error consuming service.", e);
	    }
	  }
}

