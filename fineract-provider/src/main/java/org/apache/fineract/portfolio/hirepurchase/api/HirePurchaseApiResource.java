/*

    Created by Sinatra Gunda
    At 12:09 PM on 5/25/2022

*/
package org.apache.fineract.portfolio.hirepurchase.api;

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

/* Change log
    23/02/2021
        changed calculateLoanSchedule function to disable param validation please see line 680 - 670


*/

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookPopulatorService;
import org.apache.fineract.infrastructure.bulkimport.service.BulkImportWorkbookService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.dataqueries.service.EntityDatatableChecksReadService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.account.service.AccountAssociationsReadPlatformService;
import org.apache.fineract.portfolio.account.service.PortfolioAccountReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.collateral.service.CollateralReadPlatformService;
import org.apache.fineract.portfolio.fund.service.FundReadPlatformService;
import org.apache.fineract.portfolio.group.service.GroupReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.guarantor.service.GuarantorReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleCalculationPlatformService;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleHistoryReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanChargeReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.note.service.NoteReadPlatformServiceImpl;
import java.io.InputStream;
import java.util.*;


import java.io.InputStream;
import java.util.*;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.gson.JsonElement;

@Path("/hirepurchase")
@Component
@Scope("singleton")
public class HirePurchaseApiResource {

    private final Set<String> LOAN_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "accountNo", "status", "externalId", "clientId",
            "group", "loanProductId", "loanProductName", "loanProductDescription", "isLoanProductLinkedToFloatingRate", "fundId",
            "fundName", "loanPurposeId", "loanPurposeName", "loanOfficerId", "loanOfficerName", "currency", "principal", "totalOverpaid",
            "inArrearsTolerance", "termFrequency", "termPeriodFrequencyType", "numberOfRepayments", "repaymentEvery",
            "interestRatePerPeriod", "annualInterestRate", "repaymentFrequencyType", "transactionProcessingStrategyId",
            "transactionProcessingStrategyName", "interestRateFrequencyType", "amortizationType", "interestType",
            "interestCalculationPeriodType", LoanProductConstants.allowPartialPeriodInterestCalcualtionParamName, "expectedFirstRepaymentOnDate",
            "graceOnPrincipalPayment", "recurringMoratoriumOnPrincipalPeriods", "graceOnInterestPayment", "graceOnInterestCharged", "interestChargedFromDate", "timeline",
            "totalFeeChargesAtDisbursement", "summary", "repaymentSchedule", "transactions", "charges", "collateral", "guarantors",
            "meeting", "productOptions", "amortizationTypeOptions", "interestTypeOptions", "interestCalculationPeriodTypeOptions",
            "repaymentFrequencyTypeOptions", "repaymentFrequencyNthDayTypeOptions", "repaymentFrequencyDaysOfWeekTypeOptions",
            "termFrequencyTypeOptions", "interestRateFrequencyTypeOptions", "fundOptions", "repaymentStrategyOptions", "chargeOptions",
            "loanOfficerOptions", "loanPurposeOptions", "loanCollateralOptions", "chargeTemplate", "calendarOptions",
            "syncDisbursementWithMeeting", "loanCounter", "loanProductCounter", "notes", "accountLinkingOptions", "linkedAccount",
            "interestRateDifferential", "isFloatingInterestRate", "interestRatesPeriods", LoanApiConstants.canUseForTopup,
            LoanApiConstants.isTopup, LoanApiConstants.loanIdToClose, LoanApiConstants.topupAmount, LoanApiConstants.clientActiveLoanOptions,
            LoanApiConstants.datatables,LoanApiConstants.revolvingAccountIdParam ,LoanApiConstants.autoSettlementAtDisbursementParamName , LoanApiConstants.agentDataParam ,HirePurchaseConstants.hirePurchaseParam));

    private final String resourceNameForPermissions = "LOAN";

    private final PlatformSecurityContext context;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanDropdownReadPlatformService dropdownReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final LoanChargeReadPlatformService loanChargeReadPlatformService;
    private final CollateralReadPlatformService loanCollateralReadPlatformService;
    private final LoanScheduleCalculationPlatformService calculationPlatformService;
    private final GuarantorReadPlatformService guarantorReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final DefaultToApiJsonSerializer<LoanAccountData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanScheduleData> loanScheduleToApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final FromJsonHelper fromJsonHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CalendarReadPlatformService calendarReadPlatformService;
    private final NoteReadPlatformServiceImpl noteReadPlatformService;
    private final PortfolioAccountReadPlatformService portfolioAccountReadPlatformService;
    private final AccountAssociationsReadPlatformService accountAssociationsReadPlatformService;
    private final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;
    private final EntityDatatableChecksReadService entityDatatableChecksReadService;
    private final BulkImportWorkbookService bulkImportWorkbookService;
    private final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService;

    @Autowired
    public HirePurchaseApiResource(final PlatformSecurityContext context, final LoanReadPlatformService loanReadPlatformService,
                            final LoanProductReadPlatformService loanProductReadPlatformService,
                            final LoanDropdownReadPlatformService dropdownReadPlatformService, final FundReadPlatformService fundReadPlatformService,
                            final ChargeReadPlatformService chargeReadPlatformService, final LoanChargeReadPlatformService loanChargeReadPlatformService,
                            final CollateralReadPlatformService loanCollateralReadPlatformService,
                            final LoanScheduleCalculationPlatformService calculationPlatformService,
                            final GuarantorReadPlatformService guarantorReadPlatformService,
                            final CodeValueReadPlatformService codeValueReadPlatformService, final GroupReadPlatformService groupReadPlatformService,
                            final DefaultToApiJsonSerializer<LoanAccountData> toApiJsonSerializer,
                            final DefaultToApiJsonSerializer<LoanScheduleData> loanScheduleToApiJsonSerializer,
                            final ApiRequestParameterHelper apiRequestParameterHelper, final FromJsonHelper fromJsonHelper,
                            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                            final CalendarReadPlatformService calendarReadPlatformService, final NoteReadPlatformServiceImpl noteReadPlatformService,
                            final PortfolioAccountReadPlatformService portfolioAccountReadPlatformServiceImpl,
                            final AccountAssociationsReadPlatformService accountAssociationsReadPlatformService,
                            final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService,
                            final AccountDetailsReadPlatformService accountDetailsReadPlatformService,
                            final EntityDatatableChecksReadService entityDatatableChecksReadService,
                            final BulkImportWorkbookService bulkImportWorkbookService,
                            final BulkImportWorkbookPopulatorService bulkImportWorkbookPopulatorService) {
        this.context = context;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.fundReadPlatformService = fundReadPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.loanChargeReadPlatformService = loanChargeReadPlatformService;
        this.loanCollateralReadPlatformService = loanCollateralReadPlatformService;
        this.calculationPlatformService = calculationPlatformService;
        this.guarantorReadPlatformService = guarantorReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.groupReadPlatformService = groupReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.loanScheduleToApiJsonSerializer = loanScheduleToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.fromJsonHelper = fromJsonHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.calendarReadPlatformService = calendarReadPlatformService;
        this.noteReadPlatformService = noteReadPlatformService;
        this.portfolioAccountReadPlatformService = portfolioAccountReadPlatformServiceImpl;
        this.accountAssociationsReadPlatformService = accountAssociationsReadPlatformService;
        this.loanScheduleHistoryReadPlatformService = loanScheduleHistoryReadPlatformService;
        this.accountDetailsReadPlatformService = accountDetailsReadPlatformService;
        this.entityDatatableChecksReadService = entityDatatableChecksReadService;
        this.bulkImportWorkbookService=bulkImportWorkbookService;
        this.bulkImportWorkbookPopulatorService=bulkImportWorkbookPopulatorService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo,
                              @QueryParam("sqlSearch") final String sqlSearch,
                              @QueryParam("externalId") final String externalId,
                              // @QueryParam("underHierarchy") final String hierarchy,
                              @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
                              @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder,
                              @QueryParam("accountNo") final String accountNo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final SearchParameters searchParameters = SearchParameters.forLoans(sqlSearch, externalId, offset, limit, orderBy, sortOrder,
                accountNo);

        final List<LoanAccountData> loanBasicDetails = this.loanReadPlatformService.retrieveAllHirePurchaseLoans();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, loanBasicDetails, this.LOAN_DATA_PARAMETERS);
    }



}
