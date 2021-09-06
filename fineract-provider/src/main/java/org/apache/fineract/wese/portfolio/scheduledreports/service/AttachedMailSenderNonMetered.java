/*

    Created by Sinatra Gunda
    At 10:25 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.service;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;

import java.io.File;

public class AttachedMailSenderNonMetered implements IAttachedMailSender {

    @Override
    public SEND_MAIL_MESSAGE_STATUS sendMail(File file , EmailDetail emailDetail) {

        return null ;

    }

    @Override
    public Long sleepTime(){
        return null;
    }
}
