/*

    Created by Sinatra Gunda
    At 8:45 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.service;

import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.portfolio.mailserver.repo.MailServerSettingsWrapper;
import org.apache.fineract.portfolio.mailserver.utility.MailSenderQueueManager;
import org.apache.fineract.portfolio.mailserver.utility.MailServerSender;
import org.apache.fineract.portfolio.mailserver.utility.MeteredMailSender;
import org.apache.fineract.portfolio.mailserver.utility.NonMeteredMailSender;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MailServerSenderFactory {

    private final MailService mailService;
    private final MailServerSettingsWrapper mailServerSettingsWrapper ;

    @Autowired
    public MailServerSenderFactory(MailService mailService, MailServerSettingsWrapper mailServerSettingsWrapper) {
        this.mailService = mailService;
        this.mailServerSettingsWrapper = mailServerSettingsWrapper;
    }

    public void sendMail(MailContent mailContent){

        MailSenderQueueManager.getInstance().add(mailContent);
        MailServerSettings mailSettings = mailServerSettings();
        MailServerSender mailServerSender = mailServerSenderFactory(mailSettings);
        mailServerSender.send(mailService);
    }


    private MailServerSettings mailServerSettings(){
        MailServerSettings mailServerSettings = mailServerSettingsWrapper.findOneWithNotFoundDetection();
        return mailServerSettings;
    }

    private MailServerSender mailServerSenderFactory(MailServerSettings mailServerSettings){
        boolean isPresent = Optional.ofNullable(mailServerSettings).isPresent();
        MailServerSender mailServerSender = new NonMeteredMailSender();
        if(isPresent){
            mailServerSender = new MeteredMailSender(mailServerSettings);
        }
        return mailServerSender;
    }
}
