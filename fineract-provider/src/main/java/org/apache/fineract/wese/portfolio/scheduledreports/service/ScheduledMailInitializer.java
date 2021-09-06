/*

    Created by Sinatra Gunda
    At 3:35 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.service;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.portfolio.client.domain.EmailRecipients;
import org.apache.fineract.portfolio.client.domain.EmailRecipientsKey;
import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.helper.ComparatorUtility;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.*;
import org.apache.fineract.wese.service.WeseEmailService;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ScheduledMailInitializer {

    private static ScheduledMailInitializer instance ;
    List<ScheduledSendableSession> scheduledSendableSessionList ;

    public static ScheduledMailInitializer getInstance() {
        // wait to carry on some optional test here
        return Optional.ofNullable(instance).orElseGet(ScheduledMailInitializer::new);
    }

    public ScheduledMailInitializer(){
        System.err.println("----------init new instance");
        scheduledSendableSessionList = new ArrayList<>();
    }

    public void addNewSession(WeseEmailService weseEmailService ,MailServerSettingsRepository mailServerSettingsRepository , ScheduledSendableSession scheduledSendableSession){

        scheduledSendableSessionList.add(scheduledSendableSession);
        IAttachedMailSender attachedMailSender = AttachedMailSenderFactory.createFactoryObject(weseEmailService, mailServerSettingsRepository);

        Runnable runnable = ()->{

            System.err.println("------------a session now here it will call other shit");

            System.err.println("---------start a new session here ------------");

            SendableReport sendableReport = scheduledSendableSession.getSendableReport();

            String sessionId = sendableReport.getSessionId();
            Queue<EmailRecipients> emailRecipientsQueue = scheduledSendableSession.getSendableReport().getEmailRecipientsQueue();
            PentahoReportGenerator pentahoReportGenerator = scheduledSendableSession.getSendableReport().getPentahoReportGenerator();

            String reportName = pentahoReportGenerator.getReportName();
            String description = String.format("Scheduled %s Report",reportName);

            //emailRecipientsQueue poll and iterate ,if quota reached return item back to queue
            for(;;){

                EmailRecipients emailRecipients = emailRecipientsQueue.poll();
                // if no more items in queue then processing is done return
                boolean isPresent = Optional.ofNullable(emailRecipients).isPresent();

                if(!isPresent){
                    // mark process as over
                    scheduledSendableSession.closeSession();
                    return ;
                }

                // for each item send and generate some report
                Long clientId = emailRecipients.getClientId();

                pentahoReportGenerator.updateQueryParams("R_clientId" ,clientId.toString());
                File file = pentahoReportGenerator.processReport();

                EmailDetail emailDetail = emailDetail(emailRecipients ,description);
                SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = attachedMailSender.sendMail(file,emailDetail);

                System.err.println("---------send mail status-------------"+sendMailMessageStatus);

                // File should be deleted to avoid duplicates being created
                file.delete();

                switch (sendMailMessageStatus){
                    case QOUTA_LIMIT:
                        emailRecipientsQueue.add(emailRecipients);
                        // sleep here we have reach quota
                        Long sleepTime = attachedMailSender.sleepTime();
                        sleepThread(sleepTime);
                        break;
                    default:
                        EmailSendStatus emailSendStatus = new EmailSendStatus(emailDetail ,sendMailMessageStatus);
                        //update some results here
                        scheduledSendableSession.updateResults(emailSendStatus);
                }

            }

            //attachedMailSender.sendMail();

        };

        System.err.println("-------------schedule thread externally-----------");
        Executor executor = Executors.newCachedThreadPool();
        executor.execute(runnable);
    }

    public EmailDetail emailDetail(EmailRecipients emailRecipients ,String body){

        String emailAddress = emailRecipients.getEmailAddress();
        String name = emailRecipients.getName();
        System.err.println("-----------------send email to ------------"+emailAddress);
        String subject = "Scheduled Report";

        EmailDetail emailDetail = new EmailDetail(subject ,body ,emailAddress ,name);
        return emailDetail;
    }

    public void closeSession(String sessionId){

        // flush to database here now
        //scheduledSendableSessionList.stream()


    }

    public void sleepThread(Long duration){

        Long secondsToSleep = 1000 * duration;
        try{
            System.err.println("--------------We are sleeping this thread now--------------");
            Thread.sleep(secondsToSleep);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public ScheduledMailSession getSessionResults(Long scheduledReportId){

        Predicate<ScheduledSendableSession> activeSession = (e)->{
            ScheduledMailSession scheduledMailSession = e.getScheduledMailSession();
            ScheduledReport scheduledReport = scheduledMailSession.getScheduledReport();
            Long id = scheduledReport.getId();
            boolean cmp = ComparatorUtility.compareLong(id ,scheduledReportId);
            return cmp ;
        };

        List<ScheduledSendableSession> scheduledSendableSessions = scheduledSendableSessionList.stream().filter(activeSession).collect(Collectors.toList());

        if(scheduledSendableSessions.isEmpty()){
            return null ;
        }

        ScheduledMailSession scheduledMailSession = scheduledSendableSessions.get(0).getScheduledMailSession();
        return scheduledMailSession ;
    }


}
