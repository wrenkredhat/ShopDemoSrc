package org.trader.demo.model.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.trader.demo.model.jpa.ShopOrder;
 

public class ShopHTManager {
	
	String userId;
	String password;
	String url;
	String deploymentId;
    String processName;
    URL    urlu;
    
    RuntimeEngine engine = null;
    
    KieSession ksession  = null;

	public ShopHTManager(String userId, String password, String url, String deploymentId, String processName ) {
		
		this.userId = userId;
		this.password = password;
		this.url = url;
		this.deploymentId = deploymentId;
		this.processName = processName;
		
        url = url.replace(":8180", ":8080" );
		
		url = "http://"+url+"/business-central";
		
		try {
			urlu = new URL( url );
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException ("startProcessAndHandleTaskViaRestRemoteJavaAPI:no URL:" + url );
		}
        
        // Setup the factory class with the necessarry information to communicate with the REST services
        engine = RemoteRuntimeEngineFactory.newRestBuilder()
        		.addUrl(urlu)
        		.addTimeout(5)
        		.addDeploymentId(deploymentId)
        		.addUserName(userId)
        		.addPassword(password)
        		// if you're sending custom class parameters, make sure that
        		// the remote client instance knows about them!
        		// .addExtraJaxbClasses(MyType.class)
        		.build();
	}

	public long startProcessAndHandleTaskViaRestRemoteJavaAPI( Map processVariables, boolean wait4task  ) {
 
        // Create KieSession and TaskService instances and use them
        ksession = engine.getKieSession();
        
        TaskService taskService = engine.getTaskService();
          
        // Each operation on a KieSession, TaskService or AuditLogService (client) instance
        // sends a request for the operation to the server side and waits for the response
        // If something goes wrong on the server side, the client will throw an exception.
        
        ProcessInstance processInstance = ksession.startProcess(processName, processVariables );
        
        System.out.println ("Vars:" + processVariables );
        
        long procId = processInstance.getId();
        
        System.out.println ("Started PID:" + procId );
        
        if (  wait4task && false ) {
        	wait4task( procId );
        	return procId;
        }
        
        return procId;
    }
	

	public void wait4task( long procId ) {
		
        String taskUserId = userId;
        
        
        System.out.println("wait4task" );
        
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        TaskService taskService = engine.getTaskService();
    
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(userId, "en-US");
          
        for (TaskSummary tasksum : tasks) {
        	
        	System.out.println("Found Task: " + tasksum );
        	
        	long tskID = tasksum.getId();
        	
        	if ( tskID != -1 ) {
        		               
        		Map tskContent = taskService.getTaskContent(tskID);
        		
        		Task task = taskService.getTaskById(tskID);
        		
        		String processId = task.getTaskData().getProcessId();
        		      		
        		System.out.println ("ProcessId" + processId );

        		if (processId.equals(""))
        				for ( Object o :  tskContent.keySet() ) {
        					System.out.println ( "TskCntKey:"  + o +':'+ tskContent.get(o) );
        		}
           		     		
            
        		taskService.start(tskID, taskUserId);
        		
        		Map<String, Object> resultData = new HashMap<String, Object>();
                
                Boolean completed = new Boolean(true);
                resultData.put (  "completed",  completed );
                
                taskService.complete(tskID, taskUserId, resultData);
        		
        	}
        }
    }
	
	
	protected void sleep( int sec ) {
    try {
					Thread.sleep(1000*sec);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public ShopOrder displayOrder( long bKey ) {
		
		   System.out.println("displayOrder" ); 
		
		   sleep(2);
		   
		   System.out.println("sleep" ); 
		
	       TaskService taskService = engine.getTaskService();
	       
	       System.out.println("taskService" ); 
			
	        boolean found = false;
	        
	        for( ;!found; ) {
	        	
		        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner( userId, "en-US");
	        	
	        	 System.out.println("Loop-found" ); 
	        	
		        for (TaskSummary tasksum : tasks) {
		        	
		        	System.out.println("Found Task: " + tasksum );
		        	
		        	long tskID = tasksum.getId();
		        	
		        	System.out.println("Found tskID: " + tskID );
		        	
		        	if ( tskID != -1 ) {
		        	
		        		Task task = taskService.getTaskById(tskID);
		        		
		        		
		        		Map tskContent = taskService.getTaskContent(tskID);
		        		
		        		
		           		String processId = task.getTaskData().getProcessId();
		        		
		        		System.out.println ("ProcessId:" + processId );
		        		
		        		sleep(3);
		        		
		        		if ( processId.equals ("ShopDemo.OrderProcess") ||  processId.equals ("ShopDemo.CaptureOrder") ) {
		        			                
			           		for ( Object key :  tskContent.keySet() ) {
			        			System.out.println ( "TskCntKey:"  + key +':'+ tskContent.get(key) );
			        			
			        			if ( key.equals("checkOrder") ) {
			        				
			        				ShopOrder so = (ShopOrder)tskContent.get(key);
			        				
			        				if ( so.getBusinessKey() == bKey ) {
			        					System.out.println ( "/********             Found Order           *********/" );
			        				    System.out.println ( so.toString() );	
			        				    System.out.println ( "/****************************************************/" );
			        				    found = true;
			        				    
			        				    return so;
			        				}
			        			}
			        		}	                	        		
			        	}    		
		        	}
		         }
		        
		        sleep(1);
	        }
	        
	        return null;
	}
				
	public void releaseOrder( long bKey, Boolean released ) {
		
      
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Setup the factory class with the necessarry information to communicate with the REST services
        engine = RemoteRuntimeEngineFactory.newRestBuilder()
        		.addUrl(urlu)
        		.addTimeout(5)
        		.addDeploymentId(deploymentId)
        		.addUserName(userId)
        		.addPassword(password)
        		// if you're sending custom class parameters, make sure that
        		// the remote client instance knows about them!
        		// .addExtraJaxbClasses(MyType.class)
        		.build();
        
        TaskService taskService = engine.getTaskService();
    
        taskService = engine.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(userId, "en-US");
          
        for (TaskSummary tasksum : tasks) {
        	
        	System.out.println("Found Task: " + tasksum );
        	
        	long tskID = tasksum.getId();
        	
        	if ( tskID != -1 ) {
        		               
        		Map tskContent = taskService.getTaskContent(tskID);
        		
        		Task t = taskService.getTaskById(tskID);
        		
        		System.out.println( "t.status" + t.getTaskData().getStatus());
                
           		for ( Object key :  tskContent.keySet() ) {
           			
        			System.out.println ( "TskCntKey:"  + key +':'+ tskContent.get(key) );
        			
        			if ( key.equals("checkOrder") ) {
        				
        				ShopOrder so = (ShopOrder)tskContent.get(key);
        				
        				if ( so.getBusinessKey() == bKey ) {
        					
        					System.out.println ( "/********        RELEASE   O R D E R        *********/" );
        				    System.out.println ( so.toString() );	
        				    System.out.println ( "/****************************************************/" );
        				    
        				    
        				    // Taskname: ReleasegeneratedOrders// 
        				    
        		            // taskService.claim(tskID, userId);
        		            taskService.start(tskID, userId);
        		        		
        		        	Map<String, Object> resultData = new HashMap<String, Object>();
        		                
        		            resultData.put (  "confirmed",  released );
        		                
        		            taskService.complete(tskID, userId, resultData );
        				}
        			}
        		}	
        	}
        }
    }

  }
	
