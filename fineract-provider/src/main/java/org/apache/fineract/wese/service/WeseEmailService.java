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


@Service
public class WeseEmailService{
	
	private final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService;
	
	@Autowired
	public WeseEmailService(final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService){
		this.externalServicesReadPlatformService = externalServicesReadPlatformService;
	}

    public void send(String address, String subject, String body ,String contactName) {
	    final EmailDetail emailDetail = new EmailDetail(subject, body, address, contactName);
	    sendDefinedEmail(emailDetail);
    }

    public void sendAttached(EmailDetail emailDetail ,String path ,String description){
        EmailAttachment emailAttachment = new EmailAttachment();
        emailAttachment.setPath(path);
        emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
        emailAttachment.setDescription(description);
        sendDefinedEmail(emailDetail ,emailAttachment);

    }

    public void sendDefinedEmail(EmailDetail emailDetails) {


        System.err.println("-------------sending defined email test ,simplistic----------");
        
        Email email = new SimpleEmail();
        final SMTPCredentialsData smtpCredentialsData = this.externalServicesReadPlatformService.getSMTPCredentials();
        final String authuserName = smtpCredentialsData.getUsername();

        final String authuser = smtpCredentialsData.getUsername();
        final String authpwd = smtpCredentialsData.getPassword();
        final String port  = smtpCredentialsData.getPort();
        // Very Important, Don't use email.setAuthentication()

        System.err.println("------auth user is -----------"+authuser);

        System.err.println("----------------------authpwd ------------- "+authpwd);


        System.err.println("-----------System port is -----------------"+port);

        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
        email.setDebug(false); // true if you want to debug
        email.setHostName(smtpCredentialsData.getHost());

        System.err.println("-----------set credendtials there =-------");

        email.setSmtpPort(Integer.valueOf(port));

        try {

            System.err.println("------------checking if is use tls -------------"+smtpCredentialsData.isUseTLS());
            if(smtpCredentialsData.isUseTLS()){
                email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
            }

            System.err.println("--------------------some set data -------------");


            email.setFrom(authuser, authuserName);

            email.setSubject(emailDetails.getSubject());

            System.err.println("----------------------------message set here now ---------------");


            email.setMsg(emailDetails.getBody());

            email.addTo(emailDetails.getAddress(), emailDetails.getContactName());


            System.err.println("----------------------------------add ------- to some shit ");

            email.send();

            System.err.println("-----------------why does it take long here? ---------------");
        } catch (EmailException e) {
            e.printStackTrace();
            throw new PlatformEmailSendException(e);
        }
    }


    public void sendDefinedEmail(EmailDetail emailDetails,EmailAttachment emailAttachment) {
        
        MultiPartEmail email = new MultiPartEmail();
        final SMTPCredentialsData smtpCredentialsData = this.externalServicesReadPlatformService.getSMTPCredentials();
        final String authuserName = smtpCredentialsData.getUsername();

        final String authuser = smtpCredentialsData.getUsername();
        final String authpwd = smtpCredentialsData.getPassword();

        // Very Important, Don't use email.setAuthentication()

    
        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
        email.setDebug(false); // true if you want to debug
        email.setHostName(smtpCredentialsData.getHost());

        try {
            if(smtpCredentialsData.isUseTLS()){
                email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
            }
            email.setFrom(authuser, authuserName);

            email.setSubject(emailDetails.getSubject());
            email.setMsg(emailDetails.getBody());
            email.attach(emailAttachment);
        
            email.addTo(emailDetails.getAddress(), emailDetails.getContactName());
            email.send();
        } catch (EmailException e) {

            System.err.println("---------------platform email exception ---------"+e.getMessage());
            throw new PlatformEmailSendException(e);
        }
    }
}