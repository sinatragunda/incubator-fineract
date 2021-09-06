/*

    Created by Sinatra Gunda
    At 10:12 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.service;

import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;
import org.apache.fineract.wese.service.WeseEmailService;

import java.util.Optional;
import java.util.function.Consumer;

public class AttachedMailSenderFactory {

    public static IAttachedMailSender createFactoryObject(WeseEmailService weseEmailService , MailServerSettingsRepository mailServerSettingsRepository){

        Long id = new Long(1);
        MailServerSettings mailServerSettings = mailServerSettingsRepository.findOne(id);

        IAttachedMailSender attachedMailSender = new AttachedMailSenderNonMetered(weseEmailService);

        if(mailServerSettings !=null){
            boolean isTimedServer = mailServerSettings.isTimedServer();
            if (isTimedServer){
                System.err.println("---------------------mail sender metered --------------");
                attachedMailSender = new AttachedMailSenderMetered(weseEmailService ,mailServerSettings);
            }
        }
        return attachedMailSender ;
    }
}
