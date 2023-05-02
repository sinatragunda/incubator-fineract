/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 02 May 2023 at 12:33
 */
package org.apache.fineract.utility.helper;

import java.util.function.Function;

public class FunctionsHelper {

    public static Function convertToInteger  =(r)->{
        Integer val = null;
        try{
            String castedValue = String.valueOf(r);
            val = Integer.parseInt(castedValue);
        }
        catch (NumberFormatException n){
            n.printStackTrace();
        }
        return val ;
    };

    public static <T ,R> R execute(Function function,T arg){
        return (R)function.apply(arg);
    }
}
