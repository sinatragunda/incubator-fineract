/*

    Created by Sinatra Gunda
    At 1:29 AM on 8/6/2021

*/
package org.apache.fineract.infrastructure.dataqueries.helper;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.infrastructure.dataqueries.repo.ScheduledReportRepository;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson ;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.SchedularWritePlatformService;
import org.apache.fineract.wese.helper.ReportsEmailHelper;
import org.apache.fineract.wese.service.WeseEmailService;
import org.mifosplatform.infrastructure.report.service.PentahoReportingProcessServiceImpl;

import javax.ws.rs.core.MultivaluedMap ;

public class ScheduledReportHelper {

    public static void createScheduledReport(ScheduledReportRepository scheduledReportRepository , FromJsonHelper fromJsonHelper , SchedularWritePlatformService schedularWritePlatformService, String apiRequestBody){

        JsonElement jsonElement = new JsonParser().parse(apiRequestBody);
        String reportName = fromJsonHelper.extractStringNamed("reportName" ,jsonElement);
        JsonObject jsonObject = fromJsonHelper.extractJsonObjectNamed("parameters" ,jsonElement);

        String parameters = fromJsonHelper.extractStringNamed("parameters" ,jsonElement);


        //String parameters = jsonObject.get("parameters").getAsString();


        JobName jobName = JobName.SCHEDULED_EMAIL_CLIENT_REPORTS;
        String name = jobName.name();
        String displayName = String.format("%s %s" ,reportName ,name);
        ScheduledJobDetail scheduledJobDetail = new ScheduledJobDetail(name ,displayName ,"0 0 0 1/1 * ? *'");
        schedularWritePlatformService.saveOrUpdate(scheduledJobDetail);

        System.err.println("-----------------does it bring id ? ---   "+scheduledJobDetail.getId());


        System.err.println("---------------------json string here is---------------"+parameters);

        Long jobId = scheduledJobDetail.getId();

        ScheduledReport scheduledReport = new ScheduledReport(reportName ,parameters ,jobId);
        scheduledReportRepository.save(scheduledReport);

    }

    public static Map<String ,String> reportParameters(ScheduledReportRepository scheduledReportRepository ,Long jobId){

        ScheduledReport scheduledReport = scheduledReportRepository.findByJobId(jobId);

        String parameters = scheduledReport.getParameters();
        String reportName = scheduledReport.getReportName();

        Map<String ,String> map = new HashMap();

        JsonElement jsonElement = new JsonParser().parse(parameters);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        map.put("reportName" ,reportName);

        Set<String> set = jsonObject.keySet();

        for(String key : set){
            String value = jsonObject.get(key).getAsString();
            map.put(key ,value);
        }

        return map;
    }

    public static void runScheduledMailReport(PentahoReportingProcessServiceImpl pentahoReportingProcessService , WeseEmailService weseEmailService , ScheduledReportRepository scheduledReportRepository, Long jobId){

        Map<String ,String> queryParams = reportParameters(scheduledReportRepository ,jobId);
        String reportName = queryParams.get("reportName");
       // queryParams = (MultivaluedMap<String, String>) c ;
        File file = pentahoReportingProcessService.processRequestEx(reportName , queryParams);
        ReportsEmailHelper.testSend(weseEmailService ,file.getPath() ,"Scheduled Client Reports");

    }
}
