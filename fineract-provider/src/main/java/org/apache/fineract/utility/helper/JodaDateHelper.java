/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 12 April 2023 at 06:56
 */
package org.apache.fineract.utility.helper;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDate;

public class JodaDateHelper {
    

    public static String localDateToDefaultFormat(LocalDate localDate ,String pattern){

        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(pattern);
        String dateFormatted = localDate.toString(pattern);
        //System.err.println("-----------formatted joda date is "+dateFormatted);
        return dateFormatted ;
    }
}
