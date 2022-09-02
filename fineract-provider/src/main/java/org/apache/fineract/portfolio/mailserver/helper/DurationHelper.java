/*

    Created by Sinatra Gunda
    At 8:00 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.helper;

import org.apache.fineract.wese.enumerations.DURATION_TYPE;

import java.time.Duration;

public class DurationHelper {

    public static Long duration(Duration duration ,DURATION_TYPE durationType){

        Long durationSoFar = 0L;

        switch (durationType){
            case HOURLY:
                durationSoFar = duration.toHours();
                break;
            case DAILY:
                durationSoFar = duration.toDays();
                break;
            case MINUTES:
                durationSoFar = duration.toMinutes();
                break;
        }
        return durationSoFar;
    }
}
