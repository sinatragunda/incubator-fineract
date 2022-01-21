/*

    Created by Sinatra Gunda
    At 11:25 AM on 1/6/2022

*/
package org.apache.fineract.portfolio.commissions.api;


import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.commissions.constants.CommissionChargeApiConstants;
import org.apache.fineract.portfolio.commissions.constants.LoanAgentApiConstants;
import org.apache.fineract.portfolio.commissions.data.AttachedCommissionChargesData;
import org.apache.fineract.portfolio.commissions.data.LoanAgentData;
import org.apache.fineract.portfolio.commissions.data.LoansFromAgentsData;
import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;
import org.apache.fineract.portfolio.commissions.service.AttachedCommissionChargesReadPlatformService;
import org.apache.fineract.portfolio.commissions.service.LoanAgentReadPlatformService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

import javax.ws.rs.QueryParam;


import org.apache.fineract.portfolio.commissions.service.LoansFromAgentsReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/loansfromagents")
@Component
@Scope("singleton")
public class LoansFromAgentsApiResource {

    private final Set<String> LOAN_AGENT_PARAMS = new HashSet<>(Arrays.asList("id" ,"clientId","savingsAccountId"));

    private final String resourceNameForPermissions = "LOANS_FROM_AGENTS";

    private final PlatformSecurityContext context;
    private final LoansFromAgentsReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<LoansFromAgentsData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanAccountData> loansApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanAgentReadPlatformService loanAgentReadPlatformService ;
    private final AttachedCommissionChargesReadPlatformService attachedCommissionChargesReadPlatformService;
    private final DefaultToApiJsonSerializer<AttachedCommissionChargesData> attachedCommissionChargesDataDefaultToApiJsonSerializer;

    @Autowired
    public LoansFromAgentsApiResource(final PlatformSecurityContext context, final LoansFromAgentsReadPlatformService readPlatformService,
                                final DefaultToApiJsonSerializer<LoansFromAgentsData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
                                final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService ,final LoanAgentReadPlatformService loanAgentReadPlatformService ,
                                final DefaultToApiJsonSerializer<LoanAccountData> loansApiJsonSerializer ,final AttachedCommissionChargesReadPlatformService attachedCommissionChargesReadPlatformService ,final  DefaultToApiJsonSerializer<AttachedCommissionChargesData> attachedCommissionChargesDataDefaultToApiJsonSerializer) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.loanAgentReadPlatformService = loanAgentReadPlatformService ;
        this.loansApiJsonSerializer = loansApiJsonSerializer ;
        this.attachedCommissionChargesReadPlatformService = attachedCommissionChargesReadPlatformService ;
        this.attachedCommissionChargesDataDefaultToApiJsonSerializer = attachedCommissionChargesDataDefaultToApiJsonSerializer ;
    }

    // should all just be client oriented ?
    @GET
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("clientId") final Long clientId ,@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        // lets deal with client id since its the one that comes from client portal ,later will add functionality depending on what we have
        LoanAgentData loanAgentData = this.loanAgentReadPlatformService.retrieveOneByClient(clientId);

        Long loanAgentId = loanAgentData.getId();

        final List<LoanAccountData> loanList = readPlatformService.retrieveAllLoansForAgent(loanAgentId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.loansApiJsonSerializer.serialize(settings, loanList , LoanApiConstants.LOAN_DATA_PARAMETERS);

    }

    //  retrieve commission data for single loan
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanCharges(@QueryParam("loanId") final Long loanId ,@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        // lets deal with client id since its the one that comes from client portal ,later will add functionality depending on what we have

        List<AttachedCommissionChargesData> attachedCommissionChargesDataList = attachedCommissionChargesReadPlatformService.retrieveAllByLoan(loanId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.attachedCommissionChargesDataDefaultToApiJsonSerializer.serialize(settings, attachedCommissionChargesDataList , CommissionChargeApiConstants.COMMISSION_CHARGE_DATA_PARAMETERS);

    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveNewLoanAgentTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);


        System.err.println("------------------do we get back here again in template ? ,what for ?----------------");

        //final ChargeData charge = this.readPlatformService.retrieveNewChargeDetails();

        //final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        //return this.toApiJsonSerializer.serialize(settings, charge, this.CHARGES_DATA_PARAMETERS);
        return null ;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createLoanAgent(final String apiRequestBodyAsJson) {

        System.err.println("------------------some authentication should be valid here ------------------");

        System.err.println("--------------------create loan agent here now ---------------------"+apiRequestBodyAsJson);

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanAgent().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }


}

