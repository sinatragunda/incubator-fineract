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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

import org.apache.fineract.accounting.common.AccountingDropdownReadPlatformService;
import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.producttoaccountmapping.data.ChargeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.PaymentTypeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.service.ProductToGLAccountMappingReadPlatformService;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.SavingsCompoundingInterestPeriodType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationDaysInYearType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationType;
import org.apache.fineract.portfolio.savings.SavingsPostingInterestPeriodType;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.domain.*;

import org.apache.fineract.portfolio.savings.enumerations.SAVINGS_TOTAL_CALC_CRITERIA;
import org.apache.fineract.portfolio.savings.helper.EquityGrowthHelper;
import org.apache.fineract.portfolio.savings.helper.SavingsProductPortfolioHelper;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthDividendsRepository;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthOnSavingsAccountRepository;
import org.apache.fineract.portfolio.savings.repo.SavingsAccountMonthlyDepositRepository;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsDropdownReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.apache.fineract.portfolio.tax.service.TaxReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

// added 22/07/2021
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.fineract.wese.helper.ObjectNodeHelper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.joda.time.LocalDate;


// added 23/07/2021


@Path("/savingsproducts")
@Component
@Scope("singleton")
public class SavingsProductsApiResource {

    private final SavingsProductReadPlatformService savingProductReadPlatformService;
    private final SavingsDropdownReadPlatformService dropdownReadPlatformService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<SavingsProductData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService;
    private final ProductToGLAccountMappingReadPlatformService accountMappingReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final TaxReadPlatformService taxReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService ;
    private final FromJsonHelper fromJsonHelper ;
    private final SavingsAccountMonthlyDepositRepository savingsAccountMonthlyDepositRepository ;
    private final SavingsAccountDomainService savingsAccountDomainService ;
    private final EquityGrowthDividendsRepository equityGrowthDividendsRepository ;
    private final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository;
    private final SavingsAccountAssembler savingsAccountAssembler;

    @Autowired
    public SavingsProductsApiResource(final SavingsProductReadPlatformService savingProductReadPlatformService,
            final SavingsDropdownReadPlatformService dropdownReadPlatformService,
            final CurrencyReadPlatformService currencyReadPlatformService, final PlatformSecurityContext context,
            final DefaultToApiJsonSerializer<SavingsProductData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService,
            final ProductToGLAccountMappingReadPlatformService accountMappingReadPlatformService,
            final ChargeReadPlatformService chargeReadPlatformService, PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final TaxReadPlatformService taxReadPlatformService , final SavingsAccountReadPlatformService savingsAccountReadPlatformService ,final FromJsonHelper fromJsonHelper , final SavingsAccountMonthlyDepositRepository savingsAccountMonthlyDepositRepository,final SavingsAccountDomainService savingsAccountDomainService ,final  EquityGrowthDividendsRepository equityGrowthDividendsRepository ,final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository ,final SavingsAccountAssembler savingsAccountAssembler) {
        this.savingProductReadPlatformService = savingProductReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.accountingDropdownReadPlatformService = accountingDropdownReadPlatformService;
        this.accountMappingReadPlatformService = accountMappingReadPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.taxReadPlatformService = taxReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService ;
        this.fromJsonHelper = fromJsonHelper ;
        this.savingsAccountMonthlyDepositRepository = savingsAccountMonthlyDepositRepository;
        this.savingsAccountDomainService = savingsAccountDomainService ;
        this.equityGrowthDividendsRepository = equityGrowthDividendsRepository ;
        this.equityGrowthOnSavingsAccountRepository = equityGrowthOnSavingsAccountRepository ;
        this.savingsAccountAssembler = savingsAccountAssembler;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createSavingProduct().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("productId") final Long productId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateSavingProduct(productId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);

    }

    // Added 21/07/2021 ...Calculates the portfolio balance for savings account
    @POST
    @Path("/balance/{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String portfolioBalance(@PathParam("productId") final Long productId, final String apiRequestBodyAsJson) {

        //this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_PRODUCT_PORTFOLIO);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(apiRequestBodyAsJson);

        //int calculationMethod = fromJsonHelper.extractIntegerNamed("calcCriteria" ,jsonElement);
        //SAVINGS_TOTAL_CALC_CRITERIA savingsTotalCalcCriteria = SAVINGS_TOTAL_CALC_CRITERIA.fromInt(calculationMethod);
        LocalDate startDate = fromJsonHelper.extractLocalDateNamed("startDate" ,jsonElement);
        LocalDate endDate = fromJsonHelper.extractLocalDateNamed("endDate" ,jsonElement);

        // set account balance here
        BigDecimal balance = SavingsProductPortfolioHelper.portfolioBalanceDated(savingsAccountReadPlatformService ,savingsAccountMonthlyDepositRepository, startDate.toDate() ,endDate.toDate() ,null ,productId);
        //savingProductData.setPortfolioBalance(balance);
        return ObjectNodeHelper.statusNode(true).put("portfolioBalance",balance).toString();
    }

    // Added 21/07/2021 ...Calculates the portfolio balance for savings account
    @POST
    @Path("/equitygrowth/{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public List<EquityGrowthOnSavingsAccount> equityGrowth(@PathParam("productId") final Long productId, final String apiRequestBodyAsJson) {

        //this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_PRODUCT_PORTFOLIO);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(apiRequestBodyAsJson);

        //calculate equity growth here son
        List<SavingsAccountData> savingsAccountDataList = savingsAccountReadPlatformService.retrieveAllForPortfolio(productId);

        //int calculationMethod = fromJsonHelper.extractIntegerNamed("calcCriteria" ,jsonElement);
        //SAVINGS_TOTAL_CALC_CRITERIA savingsTotalCalcCriteria = SAVINGS_TOTAL_CALC_CRITERIA.fromInt(calculationMethod);
        LocalDate startDate = fromJsonHelper.extractLocalDateNamed("startDate" ,jsonElement);
        LocalDate endDate = fromJsonHelper.extractLocalDateNamed("endDate" ,jsonElement);
        BigDecimal portfolioBalance = fromJsonHelper.extractBigDecimalWithLocaleNamed("portfolioBalance",jsonElement);
        BigDecimal profitAmount = fromJsonHelper.extractBigDecimalWithLocaleNamed("profit" ,jsonElement);
        Boolean transferEarning = fromJsonHelper.extractBooleanNamed("transferEarnings" ,jsonElement);

        EquityGrowthHelper equityGrowthHelper = new EquityGrowthHelper();
        List<EquityGrowthOnSavingsAccount> equityGrowthOnSavingsAccountList =  equityGrowthHelper.calculateEquity(savingsAccountMonthlyDepositRepository, savingsAccountDataList ,productId, startDate.toDate() ,endDate.toDate(),portfolioBalance ,profitAmount);

        if(transferEarning){
            /// do something here that does transfer earnings to the said accounts son
            EquityGrowthDividends equityGrowthDividends = equityGrowthHelper.getEquityGrowthDividends();
            equityGrowthDividends = equityGrowthDividendsRepository.saveAndFlush(equityGrowthDividends);
            equityGrowthHelper.flushToDatabase(equityGrowthOnSavingsAccountRepository,equityGrowthDividends , equityGrowthOnSavingsAccountList);
            equityGrowthHelper.transferEarnings(savingsAccountDomainService ,equityGrowthOnSavingsAccountList);
        }
        return equityGrowthOnSavingsAccountList ;
    }


    // Added 23/07/2021 ...Calculates the portfo
    @GET
    @Path("/equitygrowth/{productId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<EquityGrowthDividends> equityGrowthDividends(@PathParam("productId") final Long productId) {

        //this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_PRODUCT_PORTFOLIO);
        List<EquityGrowthDividends> equityGrowthDividendsList = equityGrowthDividendsRepository.findBySavingsProductId(productId);
        return equityGrowthDividendsList ;
    }

    @GET
    @Path("/equitydividends/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public List<EquityGrowthOnSavingsAccount> viewEquityDividends(@PathParam("id") final Long id) {

        //this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_PRODUCT_PORTFOLIO)
        List<EquityGrowthOnSavingsAccount> equityGrowthOnSavingsAccountList = equityGrowthOnSavingsAccountRepository.findByEquityGrowthDividendsId(id);
        Consumer<EquityGrowthOnSavingsAccount> consumer = (e)->{
            SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(e.getSavingsAccountId());
            System.err.println("-----------------------setting savings account name-----------------"+savingsAccount.getClient().getDisplayName());
            String name = savingsAccount.getClient().getDisplayName();
            e.setClientName(name);
        };
        equityGrowthOnSavingsAccountList.stream().forEach(consumer);
        return equityGrowthOnSavingsAccountList;
    }


    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_PRODUCT_RESOURCE_NAME);

        final Collection<SavingsProductData> products = this.savingProductReadPlatformService.retrieveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, products,
				SavingsApiSetConstants.SAVINGS_PRODUCT_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("productId") final Long productId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_PRODUCT_RESOURCE_NAME);

        SavingsProductData savingProductData = this.savingProductReadPlatformService.retrieveOne(productId);
        // set account balance here
        BigDecimal balance = SavingsProductPortfolioHelper.portfolioBalance(savingsAccountReadPlatformService , productId);
        savingProductData.setPortfolioBalance(balance);

        final Collection<ChargeData> charges = this.chargeReadPlatformService.retrieveSavingsProductCharges(productId);

        savingProductData = SavingsProductData.withCharges(savingProductData, charges);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (savingProductData.hasAccountingEnabled()) {
            final Map<String, Object> accountingMappings = this.accountMappingReadPlatformService
                    .fetchAccountMappingDetailsForSavingsProduct(productId, savingProductData.accountingRuleTypeId());
            final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = this.accountMappingReadPlatformService
                    .fetchPaymentTypeToFundSourceMappingsForSavingsProduct(productId);
            Collection<ChargeToGLAccountMapper> feeToGLAccountMappings = this.accountMappingReadPlatformService
                    .fetchFeeToIncomeAccountMappingsForSavingsProduct(productId);
            Collection<ChargeToGLAccountMapper> penaltyToGLAccountMappings = this.accountMappingReadPlatformService
                    .fetchPenaltyToIncomeAccountMappingsForSavingsProduct(productId);
            savingProductData = SavingsProductData.withAccountingDetails(savingProductData, accountingMappings,
                    paymentChannelToFundSourceMappings, feeToGLAccountMappings, penaltyToGLAccountMappings);
        }

        if (settings.isTemplate()) {
            savingProductData = handleTemplateRelatedData(savingProductData);
        }

		return this.toApiJsonSerializer.serialize(settings, savingProductData,
				SavingsApiSetConstants.SAVINGS_PRODUCT_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.SAVINGS_PRODUCT_RESOURCE_NAME);

        final SavingsProductData savingProduct = handleTemplateRelatedData(null);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, savingProduct,
				SavingsApiSetConstants.SAVINGS_PRODUCT_RESPONSE_DATA_PARAMETERS);
    }

    private SavingsProductData handleTemplateRelatedData(final SavingsProductData savingsProduct) {

        final EnumOptionData interestCompoundingPeriodType = SavingsEnumerations
                .compoundingInterestPeriodType(SavingsCompoundingInterestPeriodType.DAILY);

        final EnumOptionData interestPostingPeriodType = SavingsEnumerations
                .interestPostingPeriodType(SavingsPostingInterestPeriodType.MONTHLY);

        final EnumOptionData interestCalculationType = SavingsEnumerations
                .interestCalculationType(SavingsInterestCalculationType.DAILY_BALANCE);

        final EnumOptionData interestCalculationDaysInYearType = SavingsEnumerations
                .interestCalculationDaysInYearType(SavingsInterestCalculationDaysInYearType.DAYS_365);

        final EnumOptionData accountingRule = AccountingEnumerations.accountingRuleType(AccountingRuleType.NONE);

        CurrencyData currency = CurrencyData.blank();
        final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
        if (currencyOptions.size() == 1) {
            currency = new ArrayList<>(currencyOptions).get(0);
        }

        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = this.dropdownReadPlatformService
                .retrieveCompoundingInterestPeriodTypeOptions();

        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = this.dropdownReadPlatformService
                .retrieveInterestPostingPeriodTypeOptions();

        final Collection<EnumOptionData> interestCalculationTypeOptions = this.dropdownReadPlatformService
                .retrieveInterestCalculationTypeOptions();

        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = this.dropdownReadPlatformService
                .retrieveInterestCalculationDaysInYearTypeOptions();

        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = this.dropdownReadPlatformService
                .retrieveLockinPeriodFrequencyTypeOptions();

        final Collection<EnumOptionData> withdrawalFeeTypeOptions = this.dropdownReadPlatformService.retrievewithdrawalFeeTypeOptions();

        final Collection<PaymentTypeData> paymentTypeOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();

        final Collection<EnumOptionData> accountingRuleOptions = this.accountingDropdownReadPlatformService
                .retrieveAccountingRuleTypeOptions();
        final Map<String, List<GLAccountData>> accountingMappingOptions = this.accountingDropdownReadPlatformService
                .retrieveAccountMappingOptionsForSavingsProducts();

        // charges
        final boolean feeChargesOnly = false;
        Collection<ChargeData> chargeOptions = this.chargeReadPlatformService.retrieveSavingsProductApplicableCharges(feeChargesOnly);
        chargeOptions = CollectionUtils.isEmpty(chargeOptions) ? null : chargeOptions;

        Collection<ChargeData> penaltyOptions = this.chargeReadPlatformService.retrieveSavingsApplicablePenalties();
        penaltyOptions = CollectionUtils.isEmpty(penaltyOptions) ? null : penaltyOptions;
        final Collection<TaxGroupData> taxGroupOptions = this.taxReadPlatformService.retrieveTaxGroupsForLookUp();
        SavingsProductData savingsProductToReturn = null;
        if (savingsProduct != null) {
            savingsProductToReturn = SavingsProductData.withTemplate(savingsProduct, currencyOptions, interestCompoundingPeriodTypeOptions,
                    interestPostingPeriodTypeOptions, interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions,
                    lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions, accountingRuleOptions,
                    accountingMappingOptions, chargeOptions, penaltyOptions, taxGroupOptions);
        } else {
            savingsProductToReturn = SavingsProductData.template(currency, interestCompoundingPeriodType, interestPostingPeriodType,
                    interestCalculationType, interestCalculationDaysInYearType, accountingRule, currencyOptions,
                    interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions, interestCalculationTypeOptions,
                    interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions,
                    paymentTypeOptions, accountingRuleOptions, accountingMappingOptions, chargeOptions, penaltyOptions, taxGroupOptions);
        }

        return savingsProductToReturn;
    }

    @DELETE
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("productId") final Long productId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteSavingProduct(productId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);

    }
}