/*

    Created by Sinatra Gunda
    At 10:25 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.service;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.helper.ReportsEmailHelper;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.EmailSendStatus;
import org.apache.fineract.wese.service.WeseEmailService;

import java.io.File;
import java.time.Duration;

public class AttachedMailSenderNonMetered implements IAttachedMailSender {

    private WeseEmailService weseEmailService ;

    public AttachedMailSenderNonMetered(WeseEmailService weseEmailService){
        this.weseEmailService = weseEmailService ;
    }


    @Override
    public SEND_MAIL_MESSAGE_STATUS sendMail(File file , EmailDetail emailDetail) {

        System.err.println("-----------mail sender non metered---------");
            //send reports here son
        SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = ReportsEmailHelper.sendClientReport(weseEmailService ,emailDetail ,file.getPath());

        return sendMailMessageStatus ;
    }

    public Long sleepTime(){
        return null ;
    }
}
