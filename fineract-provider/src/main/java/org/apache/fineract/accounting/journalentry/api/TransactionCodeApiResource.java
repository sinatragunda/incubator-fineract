/*

    Created by Sinatra Gunda
    At 1:02 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.api;

import javax.ws.rs.Consumes;
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

import org.apache.fineract.accounting.constants.TransactionCodeConstants;
import org.apache.fineract.accounting.journalentry.data.TransactionCodeData;
import org.apache.fineract.accounting.journalentry.service.TransactionCodeReadPlatformService;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;


@Path("/transactioncodes")
@Component
@Scope("singleton")
public class TransactionCodeApiResource {

    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<TransactionCodeData> transactionCodeDataToApiJsonSerializer;
    private final TransactionCodeReadPlatformService transactionCodeReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public TransactionCodeApiResource(PlatformSecurityContext context, PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService, DefaultToApiJsonSerializer<TransactionCodeData> transactionCodeDataToApiJsonSerializer, TransactionCodeReadPlatformService transactionCodeReadPlatformService, ApiRequestParameterHelper apiRequestParameterHelper) {
        this.context = context;
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
        this.transactionCodeDataToApiJsonSerializer = transactionCodeDataToApiJsonSerializer;
        this.transactionCodeReadPlatformService = transactionCodeReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createTransactionCode(final String jsonRequestBody) {

        this.context.authenticatedUser().validateHasPermissionTo(TransactionCodeConstants.CREATE_PERMISSION);

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createTransactionCode().withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);

        return this.transactionCodeDataToApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateTransactionCode(final String jsonRequestBody ,@PathParam("id") Long transactionCodeId) {

        this.context.authenticatedUser().validateHasPermissionTo(TransactionCodeConstants.UPDATE_PERMISSION);

        System.err.println("--------------update transaction code -----------------------"+transactionCodeId);

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateTransactionCode(transactionCodeId).withJson(jsonRequestBody).build();

        System.err.println("------------------update transaction code here son ,at least give it correct naming ");

        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);

        String response = this.transactionCodeDataToApiJsonSerializer.serialize(result);

        System.err.println("----------------response is "+response);

        return response;
    }

    @GET
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTransactionCode(@PathParam("id") final Long id, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(TransactionCodeConstants.READ_PERMISSION);

        final TransactionCodeData transactionCodeData = transactionCodeReadPlatformService.retrieveOne(id);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.transactionCodeDataToApiJsonSerializer.serialize(settings, transactionCodeData, TransactionCodeConstants.TRANSACTION_CODE_DATA_PARAMETERS);
    }


    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTransactionCode(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser();
        final List<TransactionCodeData> transactionCodeDataList = transactionCodeReadPlatformService.retrieveAll();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.transactionCodeDataToApiJsonSerializer.serialize(settings, transactionCodeDataList, TransactionCodeConstants.TRANSACTION_CODE_DATA_PARAMETERS);
    }
}
