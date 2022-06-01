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
package org.apache.fineract.portfolio.savings.api;

import java.io.InputStream;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import org.apache.commons.lang.StringUtils;
import io.swagger.annotations.*;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookPopulatorService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookService;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.helper.NkwaziSavingsAccountHelper;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthOnSavingsAccountRepository;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.wese.helper.ObjectNodeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import io.swagger.annotations.*;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


// Added 16/12/2021
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;





@Path("/nkwazisavings")
@Component
@Scope("singleton")
public class NkwaziSavingsAccountsApiResourceEx {

    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer;

    // Added 16/12/2021
    //private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;

    // Added 24/05/2022
    private final FromJsonHelper fromJsonHelper ;
    private final DocumentWritePlatformService documentWritePlatformService ;
    private final SavingsAccountAssembler savingsAccountAssembler ;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;

    @Autowired
    public NkwaziSavingsAccountsApiResourceEx(final SavingsAccountWritePlatformService savingsAccountWritePlatformService , final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
                                              final PlatformSecurityContext context, final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer
                                               ,final FromJsonHelper fromJsonHelper , final DocumentWritePlatformService documentWritePlatformService , final  SavingsAccountAssembler savingsAccountAssembler) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        // added 16/12/2021
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService ;
        this.fromJsonHelper = fromJsonHelper ;
        this.documentWritePlatformService = documentWritePlatformService ;
        this.savingsAccountAssembler = savingsAccountAssembler ;
    }

     @POST
     @Path("{accountId}/{command}/{status}")
     @Consumes({ MediaType.APPLICATION_JSON })
     @Produces({ MediaType.APPLICATION_JSON })
     public String accountAction(@PathParam("command")final String command ,@PathParam("accountId") Long accountId ,@PathParam("status") boolean isEmployed ,String apiBody){

         String response[] = {null};

         if(command.equals("withdraw")){

             System.err.println("--------------------do some withdrawl son -----------------"+apiBody);

             final Long transactionId = NkwaziSavingsAccountHelper.withdraw(savingsAccountWritePlatformService ,savingsAccountAssembler, fromJsonHelper , accountId ,apiBody ,isEmployed);

             if(transactionId==null){
                 // error here
                 response[0] = ObjectNodeHelper.statusNode(false).put("message","Failed to make withdrawal .Amount greater than allowed max amount").toString();
                 return response[0];
             }

             response[0] = ObjectNodeHelper.statusNode(true).put("message","You have successfully withdrawn money .Now wait for feedback").toString();
                 //return response[0];
         }
         return response[0] ;
     }

}