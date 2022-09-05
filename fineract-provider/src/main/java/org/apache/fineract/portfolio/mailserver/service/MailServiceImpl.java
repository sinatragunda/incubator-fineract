/*

    Created by Sinatra Gunda
    At 10:18 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.service;

import org.apache.fineract.infrastructure.configuration.data.SMTPCredentialsData;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.portfolio.mailserver.enumerations.MAIL_CONTENT_TYPE;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.service.WeseEmailService;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.*;
import org.apache.fineract.infrastructure.core.service.PlatformEmailSendException;

// added 27/08/2021
import com.sun.mail.smtp.SMTPAddressFailedException;

import javax.mail.SendFailedException;
import com.sun.mail.smtp.SMTPSendFailedException ;

import java.net.UnknownHostException;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final WeseEmailService weseEmailService ;
    private final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService;

    @Autowired
    public
    MailServiceImpl(WeseEmailService weseEmailService, ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService) {
        this.weseEmailService = weseEmailService;
        this.externalServicesReadPlatformService = externalServicesReadPlatformService;
    }

    @Override
    public SEND_MAIL_MESSAGE_STATUS send(MailContent mailContent) {

        SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = sendDefinedEmail(mailContent);
        return sendMailMessageStatus;

    }

    public SEND_MAIL_MESSAGE_STATUS sendDefinedEmail(MailContent mailContent){

        EmailDetail emailDetails = mailContent.emailDetails();
        MAIL_CONTENT_TYPE mailContentType = mailContent.mailContentType();
        Email email = emailObject(mailContentType) ;

        final SMTPCredentialsData smtpCredentialsData = this.externalServicesReadPlatformService.getSMTPCredentials();

        final String authuserName = smtpCredentialsData.getUsername();
        final String authuser = smtpCredentialsData.getUsername();
        final String authpwd = smtpCredentialsData.getPassword();

        // Very Important, Don't use email.setAuthentication()

        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
        email.setDebug(true); // true if you want to debug
        email.setHostName(smtpCredentialsData.getHost());

        try {
            if(smtpCredentialsData.isUseTLS()){
                email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
            }

            email.setFrom(authuser, authuserName);

            email.setSubject(emailDetails.getSubject());
            email.setMsg(emailDetails.getBody());

            setAttachments(email ,mailContent);

            boolean hasAddress = Optional.ofNullable(emailDetails.getAddress()).isPresent();

            if(!hasAddress){
                return SEND_MAIL_MESSAGE_STATUS.INVALID_ADDRESS ;
            }

            email.addTo(emailDetails.getAddress(), emailDetails.getContactName());
            email.send();

            return SEND_MAIL_MESSAGE_STATUS.SUCCESS;

        }

        catch (Exception e){
            System.err.println("--------------mail error is -------"+e.getMessage());
            e.printStackTrace();
            Throwable throwable = e.getCause();
            boolean smtError = throwable instanceof SMTPSendFailedException;
            boolean sendFailed = throwable instanceof SendFailedException ;
            boolean unkownHost = throwable instanceof UnknownHostException ;
        }

        return SEND_MAIL_MESSAGE_STATUS.ERROR ;
    }


    private Email emailObject(MAIL_CONTENT_TYPE mailContentType){

        Email email = null ;
        switch (mailContentType){
            case PLAIN:
                email = new SimpleEmail();
                break;
            case MEDIA:
                email = new MultiPartEmail();
                break;
        }
        return email;
    }

    private void setAttachments(Email email , MailContent mailContent){
        MAIL_CONTENT_TYPE mailContentType = mailContent.mailContentType();
        switch (mailContentType){
            case MEDIA:
                List<File> attachments = mailContent.attachments();
                attachments.stream().forEach(e->{
                    //email.attach(e);
                });
                break;
        }
    }

}
