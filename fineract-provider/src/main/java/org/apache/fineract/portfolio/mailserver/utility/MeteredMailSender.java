/*

    Created by Sinatra Gunda
    At 1:41 AM on 9/6/2022

*/
package org.apache.fineract.portfolio.mailserver.utility;

import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.portfolio.mailserver.service.MailService;

public class MeteredMailSender implements MailServerSender {

    private MailServerSettings mailServerSettings ;

    public MeteredMailSender(MailServerSettings mailServerSettings){
        this.mailServerSettings = mailServerSettings;
    }

    @Override
    public void send(MailService mailService) {

        MailSenderQueueManager queueManager = MailSenderQueueManager.getInstance();
        boolean isQueueEmpty = queueManager.isQueueEmpty();

        /**
         * This will have many design problems since this will be executed in a thread
         */
        if(!isQueueEmpty){
            MailServerState.getInstance(mailServerSettings).trySend(mailService);
        }

    }
}
