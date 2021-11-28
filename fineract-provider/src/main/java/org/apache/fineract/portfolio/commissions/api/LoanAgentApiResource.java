/*

    Created by Sinatra Gunda
    At 12:33 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.api;

import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.commissions.constants.LoanAgentApiConstants;
import org.apache.fineract.portfolio.commissions.data.LoanAgentData;
import org.apache.fineract.portfolio.commissions.domain.LoanAgent;
import org.apache.fineract.portfolio.commissions.service.LoanAgentReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/loanagents")
@Component
@Scope("singleton")
public class LoanAgentApiResource {

    private final Set<String> LOAN_AGENT_PARAMS = new HashSet<>(Arrays.asList("id" ,"clientId","savingsAccountId"));

    private final String resourceNameForPermissions = "LOAN_AGENT";

    private final PlatformSecurityContext context;
    private final LoanAgentReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<LoanAgentData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public LoanAgentApiResource(final PlatformSecurityContext context, final LoanAgentReadPlatformService readPlatformService,
                              final DefaultToApiJsonSerializer<LoanAgentData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
                              final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final List<LoanAgentData> loanAgentDataList = this.readPlatformService.retrieveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, loanAgentDataList , LoanAgentApiConstants.LOAN_AGENT_DATA_PARAMETERS);

    }
//
//    @GET
//    @Path("{chargeId}")
//    @Consumes({ MediaType.APPLICATION_JSON })
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String retrieveCharge(@PathParam("chargeId") final Long chargeId, @Context final UriInfo uriInfo) {
//
//        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
//
//        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
//
//        ChargeData charge = this.readPlatformService.retrieveCharge(chargeId);
//        if (settings.isTemplate()) {
//            final ChargeData templateData = this.readPlatformService.retrieveNewChargeDetails();
//            charge = ChargeData.withTemplate(charge, templateData);
//        }
//
//        return this.toApiJsonSerializer.serialize(settings, charge, this.CHARGES_DATA_PARAMETERS);
//    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveNewLoanAgentTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        //System.err.println("------------------do we get back here again in template ? ,what for ?----------------");

        //final ChargeData charge = this.readPlatformService.retrieveNewChargeDetails();

        //final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        //return this.toApiJsonSerializer.serialize(settings, charge, this.CHARGES_DATA_PARAMETERS);
        return null ;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createLoanAgent(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanAgent().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }


//    @PUT
//    @Path("{chargeId}")
//    @Consumes({ MediaType.APPLICATION_JSON })
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String updateCharge(@PathParam("chargeId") final Long chargeId, final String apiRequestBodyAsJson) {
//
//        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateCharge(chargeId).withJson(apiRequestBodyAsJson).build();
//
//        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
//
//        return this.toApiJsonSerializer.serialize(result);
//    }
//
//    @DELETE
//    @Path("{chargeId}")
//    @Consumes({ MediaType.APPLICATION_JSON })
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String deleteCharge(@PathParam("chargeId") final Long chargeId) {
//
//        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteCharge(chargeId).build();
//
//        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
//
//        return this.toApiJsonSerializer.serialize(result);
//    }
}
