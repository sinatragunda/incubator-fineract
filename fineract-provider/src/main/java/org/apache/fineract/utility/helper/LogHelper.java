/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 March 2023 at 03:42
 */
package org.apache.fineract.utility.helper;

import java.util.logging.Logger;

public class LogHelper {

    public static void log(String message){
        Logger.getGlobal().info("==============================="+message+"=============");
    }

    public static void info(String message){
        System.err.println("==============================="+message+"=============");
    }


}
