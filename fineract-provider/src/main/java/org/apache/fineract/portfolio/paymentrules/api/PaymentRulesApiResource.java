/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 March 2023 at 12:43
 */
package org.apache.fineract.portfolio.paymentrules.api;


import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
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
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.portfolio.note.domain.NoteType;
import org.apache.fineract.portfolio.note.exception.NoteResourceNotSupportedException;
import org.apache.fineract.portfolio.note.service.NoteReadPlatformService;
import org.apache.fineract.portfolio.paymentrules.data.PaymentRuleData;
import org.apache.fineract.portfolio.paymentrules.service.PaymentRulesReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/paymentrules")
@Component
@Scope("singleton")
public class PaymentRulesApiResource {

    private final Set<String> NOTE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "clientId", "groupId", "loanId",
            "loanTransactionId", "depositAccountId", "savingAccountId", "noteType", "note", "createdById", "createdByUsername",
            "createdOn", "updatedById", "updatedByUsername", "updatedOn"));

    private final PlatformSecurityContext context;
    private final NoteReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<PaymentRuleData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PaymentRulesReadService paymentRulesReadService;

    @Autowired
    public PaymentRulesApiResource(final PlatformSecurityContext context, final NoteReadPlatformService readPlatformService,
                            final DefaultToApiJsonSerializer<PaymentRuleData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
                            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService ,final PaymentRulesReadService paymentRulesReadService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.paymentRulesReadService = paymentRulesReadService;
    }

    @Path("/template")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@PathParam("resourceId") final Long resourceId, @Context final UriInfo uriInfo) {

        final PaymentRuleData paymentRuleData = paymentRulesReadService.template();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, paymentRuleData);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@QueryParam("officeId") final Long officeId, @Context final UriInfo uriInfo) {

        final List<PaymentRuleData> paymentRuleDataList = this.paymentRulesReadService.retrieveAll(officeId,true);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, paymentRuleDataList);
    }


    @GET
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("id") final Long id, @Context final UriInfo uriInfo) {

        final PaymentRuleData paymentRuleData = this.paymentRulesReadService.retrieveOne(id);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, paymentRuleData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createPaymentRule().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("id") final Long id, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePaymentRule(id)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteNote(@PathParam("id") final Long id) {

        return null;
    }

}