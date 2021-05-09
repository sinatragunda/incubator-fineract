package org.apache.fineract.wese.utility ;

import org.apache.fineract.wese.service.WeseSessionObject;
import java.util.ArrayList ;
import java.util.Iterator ;

/*
	added 01/31/2021 


*/

public class IndexedSession extends ArrayList<WeseSessionObject>{
	
	private long id ;
	private static IndexedSession instance =null;

	public static IndexedSession indexedSession(){
		if(instance ==null){
			instance = new IndexedSession();
		}
		return instance ;
	}

	public boolean add(WeseSessionObject weseSessionObject){
		return super.add(weseSessionObject);
	}

	public WeseSessionObject get(String id){

		Iterator iterator = iterator();
		while(iterator.hasNext()){
			WeseSessionObject w = (WeseSessionObject)iterator.next();
			if(id.equalsIgnoreCase(w.getSessionId())){
				return w ;
			}
		}
		return null ;

	}

}