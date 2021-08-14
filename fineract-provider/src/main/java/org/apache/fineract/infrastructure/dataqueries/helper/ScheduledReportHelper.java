/*

    Created by Sinatra Gunda
    At 1:29 AM on 8/6/2021

*/
package org.apache.fineract.infrastructure.dataqueries.helper;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReportRepository;
import org.apache.fineract.infrastructure.dataqueries.service.ScheduledReportRepositoryWrapper;
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

    public static Long createScheduledReport(ScheduledReportRepositoryWrapper scheduledReportRepositoryWrapper , FromJsonHelper fromJsonHelper , SchedularWritePlatformService schedularWritePlatformService, String apiRequestBody){

        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(apiRequestBody, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String reportName = jsonObject.get("reportName").getAsString();
        String parameters = jsonObject.get("parameters").toString();
        String name = jsonObject.get("name").getAsString();

        JobName jobName = JobName.SCHEDULED_EMAIL_CLIENT_REPORTS;

        String jobNameStr = jobName.getCode();
        String displayName = String.format("%s %s" ,name ,jobNameStr);

        ScheduledJobDetail scheduledJobDetail = new ScheduledJobDetail(jobNameStr ,displayName ,"0 0 0 1/1 * ? *'");
        schedularWritePlatformService.saveOrUpdate(scheduledJobDetail);

        scheduledJobDetail.updateJobKey();
        schedularWritePlatformService.saveOrUpdate(scheduledJobDetail);

        Long jobId = scheduledJobDetail.getId();
        ScheduledReport scheduledReport = new ScheduledReport(reportName ,parameters ,jobId);
        scheduledReportRepositoryWrapper.saveOrUpdate(scheduledReport);
        return jobId ;

    }

    public static Map<String ,String> reportParameters(ScheduledReportRepositoryWrapper scheduledReportRepositoryWrapper ,Long jobId){

        ScheduledReport scheduledReport = scheduledReportRepositoryWrapper.findOneByJobId(jobId);

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

    public static void runScheduledMailReport(PentahoReportingProcessServiceImpl pentahoReportingProcessService , WeseEmailService weseEmailService , ScheduledReportRepositoryWrapper scheduledReportRepositoryWrapper, Long jobId){

        Map<String ,String> queryParams = reportParameters(scheduledReportRepositoryWrapper ,jobId);
        String reportName = queryParams.get("reportName");
        File file = pentahoReportingProcessService.processRequestEx(reportName , queryParams);


        /// we need to get list of recipients here as well the clients whose reports we need to send
        ReportsEmailHelper.testSend(weseEmailService ,file.getPath() ,"Scheduled Client Reports");

        file.delete();

        // do for all reciepients here ,if same file then delete if unique then delete

    }
}
