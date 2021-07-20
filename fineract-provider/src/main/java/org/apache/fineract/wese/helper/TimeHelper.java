package org.apache.fineract.wese.helper ;


import java.util.Date;

public class TimeHelper{


	public static boolean sameMonth(Date timeA ,Date timeB){

		int year = timeA.getYear();
		int yearB = timeB.getYear();

		if(year != yearB){
			return false ;
		}

		int month = timeA.getMonth();
		int monthB = timeB.getMonth();

		if(month != monthB){
			return false ;
		}
		return true ;
	}


}