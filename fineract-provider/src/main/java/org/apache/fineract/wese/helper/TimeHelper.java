package org.apache.fineract.wese.helper ;


import java.time.Duration;
import java.util.Date;
import java.time.Instant ;
import java.time.LocalDate;

public class TimeHelper{



	// start date to mark the start of a month 
	public static Date startDate(){
		Date date = dateNow();
		date.setDate(1); 
		return date ;
	}


    public static Date dateNow(){
        Instant instant = Instant.now();
        Date date = Date.from(instant);
        return date ;
    }

    public static int periodDuration(Date start ,Date end){

		Duration duration = Duration.between(start.toInstant() ,end.toInstant());
    	Long daysBetween = duration.toDays();

    	int months = daysBetween.intValue() / 30 ;
    	return months;
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

	// Added 28/09/2021
	public static org.joda.time.LocalDate jodaLocalDateNow(){
		org.joda.time.LocalDate now = org.joda.time.LocalDate.now();
		return now ;
	}


}