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
package org.apache.fineract.wese.api;

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
import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.apache.fineract.infrastructure.core.service.TenantDatabaseUpgradeService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.wese.helper.ObjectNodeHelper ;
import org.apache.fineract.infrastructure.security.service.*;


@Path("/weseservice")
@Component
@Scope("singleton")
public class WeseApiResource {

    private final TenantDatabaseUpgradeService tenantDatabaseUpgradeService; 
    private final JdbcTemplate jdbcTemplate ;
    private final TenantDetailsService tenantDetailsService ;
    private final MailServerSettingsRepository mailServerSettingsRepository;

    @Autowired
    public WeseApiResource(final TenantDatabaseUpgradeService tenantDatabaseUpgradeService ,final RoutingDataSource routingDataSource ,final TenantDetailsService tenantDetailsService ,final MailServerSettingsRepository mailServerSettingsRepository) {
        this.tenantDatabaseUpgradeService = tenantDatabaseUpgradeService ;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.tenantDetailsService = tenantDetailsService ;
        this.mailServerSettingsRepository = mailServerSettingsRepository;
    }

    @Path("/newtenant")
    @GET
    public ObjectNode createNewTenant(@QueryParam("tenantIdentifier") String tenantIdentifier, @QueryParam("timezone")String timezone){

        //NewTenantHelper.createNewTenant(fineractPlatformTenant, jdbcTemplate ,tenantIdentifier ,timezone);

        tenantDetailsService.createTenant(tenantIdentifier,timezone);
        
        boolean status = tenantDatabaseUpgradeService.upgradeTenant(tenantIdentifier);
        
        String message = null ;
        if(status){
            message = String.format("You have successfully created a new tenant %s .To login use these credentials : Username = wesecbs and Password = ",tenantIdentifier);
        }
        return ObjectNodeHelper.statusNode(status).put("message",message);
    
    }
    @Path("/version")
    @GET
    public String getVersion(){
        return new String("16.0.0");
    }

    @Path("/mailserversettings")
    @POST
    public String createMailServerSettings(String apiBody){

        final MailServerSettings mailServerSettings = MailServerSettings.fromHttpResponse(apiBody);
        ObjectNode objectNode = ObjectNodeHelper.statusNode(false);
        Consumer<MailServerSettings> saveConsumer = (e)->{
            mailServerSettingsRepository.save(mailServerSettings);
            objectNode.put("status",true);
        };

        Optional.ofNullable(mailServerSettings).ifPresent(saveConsumer);
        return objectNode.toString();
    }

    @Path("/mailserversettings")
    @PUT
    public String editMailServerSettings(String apiBody){

        MailServerSettings mailServerSettings = MailServerSettings.fromHttpResponse(apiBody);
        //mailServerSettings.setId(new Long(1));

        ObjectNode objectNode = ObjectNodeHelper.statusNode(false);
        Consumer<MailServerSettings> saveConsumer = (e)->{
            mailServerSettingsRepository.save(mailServerSettings);
            objectNode.put("status",true);
        };

        Optional.ofNullable(mailServerSettings).ifPresent(saveConsumer);
        return objectNode.toString();
    }



    @Path("/mailserversettings")
    @GET
    public String getMailServerSettings(){

        ObjectNode objectNode = ObjectNodeHelper.statusNode(false);
        Consumer<MailServerSettings> mailServerSettingsConsumer = (e)->{

            objectNode.put("status",true);
            objectNode.put("isTimedServer",e.isTimedServer());
            objectNode.put("timerType",e.getTimerType());
            objectNode.put("quotaDuration",e.getQuotaDuration());
            objectNode.put("limit",e.getLimit());
        };

        Long id = new Long(1);
        MailServerSettings mailServerSettings = mailServerSettingsRepository.findOne(id);
        Optional.ofNullable(mailServerSettings).ifPresent(mailServerSettingsConsumer);
        return objectNode.toString();

    }
}