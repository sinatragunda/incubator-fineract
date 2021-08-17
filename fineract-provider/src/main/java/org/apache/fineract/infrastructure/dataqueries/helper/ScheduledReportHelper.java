/*

    Created by Sinatra Gunda
    At 1:29 AM on 8/6/2021

*/
package org.apache.fineract.infrastructure.dataqueries.helper;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReportRepository;
import org.apache.fineract.infrastructure.dataqueries.service.ScheduledReportRepositoryWrapper;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson ;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.SchedularWritePlatformService;
import org.apache.fineract.portfolio.client.domain.EmailRecipients;
import org.apache.fineract.portfolio.client.domain.EmailRecipientsKey;
import org.apache.fineract.portfolio.client.helper.EmailRecipientsHelper;
import org.apache.fineract.portfolio.client.repo.EmailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.EmailRecipientsRepository;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
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
        Long id = jsonObject.get("emailRecipientsKey").getAsLong();

        EmailRecipientsKey emailRecipientKey = new EmailRecipientsKey(id);

        JobName jobName = JobName.SCHEDULED_EMAIL_CLIENT_REPORTS;

        String jobNameStr = jobName.getCode();
        String displayName = String.format("%s %s" ,name ,jobNameStr);

        ScheduledJobDetail scheduledJobDetail = new ScheduledJobDetail(jobNameStr ,displayName ,"0 0 0 1/1 * ? *'");
        schedularWritePlatformService.saveOrUpdate(scheduledJobDetail);

        scheduledJobDetail.updateJobKey();
        schedularWritePlatformService.saveOrUpdate(scheduledJobDetail);

        Long jobId = scheduledJobDetail.getId();
        ScheduledReport scheduledReport = new ScheduledReport(reportName ,parameters ,jobId);
        scheduledReport.setEmailRecipientsKey(emailRecipientKey);

        scheduledReportRepositoryWrapper.saveOrUpdate(scheduledReport);
        return jobId ;

    }

    public static Map<String ,String> reportParameters(ScheduledReport scheduledReport ,Long jobId){

       // ScheduledReport scheduledReport = scheduledReportRepositoryWrapper.findOneByJobId(jobId);
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

    public static void runScheduledMailReport(PentahoReportingProcessServiceImpl pentahoReportingProcessService , WeseEmailService weseEmailService , ScheduledReportRepositoryWrapper scheduledReportRepositoryWrapper, EmailRecipientsKeyRepository emailRecipientsKeyRepository , EmailRecipientsRepository emailRecipientsRepository , ClientReadPlatformService clientReadPlatformService , Long jobId){


        ScheduledReport scheduledReport = scheduledReportRepositoryWrapper.findOneByJobId(jobId);
        Map<String ,String> queryParams = reportParameters(scheduledReport ,jobId);

        String reportName = queryParams.get("reportName");
        Long recipientsKey = scheduledReport.getEmailRecipientsKey().getId();

        List<EmailRecipients> emailRecipientsList = EmailRecipientsHelper.emailRecipients(emailRecipientsKeyRepository ,emailRecipientsRepository  ,clientReadPlatformService ,recipientsKey);
        boolean clientReport = clientFacingReport(queryParams);

        System.err.println("---------------is client report ------------------"+clientReport);

        String subject = "Scheduled Report";
        String description = String.format("Scheduled %s Report",reportName);

        if(clientReport){

            emailRecipientsList.stream().forEach((e)->{

                // for each item send and generate some report
                String emailAddress = e.getEmailAddress();
                String name = e.getName();
                Long clientId = e.getClientId();
                queryParams.put("R_clientId" ,clientId.toString());

                File file = pentahoReportingProcessService.processRequestEx(reportName ,queryParams);

                EmailDetail emailDetail = emailDetail(emailAddress ,name ,subject ,description);
                ReportsEmailHelper.sendClientReport(weseEmailService ,emailDetail ,file.getPath() ,description);

                file.delete();
            });
            return;
        }

        File file = pentahoReportingProcessService.processRequestEx(reportName , queryParams);

        System.err.println("=================email recipients size is============= "+emailRecipientsList.size());

        emailRecipientsList.stream().forEach((e)->{
            /// we need to get list of recipients here as well the clients whose reports we need to send
            String emailAddress = e.getEmailAddress();
            String contactName = e.getName();

            System.err.println("-------------send mail "+emailAddress+" ----------- and name ---"+contactName);

            EmailDetail emailDetail =  new EmailDetail(subject,description ,emailAddress ,contactName);

            ReportsEmailHelper.sendClientReport(weseEmailService ,emailDetail ,file.getPath() ,description);
            //ReportsEmailHelper.testSend(weseEmailService,file.getPath() ,"This is some random reports test");
        });

        file.delete();
    }

    public static Boolean clientFacingReport(Map<String,String> queryParams){

        boolean has = queryParams.containsKey("R_clientId");
        return has ;
    }

    public static EmailDetail emailDetail(String email ,String contactName ,String subject,String description){
        EmailDetail emailDetail = new EmailDetail(subject ,description ,email ,contactName);
        return emailDetail;

    }
}
