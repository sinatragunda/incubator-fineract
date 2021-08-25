/*

    Created by Sinatra Gunda
    At 5:46 PM on 8/24/2021

*/
package org.apache.fineract.infrastructure.bulkimport.importhandler.helper;

public class SanitizeExcelValues {

    public static String numericString(String value){

        String val = value.replace("#","");
        val = val.replaceAll("\\s+","");
        val = val.replace("\\","");
        return val ;
    }
}
