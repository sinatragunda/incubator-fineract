/*

    Created by Sinatra Gunda
    At 12:28 AM on 9/6/2021

*/
package org.apache.fineract.wese.helper;

import java.math.BigDecimal;

public class ComparatorUtility {

    public static boolean compareLong(Long l ,Long r){
        int cmp = l.compareTo(r);
        return cmpToBoolean(cmp);
    }


    public static boolean cmpToBoolean(int cmp){
        if(cmp == 0){
            return true;
        }
        return false ;
    }

    // Added 04/10/2021 check if value is 0 then set null
    public static boolean longGreaterThanZero(Long l){
        int cmp = l.compareTo(0L);
        return cmpToBoolean(cmp);
    }

    // Added 08/10/2021
    public static boolean isBigDecimalZero(BigDecimal value){
        int cmp = value.compareTo(BigDecimal.ZERO);
        if(cmp <= 0){
            return true;
        }
        return false;
    }

    public static boolean compare(Object l ,Object r){
        return l.equals(r);
    }

    // Added 08/10/2021
    public static <T> boolean isObjectZero(Object value ,Object comparator){
        return value.equals(comparator);
    }


    // Added 15/10/2021
    public static boolean isLongZero(Long value) {
        int cmp = value.compareTo(0L);
        return cmpToBoolean(cmp);
    }


}
