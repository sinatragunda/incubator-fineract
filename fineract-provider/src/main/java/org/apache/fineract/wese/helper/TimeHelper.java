package org.apache.fineract.wese.helper ;


import java.util.Date;
import java.time.Instant ;
import java.time.LocalDate;

public class TimeHelper{



	// start date to mark the start of a month 
	public static Date startDate(){
		Date date = dateNow();
		System.err.println("-------------first date is------------"+date.toString());
		date.setDate(1); 
		System.err.println("-------------updated date is------------"+date.toString());
		return date ;
	}


    public static Date dateNow(){
        Instant instant = Instant.now();
        Date date = Date.from(instant);
        return date ;
    }


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

	public static Date jodaLocalDateToJavaDate(Long epoch){
		Instant instant = Instant.ofEpochMilli(epoch);
		return Date.from(instant);
	}


}