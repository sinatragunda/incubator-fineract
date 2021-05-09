/*
	Added 01/02/2021


*/

package org.apache.fineract.wese.utility ;


import org.apache.fineract.wese.service.WeseSessionObject;
import java.util.concurrent.Callable ;
import java.util.concurrent.Future ;
import java.util.concurrent.FutureTask ;
import java.util.concurrent.Executors ;
import java.util.concurrent.ExecutorService ;

import org.apache.fineract.wese.helper.SessionIDGenerator ;


public class BulkReportTask implements WeseSessionObject{
	
	private String sessionId ;
	private Future future = null;

	public BulkReportTask(){
		this.sessionId = SessionIDGenerator.sessionId();
	}

	public void call(Callable callable){
		ExecutorService executor = Executors.newSingleThreadExecutor();
		this.future = executor.submit(callable);
	} 

	@Override
	public String getSessionId(){
		return this.sessionId ;
	}

	public Future getFuture(){
		return this.future ;
	}

}