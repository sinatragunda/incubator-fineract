/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 01 June 2023 at 00:57
 */
package org.apache.fineract.infrastructure.generic.helper;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;

import java.util.function.Consumer;
import java.util.List ;

public class DataValidationHelper {

    private static Consumer<ApiParameterError> printErrors = (e)->{
        System.err.println("--------Developer message "+e.getDeveloperMessage());
    };

    public static void printError(List<ApiParameterError> apiParameterErrorList){
        apiParameterErrorList.stream().forEach(printErrors);
    }
}
