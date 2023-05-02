/*

    Created by Sinatra Gunda
    At 6:49 AM on 9/27/2022

*/
package org.apache.fineract.helper;

import java.util.Optional;

public class OptionalHelper {

    public static boolean isPresent(Object object){
        try {
            return Optional.ofNullable(object).isPresent();
        }
        catch (NullPointerException n){}
        return false;
    }

    public static boolean isNull(Object object){
        return !isPresent(object);
    }

    public static Boolean hasZeroValue(Number number){
        boolean has = isPresent(number);
        System.err.println("---------------------number value is -----------"+number+"========is present --"+has);
        if(has){
            System.err.println("------------------------------number equals 0 "+number.equals(0));
            return number.equals(0);
        }
        return has;
    }

    public static Boolean doesNotHaveZeroValue(Number number){
        return  !hasZeroValue(number);
    }

    public static <T> T optionalOf(T object ,T orValue){
        T value = null ;
        try {
            value = Optional.ofNullable(object).orElse(orValue);
        }
        catch (Exception n){
            System.err.println("----------------catch exception in optional class ,return of value "+orValue);
            value = orValue;
        }
        return value;
    }
}
