/*

    Created by Sinatra Gunda
    At 10:13 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.repo;

import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MailServerSettingsWrapper {

    private final MailServerSettingsRepository mailServerSettingsRepository;

    @Autowired
    public MailServerSettingsWrapper(MailServerSettingsRepository mailServerSettingsRepository) {
        this.mailServerSettingsRepository = mailServerSettingsRepository;
    }

    public MailServerSettings findOneWithNotFoundDetection(){

        MailServerSettings mailServerSettings = mailServerSettingsRepository.findOne(1L);
        boolean isPresent = Optional.ofNullable(mailServerSettings).isPresent();
        if(!isPresent){
            return null ;
        }

        return mailServerSettings;

    }
}
