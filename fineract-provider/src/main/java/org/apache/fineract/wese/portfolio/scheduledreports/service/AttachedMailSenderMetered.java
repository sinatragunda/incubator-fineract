/*

    Created by Sinatra Gunda
    At 10:26 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.service;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.wese.enumerations.DURATION_TYPE;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.helper.ReportsEmailHelper;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.EmailSendStatus;
import org.apache.fineract.wese.service.WeseEmailService;

import java.io.File;
import java.time.Duration;

public class AttachedMailSenderMetered implements IAttachedMailSender {

    private MailServerSettings mailServerSettings ;
    private static int limitCount = 0 ;
    private int limit = 0 ;
    private int quotaDuration ;
    private DURATION_TYPE durationType ;
    private WeseEmailService weseEmailService;

    public AttachedMailSenderMetered(WeseEmailService weseEmailService , MailServerSettings mailServerSettings){

        this.weseEmailService = weseEmailService;
        this.mailServerSettings = mailServerSettings ;
        this.limit = mailServerSettings.getLimit();
        this.quotaDuration = mailServerSettings.getQuotaDuration();
        this.durationType = DURATION_TYPE.fromInt(mailServerSettings.getTimerType());
    }

    public synchronized void updateLimit(){
        ++limitCount ;
    }

    public synchronized boolean isWithinQuota(){

        // 10 > 10
        if(limitCount >= limit){
            //System.err.println("------------limit already reach to stop auto sleep lets reset limitCount now---------");
            limitCount = 0 ;
            return false ;
        }
        return true ;
    }

    @Override
    public SEND_MAIL_MESSAGE_STATUS sendMail(File file , EmailDetail emailDetail) {

        boolean isWithinQuota = isWithinQuota();
        EmailSendStatus emailSendStatus = null ;

        // guard against all other threads continuing
        if(isWithinQuota){

            //send reports here son
            SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = ReportsEmailHelper.sendClientReport(weseEmailService ,emailDetail ,file.getPath());
            updateLimit();
            return sendMailMessageStatus;
        }

        return SEND_MAIL_MESSAGE_STATUS.QOUTA_LIMIT;
        //if successfull then update schedule mail session
    }

    public Long sleepTime(){
        //sleep for how long now ? ,from now sleep x amount of time

        Duration duration = null ;
        switch (durationType){
            case MINUTES:
                duration = Duration.ofMinutes(quotaDuration);
                break;
            case DAILY:
                duration = Duration.ofDays(quotaDuration);
                break;
            case HOURLY:
                duration = Duration.ofHours(quotaDuration);
                break;
        }

        System.err.println("--------time of duration to wake up is "+duration.getSeconds());

        Long sleepTime = duration.getSeconds();
        return sleepTime;
    }
}
