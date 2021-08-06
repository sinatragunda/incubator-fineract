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
import java.util.Iterator;
import com.google.gson.Gson ;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonObject;

import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.SchedularWritePlatformService;
import org.apache.fineract.wese.helper.ReportsEmailHelper;
import org.apache.fineract.wese.service.WeseEmailService;
import org.mifosplatform.infrastructure.report.service.PentahoReportingProcessServiceImpl;

import javax.ws.rs.core.MultivaluedMap ;

public class ScheduledReportHelper {

    public static void createScheduledReport(ScheduledReportRepository scheduledReportRepository , FromJsonHelper fromJsonHelper , SchedularWritePlatformService schedularWritePlatformService, String apiRequestBody){

        Gson gson = new Gson();
        JsonElement jsonElement = gson.parse(apiRequestBody);

        String reportName = fromJsonHelper.extractStringNamed("reportName" ,jsonElement);
        JsonObject jsonObject = fromJsonHelper.extractJsonObjectNamed("parameters" ,jsonElement);

        String parameters = jsonObject.toString();


        JobName jobName = JobName.SCHEDULED_EMAIL_CLIENT_REPORTS;
        String name = jobName.name();
        String displayName = String.format("%s %s" ,reportName ,name);
        ScheduledJobDetail scheduledJobDetail = new ScheduledJobDetail(name ,displayName ,"0 0 0 1/1 * ? *'");
        schedularWritePlatformService.saveOrUpdate(scheduledJobDetail);


        System.err.println("-----------------does it bring id ? ---   "+scheduledJobDetail.getId());


        System.err.println("---------------------json string here is---------------"+parameters);

        ScheduledReport scheduledReport = new ScheduledReport(reportName ,parameters ,jobId);
        scheduledReportRepository.save(scheduledReport);

    }

    public static MultivaluedMap<String ,String> reportParameters(ScheduledReportRepository scheduledReportRepository ,Long jobId){

        ScheduledReport scheduledReport = scheduledReportRepository.findByJodbId(jobId);

        String parameters = scheduledReport.getParameters();
        String reportName = scheduledReport.getReportName();

        MultivaluedMap<String ,String> multiValuedMap = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();

        multiValuedMap.add("reportName" ,reportName);

        while (keys.hasNext()){
            String key = keys.next();
            String value = jsonObject.getString(key);
            multiValuedMap.add(key ,value);
            // this is to run it l guess
        }
        return multiValuedMap;

    }

    public static void runScheduledMailReport(PentahoReportingProcessServiceImpl pentahoReportingProcessService , WeseEmailService weseEmailService , ScheduledReportRepository scheduledReportRepository, Long jobId){

        MultivaluedMap<String ,String> queryParams = reportParameters(scheduledReportRepository ,jobId);
        String reportName = params.get("reportName");
        File file = pentahoReportingProcessService.processRequestEx(reportName ,queryParams);
        ReportsEmailHelper.testSend(weseEmailService ,file.getPath() ,"Scheduled Client Reports");

    }
}
