/*

    Created by Sinatra Gunda
    At 6:04 PM on 2/7/2022

*/
package org.apache.fineract.portfolio.ssbpayments.api;

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

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.data.GlobalEntityType;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookPopulatorService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthOnSavingsAccountRepository;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


// Added 16/12/2021
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


@Path("/ssbpayments")
@Component
@Scope("singleton")
public class SsbPaymentsApiResource {

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

    @Autowired
    public SsbPaymentsApiResource(final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
                                      final PlatformSecurityContext context, final DefaultToApiJsonSerializer<SavingsAccountData> toApiJsonSerializer,
                                      final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                      final ApiRequestParameterHelper apiRequestParameterHelper,
                                      final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService,
                                      final BulkImportWorkbookService bulkImportWorkbookService,
                                      final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService ,
                                      final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository ,final SavingsAccountWritePlatformService savingsAccountWritePlatformService ,final ClientWritePlatformService clientWritePlatformService) {
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
    }


    @POST
    @Path("transactions/uploadtemplate")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String postSavingsTransactionTemplate(@FormDataParam("file") InputStream uploadedInputStream,
                                                 @FormDataParam("file") FormDataContentDisposition fileDetail,
                                                 @FormDataParam("locale") final String locale, @FormDataParam("dateFormat") final String dateFormat){
        final Long importDocumentId = this. bulkImportWorkbookService.importWorkbook(GlobalEntityType.SSB_PAYMENTS.toString(), uploadedInputStream,
                fileDetail,locale,dateFormat);
        return this.toApiJsonSerializer.serialize(importDocumentId);
    }

}
