/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 November 2022 at 20:23
 */
package org.apache.fineract.portfolio.remittance.helper;

import org.apache.fineract.wese.helper.TimeHelper;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

public class RxDealKeyHelper {

    public static String generateKey(String currencyCode){

        long timeNow = Instant.now().getEpochSecond();

        Random random = new Random(timeNow);

        long key = random.nextLong();

        if(key < 0){
            key = key * (-1);
        }

        String strip = String.valueOf(key);
        String fiveKey = strip.substring(0 ,5);

        Date date = TimeHelper.fromEpoch(timeNow);
        String fundsKey = String.format("%d%d%s%s",date.getDate(),date.getYear() ,currencyCode,fiveKey);
        return fundsKey ;
    }



}
