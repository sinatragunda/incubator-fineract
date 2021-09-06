/*

    Created by Sinatra Gunda
    At 10:19 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.service;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;

import java.io.File;

public interface IAttachedMailSender {

    public SEND_MAIL_MESSAGE_STATUS sendMail(File file , EmailDetail emailDetail);
    public Long sleepTime();//sleep time in seconds ,you will have to multiply by 1000 to get total seconds
}
