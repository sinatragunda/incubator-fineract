/*

    Created by Sinatra Gunda
    At 8:45 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.service;

import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.portfolio.mailserver.repo.MailServerSettingsWrapper;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MailServerSenderFactory {

    private final MailService mailService;
    private final MailServerSettingsWrapper mailServerSettingsWrapper ;

    @Autowired
    public MailServerSenderFactory(MailService mailService, MailServerSettingsWrapper mailServerSettingsWrapper) {
        this.mailService = mailService;
        this.mailServerSettingsWrapper = mailServerSettingsWrapper;
    }
}
