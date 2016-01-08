package org.trader.demo.wi;

import java.util.HashMap;
import java.util.Map;
 
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
 
public final class StrToJSONConverter implements WorkItemHandler {
 
  public void abortWorkItem(final WorkItem item, final WorkItemManager manager) {
    manager.abortWorkItem(item.getId());
  }
 
  public void executeWorkItem(final WorkItem item, final WorkItemManager manager) {
	 
	String jstring = (String) item.getParameter("JSON_STRING");
	
	final Map<String, Object> resultMap = new HashMap<String, Object>();
 
    resultMap.put("PARAMETER_MAP", WIRestCallerWIH.getObject(jstring, HashMap.class ));
 
    manager.completeWorkItem(item.getId(), resultMap);
  }
 
}
