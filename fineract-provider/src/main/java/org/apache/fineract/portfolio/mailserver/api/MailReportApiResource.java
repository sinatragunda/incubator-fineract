/*

    Created by Sinatra Gunda
    At 3:06 AM on 9/6/2022

*/
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

package org.apache.fineract.portfolio.mailserver.api;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.notification.helper.MailRecipientsHelper;
import org.apache.fineract.notification.repo.MailRecipientsRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.MailRecipients;
import org.apache.fineract.portfolio.client.repo.MailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.MailRecipientsRepository;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.portfolio.mailserver.enumerations.MAIL_CONTENT_TYPE;
import org.apache.fineract.portfolio.mailserver.helper.MailReportHelper;
import org.apache.fineract.portfolio.mailserver.service.MailServerSenderFactory;
import org.apache.fineract.portfolio.mailserver.service.MailService;
import org.apache.fineract.portfolio.mailserver.utility.MailContentFactory;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.mifosplatform.infrastructure.report.service.PentahoReportingProcessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.apache.fineract.infrastructure.core.service.TenantDatabaseUpgradeService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.wese.helper.ObjectNodeHelper ;
import org.apache.fineract.infrastructure.security.service.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Path("/mailreport")
@Component
@Scope("singleton")
public class MailReportApiResource {

    private final PlatformSecurityContext platformSecurityContext;
    private final MailService mailService ;
    private final MailServerSenderFactory mailServerSenderFactory;
    private final MailServerSettingsRepository mailServerSettingsRepository;
    private final PentahoReportingProcessServiceImpl pentahoReportingProcessService;
    private final FromJsonHelper fromJsonHelper;
    private final MailRecipientsRepositoryWrapper mailRecipientsRepositoryWrapper;
    private final MailRecipientsKeyRepository mailRecipientsKeyRepository ;
    private final MailRecipientsRepository mailRecipientsRepository;
    private final ClientReadPlatformService clientReadPlatformService;

    @Autowired
    public MailReportApiResource(PlatformSecurityContext platformSecurityContext, MailService mailService, MailServerSenderFactory mailServerSenderFactory, MailServerSettingsRepository mailServerSettingsRepository, PentahoReportingProcessServiceImpl pentahoReportingProcessService, FromJsonHelper fromJsonHelper, MailRecipientsRepositoryWrapper mailRecipientsRepositoryWrapper, MailRecipientsKeyRepository mailRecipientsKeyRepository, MailRecipientsRepository mailRecipientsRepository, ClientReadPlatformService clientReadPlatformService) {
        this.platformSecurityContext = platformSecurityContext;
        this.mailService = mailService;
        this.mailServerSenderFactory = mailServerSenderFactory;
        this.mailServerSettingsRepository = mailServerSettingsRepository;
        this.pentahoReportingProcessService = pentahoReportingProcessService;
        this.fromJsonHelper = fromJsonHelper;
        this.mailRecipientsRepositoryWrapper = mailRecipientsRepositoryWrapper;
        this.mailRecipientsKeyRepository = mailRecipientsKeyRepository;
        this.mailRecipientsRepository = mailRecipientsRepository;
        this.clientReadPlatformService = clientReadPlatformService;
    }

    @POST
    public String send(String apiBody){

        this.platformSecurityContext.authenticatedUser();

        JsonElement jsonElement = fromJsonHelper.parse(apiBody);
        JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,apiBody);

        List<String> emailAddressList = new ArrayList<>();

        boolean includePrimaryRecipient = jsonCommand.booleanObjectValueOfParameterNamed("primaryRecipient");

        String primaryRecipient = jsonCommand.stringValueOfParameterNamed("emailAddress");

        System.err.println("-----------primary recipient ? "+includePrimaryRecipient);

        Optional.ofNullable(primaryRecipient).ifPresent(e->{

            if(includePrimaryRecipient) {
                emailAddressList.add(primaryRecipient);
            }
        });

        String reportName = jsonCommand.stringValueOfParameterNamed("reportName");

        System.err.println("--------------------report Name is "+reportName+"=============");

        String subject ="Core Banking Report Mail";
        String body = String.format("%s mailing",reportName);

        JsonArray externalRecipientsJsonArray = fromJsonHelper.extractJsonArrayNamed("externalRecipients",jsonElement);

        JsonArray mailRecipientsJsonArray = fromJsonHelper.extractJsonArrayNamed("mailRecipients" ,jsonElement);

        Iterator<JsonElement> externalRecipientIterator = externalRecipientsJsonArray.iterator();

        while(externalRecipientIterator.hasNext()){

            JsonElement jsonElement1 = externalRecipientIterator.next();
            String emailAddress  = jsonElement1.getAsString();
            System.err.println("-------------email address is ----------------------"+emailAddress);
            emailAddressList.add(emailAddress);
        }

        List<EmailDetail> emailDetailList = new ArrayList<>();

        emailAddressList.stream().forEach(e->{
            String emailAddress = e ;
            System.err.println("-----------------lets attach this email ------------"+emailAddress);
            EmailDetail emailDetail = new EmailDetail(subject,body ,emailAddress ,"Client");
            emailDetailList.add(emailDetail);
        });

        Iterator<JsonElement> mailRecipientsIterator = mailRecipientsJsonArray.iterator();
        Queue<MailRecipients> mailRecipientsQueue = new LinkedList<>();

        emailAddressList.stream().forEach(e->{
           // will get back to this and reconfigure it
        });

        while(mailRecipientsIterator.hasNext()){

            JsonElement jsonElement1 = mailRecipientsIterator.next();
            //System.err.println("-------------mail recipients keys "+jsonElement1);

            Long keyId = fromJsonHelper.extractLongNamed("id" ,jsonElement1);

            //System.err.println("-----------------key id is "+keyId);
            Queue<MailRecipients> queue = MailRecipientsHelper.emailRecipients(mailRecipientsKeyRepository ,mailRecipientsRepository ,clientReadPlatformService ,keyId);
            mailRecipientsQueue.addAll(queue);

        }

        JsonObject reportParamsJson = fromJsonHelper.extractJsonObjectNamed("reportParams",jsonElement);

        Set<String> keys = reportParamsJson.keySet();

        Map<String ,String> params = new HashMap<>();
        params.put("output-type","PDF");
        params.put("locale","en");
        params.put("tenantIdentifier", ThreadLocalContextUtil.getTenant().getTenantIdentifier());

        for(String key : keys){

            JsonElement jsonElement1 = reportParamsJson.get(key);
            String value = jsonElement1.getAsString();
            //System.err.println("-------------------------key is -----------"+key+"-------------------value is "+value);
            params.put(key ,value);
        }

        //System.err.println("----------------------output params are "+params.toString());

        File file = pentahoReportingProcessService.processRequestEx(reportName ,params);

        //System.err.println("--so we dont here ? --------------------");

        Consumer<MailRecipients> toEmailDetail = (e)->{

            String emailAddress = e.getEmailAddress();
            String contactName = e.getName();
            //System.err.println("-----------------mail recipient ------"+emailAddress+"--------------with contanct name ---"+contactName);

            EmailDetail emailDetail = new EmailDetail(subject ,body ,emailAddress,contactName) ;

            System.err.println("----------------------emailDetail -----------------"+emailDetail);
            emailDetailList.add(emailDetail);
        };

        mailRecipientsQueue.stream().forEach(toEmailDetail);

        List<File> attachments = Arrays.asList(file);
        List<MailContent> mailContentList = new ArrayList<>();

        System.err.println("------------------------------filename is --------------"+file.getName());

        Consumer<EmailDetail> toMailContent = (e)->{
            MailContent mailContent = MailContentFactory.createMailContent(MAIL_CONTENT_TYPE.MEDIA ,attachments ,e);
            mailContentList.add(mailContent);
        };

        emailDetailList.stream().forEach(toMailContent);

        /**
         * From MailContentList then lets add to MailServerSenderFactory
         * This class then handles everything ,the queeing etc
         */

        System.err.println("---------------------total mail contents are ------------"+mailContentList.size());

        mailContentList.stream().forEach(e->{
            mailServerSenderFactory.sendMail(e);
        });

        return ObjectNodeHelper.statusNode(true).toString();
    }

}
