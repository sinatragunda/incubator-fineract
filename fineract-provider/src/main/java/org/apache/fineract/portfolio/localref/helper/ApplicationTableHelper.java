/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 02 February 2023 at 12:48
 */
package org.apache.fineract.portfolio.localref.helper;

import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.apache.fineract.portfolio.localref.enumerations.APPLICATION_ACTION;

import java.util.Optional;

public class ApplicationTableHelper {

    public static DatatableData getTable(ReadWriteNonCoreDataService readWriteNonCoreDataService , String table , APPLICATION_ACTION applicationAction){

        applicationAction = Optional.ofNullable(applicationAction).orElse(APPLICATION_ACTION.LIST);
        DatatableData datatableData = null ;

        System.err.println("===================when is this called when creatonmg new app record ?");
        switch (applicationAction){
            default:
                datatableData = readWriteNonCoreDataService.retrieveDatatable(table);
                checkForException(datatableData);
                break;
        }
        return datatableData;

    }

    public static void checkForException(Object object){
        boolean isPresent = Optional.ofNullable(object).isPresent();
        if(!isPresent){
            throw new RuntimeException();
        }
    }
}
