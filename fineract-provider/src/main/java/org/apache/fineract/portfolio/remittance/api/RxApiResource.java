package org.apache.fineract.portfolio.remittance.api;

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
import org.apache.commons.lang.StringUtils;


import io.swagger.annotations.*;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.data.GlobalEntityType;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookPopulatorService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.jobs.exception.OperationNotAllowedException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.remittance.constants.RxDealConstants;
import org.apache.fineract.portfolio.remittance.data.RxData;
import org.apache.fineract.portfolio.remittance.data.RxDealData;
import org.apache.fineract.portfolio.remittance.service.RxDealReadPlatformService;
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

import java.util.Collection;
import java.util.Optional;


@Path("/rx")
@Component
@Scope("singleton")
public class RxApiResource {

    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<RxData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<RxDealData> rxDealDataDefaultToApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService;
    private final BulkImportWorkbookService bulkImportWorkbookService;
    private final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService;
    private final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository ;

    // Added 16/12/2021
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final ClientWritePlatformService clientWritePlatformService;

    private final RxDealReadPlatformService rxReadPlatformService ;

    @Autowired
    public RxApiResource(final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
                                      final PlatformSecurityContext context, final DefaultToApiJsonSerializer<RxData> toApiJsonSerializer,
                                      final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                      final ApiRequestParameterHelper apiRequestParameterHelper,
                                      final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService,
                                      final BulkImportWorkbookService bulkImportWorkbookService,
                                      final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService ,
                                      final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository ,final SavingsAccountWritePlatformService savingsAccountWritePlatformService ,final ClientWritePlatformService clientWritePlatformService ,final RxDealReadPlatformService rxReadPlatformService ,final DefaultToApiJsonSerializer rxDealDataDefaultToApiJsonSerializer) {
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
        this.rxReadPlatformService = rxReadPlatformService;
        /**
         * Added 06/11/2022 at 2213
         */
        this.rxDealDataDefaultToApiJsonSerializer = rxDealDataDefaultToApiJsonSerializer;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@Context final UriInfo uriInfo) {

        //this.context.authenticatedUser().validateHasReadPermission(RxDealConstants.permission);

        final RxData rxData =this.rxReadPlatformService.template();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, rxData,
                RxDealConstants.RX_DEAL_PARAMETERS);
    }

    /**
     * Added 06/11/2022 at 2205
     * Key could be string or id ,would be mentioned in QueryParam is key
     */
    @GET
    @Path("/transactions/{key}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String getData(@Context final UriInfo uriInfo ,@PathParam("key") final String id ,@QueryParam("isKey") boolean isKey) {

        System.err.println("------------validate if this guy has read params ");
        this.context.authenticatedUser().validateHasReadPermission(RxDealConstants.RESOURCE_NAME);

        System.err.println("------------done valudating ");

        Long rxDealId = null ;
        RxDealData rxDealData = null ;

        if(!isKey){
            rxDealId = Long.valueOf(id);
            rxDealData = rxReadPlatformService.retreiveOne(rxDealId);
        }
        else{
            String key = id ;
            rxDealData = rxReadPlatformService.retreiveOne(key);
        }

        System.err.println("---------------------value of object is ------"+ Optional.ofNullable(rxDealData).isPresent());

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.rxDealDataDefaultToApiJsonSerializer.serialize(settings, rxDealData,
                RxDealConstants.RX_DEAL_PARAMETERS);
    }

    /**
     * Added 07/11/2022 at 0837
     */
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getAllRxDeals(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(RxDealConstants.RESOURCE_NAME);

        Collection<RxDealData> rxDealDataCollection = this.rxReadPlatformService.retreiveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.rxDealDataDefaultToApiJsonSerializer.serialize(settings, rxDealDataCollection,
                RxDealConstants.RX_DEAL_PARAMETERS);
    }




    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        System.err.println("---------------------------------apiRequestBodyAsJson -------------"+apiRequestBodyAsJson);

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createRxDeal().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    /**
     * Added 06/11/2022 at 0405
     * @param apiRequestBodyAsJson
     * @return
     */
    @POST
    @Path("/receive/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String receive(final String apiRequestBodyAsJson ,@PathParam("id") final Long id) {

        System.err.println("---------------------------------apiRequestBodyAsJson -------------"+apiRequestBodyAsJson);

        final CommandWrapper commandRequest = new CommandWrapperBuilder().receiveRxDeal().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }


//    @PUT
//    @Path("{accountId}")
//    @Consumes({ MediaType.APPLICATION_JSON })
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String update(@PathParam("accountId") final Long accountId, final String apiRequestBodyAsJson,
//                         @QueryParam("command") final String commandParam) {
//
//        if (is(commandParam, "updateWithHoldTax")) {
//            final CommandWrapper commandRequest = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson).updateWithHoldTax(accountId)
//                    .build();
//            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
//            return this.toApiJsonSerializer.serialize(result);
//        }
//
//        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateSavingsAccount(accountId).withJson(apiRequestBodyAsJson)
//                .build();
//
//        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
//
//        return this.toApiJsonSerializer.serialize(result);
//    }


    @DELETE
    @Path("{accountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("accountId") final Long accountId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteSavingsAccount(accountId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("downloadtemplate")
    @Produces("application/vnd.ms-excel")
    public Response getSavingsTemplate(@QueryParam("officeId")final Long officeId,
                                       @QueryParam("staffId")final Long staffId,@QueryParam("dateFormat") final String dateFormat) {
        return bulkImportWorkbookPopulatorService.getTemplate(GlobalEntityType.SAVINGS_ACCOUNT.toString(),officeId, staffId,dateFormat);
    }

    @GET
    @Path("transactions/downloadtemplate")
    @Produces("application/vnd.ms-excel")
    public Response getSavingsTransactionTemplate(@QueryParam("officeId")final Long officeId,@QueryParam("dateFormat") final String dateFormat) {
        return bulkImportWorkbookPopulatorService.getTemplate(GlobalEntityType.SAVINGS_TRANSACTIONS.toString(),officeId, null,dateFormat);
    }


}
