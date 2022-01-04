/*

    Created by Sinatra Gunda
    At 12:06 PM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.api;


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
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.commissions.constants.CommissionChargeApiConstants;
import org.apache.fineract.portfolio.commissions.data.CommissionChargeData;
import org.apache.fineract.portfolio.commissions.service.CommissionChargesReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/commissioncharges")
@Component
@Scope("singleton")
public class CommissionChargeApiResource {


    private final PlatformSecurityContext context;
    private final CommissionChargesReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<CommissionChargeData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public CommissionChargeApiResource(final PlatformSecurityContext context, final CommissionChargesReadPlatformService readPlatformService,
                                final DefaultToApiJsonSerializer<CommissionChargeData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
                                final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }


    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(CommissionChargeApiConstants.resourceNameForPermissions);

        System.err.println("------------------template for commmissions----------------");

        //final ChargeData charge = this.readPlatformService.retrieveNewChargeDetails();
        final CommissionChargeData commissionChargeData = this.readPlatformService.retrieveNewCommissionChargeTemplate();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, commissionChargeData, CommissionChargeApiConstants.COMMISSION_CHARGE_DATA_PARAMETERS);

    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createCommissionCharge(final String apiRequestBodyAsJson) {

        System.err.println("------------------some authentication should be valid here ------------------");

        System.err.println("--------------------create commission charge here now ---------------------"+apiRequestBodyAsJson);

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCommissionCharge().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }


}

