/*

    Created by Sinatra Gunda
    At 1:35 AM on 9/6/2022

*/
package org.apache.fineract.portfolio.mailserver.utility;

import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.portfolio.mailserver.service.MailService;

public class NonMeteredMailSender implements MailServerSender {

    @Override
    public void send(MailService mailService) {

        MailSenderQueueManager queueManager = MailSenderQueueManager.getInstance();
        boolean isQueueEmpty = queueManager.isQueueEmpty();
        /**
         * Design dilemna here ,what if one mail object creates a list of mail contents would they all get executed once ?
         * Lets first test this option of sending a mail at a time ,would have time issues now
         */

        System.err.println("-------------------non metered mail sender -------------");
        if(!isQueueEmpty){
            MailContent mailContent = queueManager.peekOrPoll(false);
            mailService.send(mailContent);
        }

    }
}
