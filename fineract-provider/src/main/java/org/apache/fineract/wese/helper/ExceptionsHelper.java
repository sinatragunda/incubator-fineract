/*

    Created by Sinatra Gunda
    At 10:33 AM on 10/1/2021

*/
package org.apache.fineract.wese.helper;


import java.util.Objects;

public class ExceptionsHelper {

    public static boolean isAnyNull(Object...  args){
        for(Object object : args){
            boolean isNull = Objects.isNull(object);
            if(isNull){
                //System.err.println("----------------value is null ");
                return true ;
            }
        }
        return false ;
    }
}
