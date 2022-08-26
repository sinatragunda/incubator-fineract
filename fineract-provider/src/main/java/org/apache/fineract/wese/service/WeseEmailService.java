/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.wese.service;

import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import org.apache.fineract.infrastructure.configuration.data.SMTPCredentialsData;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


// added 16/07/2021
import org.apache.commons.mail.*;
import org.apache.fineract.infrastructure.core.service.PlatformEmailSendException;

// added 27/08/2021
import com.sun.mail.smtp.SMTPAddressFailedException;

import javax.mail.SendFailedException;
import com.sun.mail.smtp.SMTPSendFailedException ;

import java.net.UnknownHostException;
import java.util.Optional;

@Service
public class WeseEmailService{
	
	private final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService;
    
	@Autowired
	public WeseEmailService(final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService){
		this.externalServicesReadPlatformService = externalServicesReadPlatformService;
	}


    // added 01/03/2022
    public SEND_MAIL_MESSAGE_STATUS send(EmailDetail emailDetail){
        SEND_MAIL_MESSAGE_STATUS status = sendDefinedEmail(emailDetail,null);
        return status ;
    }

    public void send(String address, String subject, String body ,String contactName) {
	    final EmailDetail emailDetail = new EmailDetail(subject, body, address, contactName);
	    sendDefinedEmail(emailDetail);
    }

    public SEND_MAIL_MESSAGE_STATUS sendAttached(EmailDetail emailDetail ,String path){
        EmailAttachment emailAttachment = new EmailAttachment();
        emailAttachment.setPath(path);
        emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);

        String description = emailDetail.getBody();
        emailAttachment.setDescription(description);
        SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = sendDefinedEmail(emailDetail ,emailAttachment);
        return sendMailMessageStatus ;
    }

    public void sendDefinedEmail(EmailDetail emailDetails) {
        
        final Email email = new SimpleEmail();
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

            email.addTo(emailDetails.getAddress(), emailDetails.getContactName());
            email.send();
        } catch (EmailException e){
            //throw new PlatformEmailSendException(e);
        }
    }


    public SEND_MAIL_MESSAGE_STATUS sendDefinedEmail(EmailDetail emailDetails, EmailAttachment emailAttachment) {
        
        MultiPartEmail email = new MultiPartEmail();
        final SMTPCredentialsData smtpCredentialsData = this.externalServicesReadPlatformService.getSMTPCredentials();
        
        final String authuserName = smtpCredentialsData.getUsername();

        final String authuser = smtpCredentialsData.getUsername();
        final String authpwd = smtpCredentialsData.getPassword();

        // Very Important, Don't use email.setAuthentication()

        System.err.println("----------auth user is "+authuser+"------------and getPassword "+authpwd);

    
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

            boolean hasAttachment = Optional.ofNullable(emailAttachment).isPresent();
            
            // modified 01/03/2022
            if(hasAttachment){
                email.attach(emailAttachment);
            }

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
}