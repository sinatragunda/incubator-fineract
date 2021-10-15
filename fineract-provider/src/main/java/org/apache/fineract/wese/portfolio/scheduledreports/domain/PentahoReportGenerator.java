/*

    Created by Sinatra Gunda
    At 2:26 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.domain;

import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.mifosplatform.infrastructure.report.service.PentahoReportingProcessServiceImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map ;
import java.util.Set;


import com.google.gson.Gson ;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PentahoReportGenerator {

    private String reportName ;
    private Map queryParams ;
    private File file = null;
    private PentahoReportingProcessServiceImpl pentahoReportingProcessService;


    public PentahoReportGenerator(PentahoReportingProcessServiceImpl pentahoReportingProcessService){
        this.queryParams = new HashMap();
        this.pentahoReportingProcessService = pentahoReportingProcessService;
    }


    public Boolean clientFacingReport(Map<String,String> queryParams){
        boolean has = queryParams.containsKey("R_clientId");
        return has ;
    }

    public Map<String ,String> reportParameters(ScheduledReport scheduledReport){

        String parameters = scheduledReport.getParameters();
        this.reportName = scheduledReport.getReportName();

        JsonElement jsonElement = new JsonParser().parse(parameters);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        queryParams.put("reportName" ,reportName);
        Set<String> set = jsonObject.keySet();

        for(String key : set){
            String value = jsonObject.get(key).getAsString();
            queryParams.put(key ,value);
        }
        return queryParams;
    }

    public void updateQueryParams(String key ,String value){
        queryParams.put(key ,value);
    }

    public String getReportName() {
        return reportName;
    }

    public File processReport(){
        File file = pentahoReportingProcessService.processRequestEx(reportName ,queryParams);
        return file;
    }

    // Reccuring is a non client facing file ,since only one of it is needed its safe to just not delete it
    public File getReccuringFile(){
        if(file==null){
            //System.err.println("-----------------creating new non client file ------------");
            file = processReport();
        }
        return file ;
    }
}


