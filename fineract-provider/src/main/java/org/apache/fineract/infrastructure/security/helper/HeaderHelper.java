/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 00:57
 */
package org.apache.fineract.infrastructure.security.helper;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class HeaderHelper {

    public static Boolean hasHeader(HttpServletRequest httpServletRequest ,String value){
        Enumeration<String> headers = httpServletRequest.getHeaderNames();
        while (headers.hasMoreElements()){
            String arg = headers.nextElement();
            boolean same = arg.equalsIgnoreCase(value);
            if(same){
                return true ;
            }
        }
        return false ;
    }
}
