package org.apache.fineract.wese.helper ;


import java.time.*;
import java.util.Date;
import java.util.Optional;


// Added 02/01/2022
import org.joda.time.DateTime ;

public class TimeHelper{

	// start date to mark the start of a month 
	public static Date startDate(Long transactionDateEpoch){
		
		Date date[] = {dateNow()};

		// added 28/12/2021
		Optional.ofNullable(transactionDateEpoch).ifPresent(e->{
			date[0] = fromEpoch(transactionDateEpoch);
		});

		date[0].setDate(1); 
		return date[0] ;
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

	// Added 28/09/2021
	public static org.joda.time.LocalDate javaDateToJodaLocalDate(Date date){

		Long epoch  = date.getTime();
		DateTime dateTime = new DateTime(epoch);
		org.joda.time.LocalDate localDate = dateTime.toLocalDate();
		return localDate ;
	}

	// Added 03/01/2022
	public static Long jodaLocalDateToEpoch(org.joda.time.LocalDate localDate){

		int year = localDate.getYear();
		int month = localDate.getMonthOfYear();
		int day = localDate.getDayOfMonth();

		LocalDate javaLocalDate = LocalDate.of(year ,month ,day);
		Instant instant = javaLocalDate.atStartOfDay().toInstant(ZoneOffset.UTC);
		Long epoch = instant.getEpochSecond();
		return epoch;
	}

	// Added 03/01/2021
	public static Date fromEpoch(Long epoch){
		Instant instant = Instant.ofEpochSecond(epoch);
		return Date.from(instant);
	}







}