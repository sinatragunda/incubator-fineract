
package org.apache.fineract.wese.infrastructure.helper ;

import java.util.concurrent.Callable ;

import java.util.concurrent.Future ;


import org.apache.fineract.wese.utility.BulkReportTask ;
import org.apache.fineract.wese.utility.IndexedSession ;
import org.json.JSONObject ;
/*
	Changelog 
	01/31/2021 file created 


*/
public class BulkReportHelper{

	public static JSONObject runBulkReport(Callable callable){

		BulkReportTask bulkReportTask = new BulkReportTask();
		bulkReportTask.call(callable);
	
		IndexedSession.indexedSession().add(bulkReportTask);

		String sessionId = bulkReportTask.getSessionId();
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sessionId" ,sessionId);
		jsonObject.put("status" ,true);

		return jsonObject ;
	}

	public static int bulkReportStatus(String id){

		Future future = getBulkReport(id);
		int isDone = 0; 

		if(future!=null){			
			if(future.isDone()){
				isDone = 1;
			}
		}
		else{
			isDone = -1 ;
		}
		return isDone ;	
	}

	public static Future getBulkReport(String id){
		BulkReportTask bulkReportTask = (BulkReportTask)IndexedSession.indexedSession().get(id);
		if(bulkReportTask==null){
			return null ;
		}
		return bulkReportTask.getFuture();
	}



}