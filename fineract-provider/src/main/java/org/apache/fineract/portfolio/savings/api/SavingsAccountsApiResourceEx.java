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


// Added 25/05/2022
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;



@Path("/savingsaccounts/ex")
@Component
@Scope("singleton")
public class SavingsAccountsApiResourceEx {

    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService;
    private final BulkImportWorkbookService bulkImportWorkbookService;
    private final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService;
    private final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository ;

    // Added 16/12/2021
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final ClientWritePlatformService clientWritePlatformService;

    // Added 24/05/2022
    private final FromJsonHelper fromJsonHelper ;
    private final DocumentWritePlatformService documentWritePlatformService ;
    private final SavingsAccountAssembler savingsAccountAssembler ;

    @Autowired
    public SavingsAccountsApiResourceEx(final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
                                        final PlatformSecurityContext context, final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer,
                                        final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                        final ApiRequestParameterHelper apiRequestParameterHelper,
                                        final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService,
                                        final BulkImportWorkbookService bulkImportWorkbookService,
                                        final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService ,
                                        final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository , final SavingsAccountWritePlatformService savingsAccountWritePlatformService , final ClientWritePlatformService clientWritePlatformService , final FromJsonHelper fromJsonHelper , final DocumentWritePlatformService documentWritePlatformService ,final  SavingsAccountAssembler savingsAccountAssembler) {
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.savingsAccountChargeReadPlatformService = savingsAccountChargeReadPlatformService;
        this.bulkImportWorkbookService=bulkImportWorkbookService;
        this.bulkImportWorkbookPopulatorService=bulkImportWorkbookPopulatorService;
        this.equityGrowthOnSavingsAccountRepository = equityGrowthOnSavingsAccountRepository;

        // added 16/12/2021
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService ;
        this.clientWritePlatformService = clientWritePlatformService;
        this.fromJsonHelper = fromJsonHelper ;
        this.documentWritePlatformService = documentWritePlatformService ;
        this.savingsAccountAssembler = savingsAccountAssembler ;
    }

    // @POST
    // @Path("nkwazi/{accountId}/{command}")
    // @Consumes({ MediaType.MULTIPART_FORM_DATA })
    // @Produces({ MediaType.APPLICATION_JSON })
    // public String handleCommand(@FormDataParam("file") @ApiParam(value = "file") InputStream uploadedInputStream,@PathParam("accountId") Long accountId ,@PathParam("command")String command ,String apiBody ,@HeaderParam("Content-Length") @ApiParam(value = "Content-Length") final Long fileSize,
    //         @FormDataParam("file") @ApiParam(value = "file") final FormDataContentDisposition fileDetails, @FormDataParam("file") @ApiParam(value = "file") final FormDataBodyPart bodyPart){

    //     String response[] = {null};

    //     if(command.equals("withdraw")){

    //         String entityType = "savings";
    //         String description = "Savings Withdrawal Document";
    //         String name = String.format("%s",fileDetails.getFileName());

    //         final DocumentCommand documentCommand = new DocumentCommand(null, null, entityType, accountId, name, fileDetails.getFileName(),
    //             fileSize, bodyPart.getMediaType().toString(), description, null);

    //         final Long documentId = this.documentWritePlatformService.createDocument(documentCommand, uploadedInputStream);

    //         Optional.ofNullable(documentId).ifPresent(e->{
    //             final Long transactionId = NkwaziSavingsAccountHelper.withdraw(savingsAccountWritePlatformService ,savingsAccountAssembler, fromJsonHelper , accountId ,apiBody);
    //             response[0] = this.toApiJsonSerializer.serialize(transactionId);
    //             //return response[0];
    //         });
    //     }
    //     return response[0] ;
    // }

}