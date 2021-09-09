/*

    Created by Sinatra Gunda
    At 1:29 AM on 8/6/2021

*/
package org.apache.fineract.infrastructure.dataqueries.helper;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.infrastructure.dataqueries.service.ScheduledReportRepositoryWrapper;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

import com.google.gson.Gson ;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.jobs.service.SchedularWritePlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.EmailRecipients;
import org.apache.fineract.portfolio.client.domain.EmailRecipientsKey;
import org.apache.fineract.portfolio.client.helper.EmailRecipientsHelper;
import org.apache.fineract.portfolio.client.repo.EmailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.EmailRecipientsRepository;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.spm.repository.EmailSendStatusRepository;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;
import org.apache.fineract.spm.repository.ScheduledMailSessionRepository;
import org.apache.fineract.wese.helper.ReportsEmailHelper;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.PentahoReportGenerator;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.ScheduledSendableSession;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.SendableReport;
import org.apache.fineract.wese.portfolio.scheduledreports.service.ScheduledMailInitializer;
import org.apache.fineract.wese.service.WeseEmailService;
import org.mifosplatform.infrastructure.report.service.PentahoReportingProcessServiceImpl;

import javax.ws.rs.core.MultivaluedMap ;


// Added 06/09/2021
import org.apache.fineract.wese.portfolio.scheduledreports.domain.ScheduledMailSession;



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

    public static void runScheduledMailReport(PentahoReportingProcessServiceImpl pentahoReportingProcessService , WeseEmailService weseEmailService , ScheduledReportRepositoryWrapper scheduledReportRepositoryWrapper, EmailRecipientsKeyRepository emailRecipientsKeyRepository , EmailRecipientsRepository emailRecipientsRepository , MailServerSettingsRepository mailServerSettingsRepository , ScheduledMailSessionRepository scheduledMailSessionRepository , EmailSendStatusRepository emailSendStatusRepository, ClientReadPlatformService clientReadPlatformService , ScheduledJobDetail scheduledJobDetail){

        Long jobId = scheduledJobDetail.getId();
        
        ScheduledReport scheduledReport = scheduledReportRepositoryWrapper.findOneByJobId(jobId);
       
        // updated 05/09/2021 ,to cater for new metered connection
        Consumer<ScheduledReport> setScheduledJobDetailConsumer = (e)->{
            scheduledReport.setScheduledJobDetail(scheduledJobDetail);
        };

        Optional.ofNullable(scheduledReport).ifPresent(setScheduledJobDetailConsumer);

        PentahoReportGenerator pentahoReportGenerator = new PentahoReportGenerator(pentahoReportingProcessService);
        Map<String ,String> queryParams = pentahoReportGenerator.reportParameters(scheduledReport);
        String reportName = pentahoReportGenerator.getReportName();

        Long recipientsKey = scheduledReport.getEmailRecipientsKey().getId();
        Queue<EmailRecipients> emailRecipientsQueue = EmailRecipientsHelper.emailRecipients(emailRecipientsKeyRepository ,emailRecipientsRepository  ,clientReadPlatformService ,recipientsKey);

        int recipientsCount = emailRecipientsQueue.size();
    

        String subject = "Scheduled Report";
        String description = String.format("Scheduled %s Report",reportName);

        SendableReport sendableReport = new SendableReport(pentahoReportGenerator ,emailRecipientsQueue);
        ScheduledMailSession scheduledMailSession = new ScheduledMailSession(scheduledReport);
        ScheduledSendableSession scheduledSendableSession = new ScheduledSendableSession(scheduledMailSession ,sendableReport);

        // update the total job count 
        scheduledMailSession.setTotalJobCount(recipientsCount);

        ScheduledMailInitializer.getInstance().addNewSession(weseEmailService ,mailServerSettingsRepository,scheduledMailSessionRepository ,emailSendStatusRepository ,scheduledSendableSession);
        
    }


    public static EmailDetail emailDetail(String email ,String contactName ,String subject,String description){
        EmailDetail emailDetail = new EmailDetail(subject ,description ,email ,contactName);
        return emailDetail;

    }

    public static String validateEmailAddressForClients(ClientReadPlatformService clientReadPlatformService, Long clientId){

        ClientData clientData = clientReadPlatformService.retrieveOne(clientId);
        Optional<String> emailAddress = Optional.ofNullable(clientData).map(ClientData::getEmailAddress);
        return emailAddress.get();

    }

    public static ScheduledMailSession scheduledMailSessionResults(ScheduledMailSessionRepository scheduledMailSessionRepository ,EmailSendStatusRepository emailSendStatusRepository, ScheduledReport scheduledReport){

        // get scheduling id here of running status and their results
        // get some results from static container here
        ScheduledMailSession scheduledMailSession = ScheduledMailInitializer.getInstance().getSessionResults(scheduledMailSessionRepository ,emailSendStatusRepository , scheduledReport);
        
        // if its null then result might have been flushed to database of which there could only be two off them right ? 
        return Optional.ofNullable(scheduledMailSession).orElseGet(ScheduledMailSession::new);
    }

}
