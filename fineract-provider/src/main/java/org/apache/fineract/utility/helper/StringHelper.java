/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 April 2023 at 07:09
 */
package org.apache.fineract.utility.helper;

public class StringHelper {

    public static String stripSpaces(String arg){
        return  arg.replaceAll(" " ,"");
    }
}
