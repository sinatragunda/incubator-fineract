/*

    Created by Sinatra Gunda
    At 6:49 AM on 9/27/2022

*/
package org.apache.fineract.helper;

import java.util.Optional;

public class OptionalHelper {

    public static boolean isPresent(Object object){
        return Optional.ofNullable(object).isPresent();
    }

    public static Object optionalOf(Object object ,Object orValue){
        return Optional.ofNullable(object).orElse(orValue);
    }
}
