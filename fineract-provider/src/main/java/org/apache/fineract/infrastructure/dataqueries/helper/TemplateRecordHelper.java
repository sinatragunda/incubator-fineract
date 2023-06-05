/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 February 2023 at 10:32
 */
package org.apache.fineract.infrastructure.dataqueries.helper;

import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.infrastructure.dataqueries.enumerations.DATA_TABLE_CATEGORY;
import org.apache.fineract.infrastructure.dataqueries.service.ServiceAdapter;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.utility.domain.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemplateRecordHelper {

    /**
     * Added 19/02/2023 at 0409
     * Intend to get list of data of corresponding application
     */
    public static void templateRecords(ServiceAdapter serviceAdapter, DatatableData dataTable){

        String applicationTableName = dataTable.getApplicationTableName();

        //System.err.println("==================print table application name "+applicationTableName);

        List<? extends Record> recordList = new ArrayList<>();
        DATA_TABLE_CATEGORY tableCategory = DATA_TABLE_CATEGORY.fromString(applicationTableName);
        switch(tableCategory){
            case LOAN:
                recordList = serviceAdapter.getLoanReadPlatformService().retrieveAll(null).getPageItems();
                break;
            case CLIENT:
                recordList = serviceAdapter.getClientReadPlatformService().retrieveAll(null).getPageItems();
                break;
        }

        //System.err.println("================record size is "+recordList.size()+"=========whsts this effectively final thing");

        List<? extends Record> finalRecordList = recordList;

        Optional.ofNullable(dataTable).ifPresent(e->{
            e.setRecordList(finalRecordList);
        });
    }
}
