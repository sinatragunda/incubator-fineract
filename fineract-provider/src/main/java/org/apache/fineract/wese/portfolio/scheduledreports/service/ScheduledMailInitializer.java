/*

    Created by Sinatra Gunda
    At 3:35 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.service;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.portfolio.client.domain.MailRecipients;
import org.apache.fineract.spm.repository.EmailSendStatusRepository;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;
import org.apache.fineract.spm.repository.ScheduledMailSessionRepository;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.helper.ComparatorUtility;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.*;
import org.apache.fineract.wese.portfolio.scheduledreports.enumerations.ACTIVE_MAIL_SESSION_STATUS;
import org.apache.fineract.wese.portfolio.scheduledreports.helper.ScheduledMailSessionHelper;
import org.apache.fineract.wese.service.WeseEmailService;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ScheduledMailInitializer {

    private static ScheduledMailInitializer instance = null;
    List<ScheduledSendableSession> scheduledSendableSessionList ;

    public static ScheduledMailInitializer getInstance() {
        // wait to carry on some optional test here
        if(instance==null){
            instance = new ScheduledMailInitializer();
        }
        return instance ;
    }

    public ScheduledMailInitializer(){
        scheduledSendableSessionList = new ArrayList<>();
    }

    public void addNewSession(WeseEmailService weseEmailService ,MailServerSettingsRepository mailServerSettingsRepository ,ScheduledMailSessionRepository scheduledMailSessionRepository ,EmailSendStatusRepository emailSendStatusRepository,  ScheduledSendableSession scheduledSendableSession){

        scheduledSendableSessionList.add(scheduledSendableSession);

        IAttachedMailSender attachedMailSender = AttachedMailSenderFactory.createFactoryObject(weseEmailService, mailServerSettingsRepository);

        //Runnable runnable = ()->{

        //start session
        scheduledSendableSession.getScheduledMailSession().init();

        Queue<MailRecipients> mailRecipientsQueue = scheduledSendableSession.getSendableReport().getMailRecipientsQueue();

        PentahoReportGenerator pentahoReportGenerator = scheduledSendableSession.getSendableReport().getPentahoReportGenerator();

        String reportName = pentahoReportGenerator.getReportName();
        String description = String.format("Scheduled %s Report",reportName);
        scheduledSendableSession.getScheduledMailSession().setActiveMailSessionStatus(ACTIVE_MAIL_SESSION_STATUS.ACTIVE);

        ScheduledMailSession scheduledMailSession = scheduledSendableSession.getScheduledMailSession();
        ScheduledReport scheduledReport = scheduledMailSession.getScheduledReport();
        Map<String ,String> queryParams = pentahoReportGenerator.reportParameters(scheduledReport);
        boolean clientReport = pentahoReportGenerator.clientFacingReport(queryParams);

        //mailRecipientsQueue poll and iterate ,if quota reached return item back to queue
        for(;;){

            MailRecipients mailRecipients = mailRecipientsQueue.poll();
            // if no more items in queue then processing is done return
            boolean isPresent = Optional.ofNullable(mailRecipients).isPresent();
            if(!isPresent){
                // mark process as over
                closeSession(scheduledMailSessionRepository ,emailSendStatusRepository ,scheduledSendableSession);
                return ;
            }

            // for each item send and generate some report
            Long clientId = mailRecipients.getClientId();

            File file = null ;
            if(clientReport){

                pentahoReportGenerator.updateQueryParams("R_clientId" ,clientId.toString());
                file = pentahoReportGenerator.processReport();
            }
            else{
                file = pentahoReportGenerator.getReccuringFile();
            }

            EmailDetail emailDetail = emailDetail(mailRecipients,description);
            SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = attachedMailSender.sendMail(file,emailDetail);

            System.err.println("---------send mail status-------------"+sendMailMessageStatus);

            // File should be deleted to avoid duplicates being created ,but if its not client facing then keep it for the duration of the process
            if(clientReport){
                file.delete();
            }

            switch (sendMailMessageStatus){
                case QOUTA_LIMIT:
                    // return last polled item to queue ,either back or front but we think its back due to implementation
                    mailRecipientsQueue.add(mailRecipients);
                    updateScheduledSessionStatus(scheduledSendableSession , ACTIVE_MAIL_SESSION_STATUS.QUOTA_REACHED);
                    Long sleepTime = attachedMailSender.sleepTime();
                    sleepThread(sleepTime);
                    updateScheduledSessionStatus(scheduledSendableSession ,ACTIVE_MAIL_SESSION_STATUS.ACTIVE);
                    break;
                default:
                    EmailSendStatus emailSendStatus = new EmailSendStatus(emailDetail ,sendMailMessageStatus);
                    scheduledSendableSession.updateResults(emailSendStatus);
            }

        }

            //attachedMailSender.sendMail();

        //};


        //System.err.println("-------------schedule thread externally-----------");
        //ThreadCheat.raise(true) ;
        //Executor executor = Executors.newCachedThreadPool();
        //executor.execute(runnable);

        //Thread thread = new Thread(runnable);
        //thread.setDaemon(true);
        //thread.start();
        //ThreadCheat.raise(false) ;

    }

    public EmailDetail emailDetail(MailRecipients mailRecipients, String body){

        String emailAddress = mailRecipients.getEmailAddress();
        String name = mailRecipients.getName();
        System.err.println("-----------------send email to ------------"+emailAddress);
        String subject = "Scheduled Report";

        EmailDetail emailDetail = new EmailDetail(subject ,body ,emailAddress ,name);
        return emailDetail;
    }

    public void closeSession(ScheduledMailSessionRepository scheduledMailSessionRepository , EmailSendStatusRepository emailSendStatusRepository, ScheduledSendableSession scheduledSendableSession){

        // flush to database here now
        ScheduledMailSession scheduledMailSession = scheduledSendableSession.getScheduledMailSession();
        scheduledMailSession.closeSession();
        ScheduledMailSessionHelper.saveResults(scheduledMailSessionRepository ,emailSendStatusRepository ,scheduledMailSession);

    }

    public void updateScheduledSessionStatus(ScheduledSendableSession scheduledSendableSession , ACTIVE_MAIL_SESSION_STATUS activeMailSessionStatus){
        scheduledSendableSession.getScheduledMailSession().setActiveMailSessionStatus(activeMailSessionStatus);
    }

    public void sleepThread(Long duration){

        Long secondsToSleep = 1000 * duration;
        try{
            Thread.sleep(secondsToSleep);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public ScheduledMailSession getSessionResults(ScheduledMailSessionRepository scheduledMailSessionRepository ,EmailSendStatusRepository emailSendStatusRepository,ScheduledReport scheduledReport){

        Long scheduledReportId = scheduledReport.getId();
        Predicate<ScheduledSendableSession> activeSession = (e)->{
            ScheduledMailSession scheduledMailSession = e.getScheduledMailSession();
            ScheduledReport scheduledReport1 = scheduledMailSession.getScheduledReport();
            Long id = scheduledReport1.getId();
            boolean cmp = ComparatorUtility.compareLong(id ,scheduledReportId);
            return cmp ;
        };

        List<ScheduledSendableSession> scheduledSendableSessions = scheduledSendableSessionList.stream().filter(activeSession).collect(Collectors.toList());

        if(scheduledSendableSessions.isEmpty()){

            System.err.println("---------------------------------scheduledsendablesessions is empty son ,look for items in database-------------");
            // no active session here
            // so we getting results from database now son
            // so we should now collect results from the database
            ScheduledMailSession scheduledMailSession = ScheduledMailSessionHelper.activeAndPreviousSessions(scheduledMailSessionRepository ,emailSendStatusRepository ,scheduledReport ,false);
            return scheduledMailSession ;
        }


        ScheduledMailSession scheduledMailSession = scheduledSendableSessions.get(0).getScheduledMailSession();
        scheduledMailSession.setActive(true);

        // he we already have an active session right now lets hunt for one in database

        ScheduledMailSession previousMailSession = ScheduledMailSessionHelper.activeAndPreviousSessions(scheduledMailSessionRepository ,emailSendStatusRepository ,scheduledReport ,true);

        Consumer<ScheduledMailSession> setPrevious = (e)->{

            List<EmailSendStatus> previousSessionEmailSendStatusList = e.getActiveEmailSendStatusList();
            // nullify scheduled mail session reduncancies
            previousSessionEmailSendStatusList.stream().forEach(ScheduledMailSessionHelper.nullifyScheduledMailSession);
            scheduledMailSession.setPreviousEmailSendStatusList(previousSessionEmailSendStatusList);
        };

        Optional.ofNullable(previousMailSession).ifPresent(setPrevious);
        return scheduledMailSession ;
    }


}
