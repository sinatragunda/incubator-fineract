/*

    Created by Sinatra Gunda
    At 2:09 AM on 9/6/2022

*/
package org.apache.fineract.portfolio.mailserver.api;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.portfolio.mailserver.enumerations.MAIL_CONTENT_TYPE;
import org.apache.fineract.portfolio.mailserver.service.MailServerSenderFactory;
import org.apache.fineract.portfolio.mailserver.service.MailService;
import org.apache.fineract.portfolio.mailserver.utility.MailContentFactory;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.apache.fineract.infrastructure.core.service.TenantDatabaseUpgradeService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.wese.helper.ObjectNodeHelper ;
import org.apache.fineract.infrastructure.security.service.*;


@Path("/mailservice")
@Component
@Scope("singleton")
public class MailServiceApiResource {

    private final MailService mailService ;
    private final MailServerSenderFactory mailServerSenderFactory;
    private final MailServerSettingsRepository mailServerSettingsRepository;

    @Autowired
    public MailServiceApiResource(MailService mailService, MailServerSenderFactory mailServerSenderFactory, MailServerSettingsRepository mailServerSettingsRepository) {
        this.mailService = mailService;
        this.mailServerSenderFactory = mailServerSenderFactory;
        this.mailServerSettingsRepository = mailServerSettingsRepository;
    }

    @Path("/test")
    @GET
    public String test(){

        MailContent mailContent = MailContentFactory.createMailContent(MAIL_CONTENT_TYPE.PLAIN ,null ,"treyviis@gmail.com" ,"Trevis Gunda","Mail Server Test" ,"Wese Mail server test");
        List<MailContent> mailContentList = new ArrayList<>(10);
        System.err.println("---------------initial size is "+mailContentList.size());
        for(int i = 0 ; i < 10 ;++i){
            mailContentList.add(mailContent);
        }

        mailContentList.stream().forEach(e->{

            System.err.println("---------lets send mail now -------------------");
            //mailService.send(e);
            mailServerSenderFactory.sendMail(e);
        });

        return "Test";
    }

}