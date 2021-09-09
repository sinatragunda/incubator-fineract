/*

    Created by Sinatra Gunda
    At 5:14 PM on 9/6/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.helper;

import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.spm.repository.EmailSendStatusRepository;
import org.apache.fineract.spm.repository.ScheduledMailSessionRepository;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.EmailSendStatus;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.ScheduledMailSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ScheduledMailSessionHelper {

    public static Consumer<EmailSendStatus> nullifyScheduledMailSession = (o)->{
        o.setScheduledMailSession(null);
    };

    public static void saveResults(ScheduledMailSessionRepository scheduledMailSessionRepository , EmailSendStatusRepository emailSendStatusRepository , ScheduledMailSession scheduledMailSession){

        System.err.println("------schedulued report is----------"+scheduledMailSession.getScheduledReport().getReportName());

        scheduledMailSessionRepository.save(scheduledMailSession);

        System.err.println("---------does it return it or ------"+scheduledMailSession.getId());

        List<EmailSendStatus> emailSendStatusList = scheduledMailSession.getActiveEmailSendStatusList();

        Consumer<EmailSendStatus> emailSendStatusPersistenceConsumer = (e)->{
            e.updateScheduledMailSession(scheduledMailSession);
            emailSendStatusRepository.save(e);
        };

        emailSendStatusList.stream().forEach(emailSendStatusPersistenceConsumer);

    }

    public static ScheduledMailSession activeAndPreviousSessions(ScheduledMailSessionRepository scheduledMailSessionRepository , EmailSendStatusRepository emailSendStatusRepository , ScheduledReport scheduledReport , boolean thereIsActiveSession) {

        Long scheduledReportId = scheduledReport.getId();

        List<ScheduledMailSession> scheduledMailSessionList = scheduledMailSessionRepository.findByScheduledReportId(scheduledReportId);


        System.err.println("---------------collecting from database now-------------------------"+scheduledMailSessionList.size());

        // we only want last two items since they are the most recent ,the others you can delete but so far no solution for deleting
        scheduledMailSessionList.stream().map(ScheduledMailSession::getId).sorted();


        // to avoid cylclic redundancies


        int size = scheduledMailSessionList.size();

        Consumer<ScheduledMailSession> addEmailSendStatusConsumer = (e) -> {

            Long scheduledMailSessionId = e.getId();

            System.err.println("----------------scheduled mail session id is -----------"+scheduledMailSessionId);
            List<EmailSendStatus> emailSendStatusList = emailSendStatusRepository.findByScheduledMailSessionId(scheduledMailSessionId);

            System.err.println("-----------------------emailSendStatus size is -------------"+emailSendStatusList.size());

            emailSendStatusList.stream().forEach(nullifyScheduledMailSession);
            e.setActiveEmailSendStatusList(emailSendStatusList);

        };

        List<ScheduledMailSession> sortedMailSessions = new ArrayList<>();

        if(thereIsActiveSession){
            return  previousSession(scheduledMailSessionList ,addEmailSendStatusConsumer , true);
        }

        if (size >= 2) {

            System.err.println("==============size greater or equal to 2===========");

            ScheduledMailSession activeMailSession = scheduledMailSessionList.get(size - 1);
            ScheduledMailSession previousMailSession = scheduledMailSessionList.get(size - 2);

            Optional.ofNullable(activeMailSession).ifPresent(addEmailSendStatusConsumer);
            Optional.ofNullable(previousMailSession).ifPresent(addEmailSendStatusConsumer);

            System.err.println("---------------sorted mail sessions size is ------------"+sortedMailSessions.size());

            List<EmailSendStatus> previousSendList = previousMailSession.getActiveEmailSendStatusList();

            activeMailSession.setPreviousEmailSendStatusList(previousSendList);

            System.err.println("-------------done setting sessions return now----------------");
            activeMailSession.setActive(true);
            //activeMailSession.setScheduledReport(scheduledReport);
            return activeMailSession;
        }

        if(size > 0){
            System.err.println("------------------size is also greater than 0 ,here we assume its only one item------------- "+size);
            scheduledMailSessionList.stream().forEach(addEmailSendStatusConsumer);
            ScheduledMailSession scheduledMailSession = scheduledMailSessionList.get(0);
            scheduledMailSession.setScheduledReport(scheduledReport);
            return scheduledMailSession;
        }

        System.err.println("--------------------all is vain return null ---------------");

        return null;

    }

    public static ScheduledMailSession previousSession(List<ScheduledMailSession> scheduledMailSessions ,Consumer<ScheduledMailSession> addEmailSendStatusConsumer, boolean hasActive){

        int size = scheduledMailSessions.size();
        if(size > 0){
            int index = 0 ;


            if(hasActive) {
                index = size - 1;

                System.err.println("-------------previous session index is "+index);
                // last item in array
                ScheduledMailSession scheduledMailSession = scheduledMailSessions.get(index);
                Optional.ofNullable(scheduledMailSession).ifPresent(addEmailSendStatusConsumer);
                return scheduledMailSession;
            }
        }
        return null ;
    }

}
