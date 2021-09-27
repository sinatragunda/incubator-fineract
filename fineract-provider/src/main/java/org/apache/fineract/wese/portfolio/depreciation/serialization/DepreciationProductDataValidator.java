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
package org.apache.fineract.wese.portfolio.depreciation.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.common.AccountingConstants;
import org.apache.fineract.accounting.common.AccountingConstants.LOAN_PRODUCT_ACCOUNTING_PARAMS;
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.accounting.rule.domain.AccountingRule;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.domain.InterestCalculationPeriodMethod;
import org.apache.fineract.portfolio.loanproduct.domain.InterestMethod;
import org.apache.fineract.portfolio.loanproduct.domain.InterestRecalculationCompoundingMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanPreClosureInterestCalculationStrategy;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductValueConditionType;
import org.apache.fineract.portfolio.loanproduct.domain.RecalculationFrequencyType;
import org.apache.fineract.portfolio.loanproduct.enumerations.LOAN_FACTOR_SOURCE_ACCOUNT_TYPE;
import org.apache.fineract.portfolio.loanproduct.exception.EqualAmortizationUnsupportedFeatureException;
import org.apache.fineract.wese.enumerations.DURATION_TYPE;
import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciationProduct;
import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciationProductConstants;
import org.apache.fineract.wese.portfolio.depreciation.enumerations.DEPRECIATION_METHOD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import org.apache.fineract.wese.enumerations.SACCO_LOAN_LOCK ;


@Component
public final class DepreciationProductDataValidator {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("name","shortName",DepreciationProductConstants.assetClass,DepreciationProductConstants.currencyCode ,
            DepreciationProductConstants.depreciationMethod ,DepreciationProductConstants.description ,DepreciationProductConstants.externalId ,DepreciationProductConstants.isAppreciatingAsset,
            DepreciationProductConstants.isZeroSalvagedAsset ,DepreciationProductConstants.maxRateOfDecay ,DepreciationProductConstants.rateOfDecay ,DepreciationProductConstants.usefullLifeTimer ,
            DepreciationProductConstants.maxRateOfDecay ,DepreciationProductConstants.usefullLifeTimer , DepreciationProductConstants.salvageValue ,DepreciationProductConstants.tag ,DepreciationProductConstants.maxRateOfDecay));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public DepreciationProductDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanproduct");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed("name", element);
        baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);

        final String shortName = this.fromApiJsonHelper.extractStringNamed(LoanProductConstants.shortName, element);
        baseDataValidator.reset().parameter(LoanProductConstants.shortName).value(shortName).notBlank().notExceedingLengthOf(4);

        final String description = this.fromApiJsonHelper.extractStringNamed("description", element);
        baseDataValidator.reset().parameter("description").value(description).notExceedingLengthOf(500);


        final String tag = this.fromApiJsonHelper.extractStringNamed(DepreciationProductConstants.tag, element);
        baseDataValidator.reset().parameter(DepreciationProductConstants.tag).value(tag).notExceedingLengthOf(500);



        if (this.fromApiJsonHelper.parameterExists(DepreciationProductConstants.assetClass, element)) {
            final Long assetClassId = this.fromApiJsonHelper.extractLongNamed(DepreciationProductConstants.assetClass, element);
            baseDataValidator.reset().parameter(DepreciationProductConstants.assetClass).value(assetClassId).ignoreIfNull().integerGreaterThanZero();
        }
        
        boolean isZeroSalvagedAsset = false;
        if (this.fromApiJsonHelper.parameterExists(DepreciationProductConstants.isZeroSalvagedAsset, element)) {
            isZeroSalvagedAsset = this.fromApiJsonHelper.extractBooleanNamed(DepreciationProductConstants.isZeroSalvagedAsset, element);
            baseDataValidator.reset().parameter(DepreciationProductConstants.isZeroSalvagedAsset).value(isZeroSalvagedAsset).ignoreIfNull()
                    .validateForBooleanValue();
        }

        boolean isAppreciatingAsset = false ;
        if (this.fromApiJsonHelper.parameterExists(DepreciationProductConstants.isAppreciatingAsset, element)) {
            isAppreciatingAsset = this.fromApiJsonHelper.extractBooleanNamed(DepreciationProductConstants.isAppreciatingAsset, element);
            baseDataValidator.reset().parameter(DepreciationProductConstants.isAppreciatingAsset).value(isAppreciatingAsset).ignoreIfNull()
                    .validateForBooleanValue();
        }
       

        final Integer usefullLife = this.fromApiJsonHelper.extractIntegerNamed(DepreciationProductConstants.usefullLife, element, Locale.getDefault());
        baseDataValidator.reset().parameter(DepreciationProductConstants.usefullLife).value(usefullLife).ignoreIfNull().integerZeroOrGreater();


        final Integer minUsefullLife = this.fromApiJsonHelper.extractIntegerNamed(DepreciationProductConstants.minUsefullLife, element, Locale.getDefault());
        baseDataValidator.reset().parameter(DepreciationProductConstants.minUsefullLife).value(minUsefullLife).ignoreIfNull().integerZeroOrGreater();


        final Integer maxUsefullLife = this.fromApiJsonHelper.extractIntegerNamed(DepreciationProductConstants.maxUsefullLife, element, Locale.getDefault());
        baseDataValidator.reset().parameter(DepreciationProductConstants.maxUsefullLife).value(maxUsefullLife).ignoreIfNull().integerZeroOrGreater();



        Integer depreciationMethodInt = null ;
        if(this.fromApiJsonHelper.parameterExists(DepreciationProductConstants.depreciationMethod, element)) {
            depreciationMethodInt = this.fromApiJsonHelper.extractIntegerNamed(DepreciationProductConstants.depreciationMethod, element, Locale.getDefault());
            baseDataValidator.reset().parameter(DepreciationProductConstants.depreciationMethod).value(depreciationMethodInt).ignoreIfNull();
        }

        //final DEPRECIATION_METHOD depreciationMethod = DEPRECIATION_METHOD.fromInt(depreciationMethodInt);

        Integer usefullLifeTimerInt = null ;
        if(this.fromApiJsonHelper.parameterExists(DepreciationProductConstants.usefullLifeTimer, element)) {
            usefullLifeTimerInt = this.fromApiJsonHelper.extractIntegerNamed(DepreciationProductConstants.usefullLifeTimer, element, Locale.getDefault());
            baseDataValidator.reset().parameter(DepreciationProductConstants.usefullLifeTimer).value(usefullLifeTimerInt).ignoreIfNull();
        }

        //final DURATION_TYPE usefullLifeTimer = DURATION_TYPE.fromInt(usefullLifeTimerInt);

        final String currencyCode = this.fromApiJsonHelper.extractStringNamed("currencyCode", element);
        baseDataValidator.reset().parameter("currencyCode").value(currencyCode).notBlank().notExceedingLengthOf(3);


        final BigDecimal rateOfDecay = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(DepreciationProductConstants.rateOfDecay, element);
        baseDataValidator.reset().parameter(DepreciationProductConstants.rateOfDecay).value(rateOfDecay).positiveAmount();


        final BigDecimal minRateOfDecay = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(DepreciationProductConstants.minRateOfDecay, element);
        baseDataValidator.reset().parameter(DepreciationProductConstants.minRateOfDecay).value(minRateOfDecay).positiveAmount();


        final BigDecimal maxRateOfDecay = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(DepreciationProductConstants.maxRateOfDecay, element);
        baseDataValidator.reset().parameter(DepreciationProductConstants.maxRateOfDecay).value(maxRateOfDecay).positiveAmount();

        final BigDecimal salvageValue = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(DepreciationProductConstants.salvageValue, element);
        baseDataValidator.reset().parameter(DepreciationProductConstants.salvageValue).value(salvageValue).ignoreIfNull().zeroOrPositiveAmount();

        final Integer accountingRuleInt = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(DepreciationProductConstants.accountingRule ,element);
        baseDataValidator.reset().parameter(DepreciationProductConstants.accountingRule).value(accountingRuleInt).ignoreIfNull();

        //AccountingRuleType accountingRuleType = AccountingRuleType.fromInt(accountingRuleInt);


        if (isAccrualBasedAccounting(accountingRuleInt)) {

            final Long depreciationChargeAccountId = this.fromApiJsonHelper.extractLongNamed(
                    AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.DEPRECIATION_CHARGE.getValue(), element);
            baseDataValidator.reset().parameter(AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.DEPRECIATION_CHARGE.getValue())
                    .value(depreciationChargeAccountId).notNull().integerGreaterThanZero();

            final Long assetCostAccountId = this.fromApiJsonHelper.extractLongNamed(
                    AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ASSET_COST.getValue(), element);
            baseDataValidator.reset().parameter(AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ASSET_COST.getValue()).value(assetCostAccountId)
                    .notNull().integerGreaterThanZero();

            final Long accumulatedDepreciationAccountId = this.fromApiJsonHelper.extractLongNamed(
                    AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ACCUMULATED_DEPRECIATION.getValue(), element);
            baseDataValidator.reset().parameter(AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ACCUMULATED_DEPRECIATION.getValue())
                    .value(accumulatedDepreciationAccountId).notNull().integerGreaterThanZero();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json, final DepreciationProduct depreciationProduct) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("depreciationproduct");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
        if (this.fromApiJsonHelper.parameterExists("name", element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.shortName, element)) {
            final String shortName = this.fromApiJsonHelper.extractStringNamed(LoanProductConstants.shortName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.shortName).value(shortName).notBlank().notExceedingLengthOf(4);
        }

        if (this.fromApiJsonHelper.parameterExists("description", element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed("description", element);
            baseDataValidator.reset().parameter("description").value(description).notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists("fundId", element)) {
            final Long fundId = this.fromApiJsonHelper.extractLongNamed("fundId", element);
            baseDataValidator.reset().parameter("fundId").value(fundId).ignoreIfNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("includeInBorrowerCycle", element)) {
            final Boolean includeInBorrowerCycle = this.fromApiJsonHelper.extractBooleanNamed("includeInBorrowerCycle", element);
            baseDataValidator.reset().parameter("includeInBorrowerCycle").value(includeInBorrowerCycle).ignoreIfNull()
                    .validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists("currencyCode", element)) {
            final String currencyCode = this.fromApiJsonHelper.extractStringNamed("currencyCode", element);
            baseDataValidator.reset().parameter("currencyCode").value(currencyCode).notBlank().notExceedingLengthOf(3);
        }

        if (this.fromApiJsonHelper.parameterExists("digitsAfterDecimal", element)) {
            final Integer digitsAfterDecimal = this.fromApiJsonHelper.extractIntegerNamed("digitsAfterDecimal", element,
                    Locale.getDefault());
            baseDataValidator.reset().parameter("digitsAfterDecimal").value(digitsAfterDecimal).notNull().inMinMaxRange(0, 6);
        }

        if (this.fromApiJsonHelper.parameterExists("inMultiplesOf", element)) {
            final Integer inMultiplesOf = this.fromApiJsonHelper.extractIntegerNamed("inMultiplesOf", element, Locale.getDefault());
            baseDataValidator.reset().parameter("inMultiplesOf").value(inMultiplesOf).ignoreIfNull().integerZeroOrGreater();
        }

        final String minPrincipalParameterName = "minPrincipal";
        BigDecimal minPrincipalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(minPrincipalParameterName, element)) {
            minPrincipalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minPrincipalParameterName, element);
            baseDataValidator.reset().parameter(minPrincipalParameterName).value(minPrincipalAmount).ignoreIfNull().positiveAmount();
        }

        final String maxPrincipalParameterName = "maxPrincipal";
        BigDecimal maxPrincipalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(maxPrincipalParameterName, element)) {
            maxPrincipalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(maxPrincipalParameterName, element);
            baseDataValidator.reset().parameter(maxPrincipalParameterName).value(maxPrincipalAmount).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("principal", element)) {
            final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);
            baseDataValidator.reset().parameter("principal").value(principal).positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("inArrearsTolerance", element)) {
            final BigDecimal inArrearsTolerance = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("inArrearsTolerance", element);
            baseDataValidator.reset().parameter("inArrearsTolerance").value(inArrearsTolerance).ignoreIfNull().zeroOrPositiveAmount();
        }

        final String minNumberOfRepaymentsParameterName = "minNumberOfRepayments";
        Integer minNumberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(minNumberOfRepaymentsParameterName, element)) {
            minNumberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(minNumberOfRepaymentsParameterName, element);
            baseDataValidator.reset().parameter(minNumberOfRepaymentsParameterName).value(minNumberOfRepayments).ignoreIfNull()
                    .integerGreaterThanZero();
        }

        final String maxNumberOfRepaymentsParameterName = "maxNumberOfRepayments";
        Integer maxNumberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(maxNumberOfRepaymentsParameterName, element)) {
            maxNumberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(maxNumberOfRepaymentsParameterName, element);
            baseDataValidator.reset().parameter(maxNumberOfRepaymentsParameterName).value(maxNumberOfRepayments).ignoreIfNull()
                    .integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("numberOfRepayments", element)) {
            final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("numberOfRepayments", element);
            baseDataValidator.reset().parameter("numberOfRepayments").value(numberOfRepayments).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("repaymentEvery", element)) {
            final Integer repaymentEvery = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("repaymentEvery", element);
            baseDataValidator.reset().parameter("repaymentEvery").value(repaymentEvery).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("repaymentFrequencyType", element)) {
            final Integer repaymentFrequencyType = this.fromApiJsonHelper.extractIntegerNamed("repaymentFrequencyType", element,
                    Locale.getDefault());
            baseDataValidator.reset().parameter("repaymentFrequencyType").value(repaymentFrequencyType).notNull().inMinMaxRange(0, 3);
        }

        if (this.fromApiJsonHelper.parameterExists("transactionProcessingStrategyId", element)) {
            final Long transactionProcessingStrategyId = this.fromApiJsonHelper
                    .extractLongNamed("transactionProcessingStrategyId", element);
            baseDataValidator.reset().parameter("transactionProcessingStrategyId").value(transactionProcessingStrategyId).notNull()
                    .integerGreaterThanZero();
        }

        // grace validation
        if (this.fromApiJsonHelper.parameterExists("graceOnPrincipalPayment", element)) {
            final Integer graceOnPrincipalPayment = this.fromApiJsonHelper
                    .extractIntegerWithLocaleNamed("graceOnPrincipalPayment", element);
            baseDataValidator.reset().parameter("graceOnPrincipalPayment").value(graceOnPrincipalPayment).zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("graceOnInterestPayment", element)) {
            final Integer graceOnInterestPayment = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("graceOnInterestPayment", element);
            baseDataValidator.reset().parameter("graceOnInterestPayment").value(graceOnInterestPayment).zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("graceOnInterestCharged", element)) {
            final Integer graceOnInterestCharged = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("graceOnInterestCharged", element);
            baseDataValidator.reset().parameter("graceOnInterestCharged").value(graceOnInterestCharged).zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.graceOnArrearsAgeingParameterName, element)) {
            final Integer graceOnArrearsAgeing = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    LoanProductConstants.graceOnArrearsAgeingParameterName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.graceOnArrearsAgeingParameterName).value(graceOnArrearsAgeing)
                    .integerZeroOrGreater();
        }

        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.overdueDaysForNPAParameterName, element)) {
            final Integer overdueDaysForNPA = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    LoanProductConstants.overdueDaysForNPAParameterName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.overdueDaysForNPAParameterName).value(overdueDaysForNPA)
                    .integerZeroOrGreater();
        }

        //
        if (this.fromApiJsonHelper.parameterExists("amortizationType", element)) {
            final Integer amortizationType = this.fromApiJsonHelper.extractIntegerNamed("amortizationType", element, Locale.getDefault());
            baseDataValidator.reset().parameter("amortizationType").value(amortizationType).notNull().inMinMaxRange(0, 1);
        }

        if (this.fromApiJsonHelper.parameterExists("interestType", element)) {
            final Integer interestType = this.fromApiJsonHelper.extractIntegerNamed("interestType", element, Locale.getDefault());
            baseDataValidator.reset().parameter("interestType").value(interestType).notNull().inMinMaxRange(0, 1);
        }
        Integer interestCalculationPeriodType = loanProduct.getLoanProductRelatedDetail().getInterestCalculationPeriodMethod().getValue();
        if (this.fromApiJsonHelper.parameterExists("interestCalculationPeriodType", element)) {
            interestCalculationPeriodType = this.fromApiJsonHelper.extractIntegerNamed("interestCalculationPeriodType", element,
                    Locale.getDefault());
            baseDataValidator.reset().parameter("interestCalculationPeriodType").value(interestCalculationPeriodType).notNull()
                    .inMinMaxRange(0, 1);
        }

        /**
         * { @link DaysInYearType }
         */
        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.daysInYearTypeParameterName, element)) {
            final Integer daysInYearType = this.fromApiJsonHelper.extractIntegerNamed(LoanProductConstants.daysInYearTypeParameterName,
                    element, Locale.getDefault());
            baseDataValidator.reset().parameter(LoanProductConstants.daysInYearTypeParameterName).value(daysInYearType).notNull()
                    .isOneOfTheseValues(1, 360, 364, 365);
        }

        /**
         * { @link DaysInMonthType }
         */
        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.daysInMonthTypeParameterName, element)) {
            final Integer daysInMonthType = this.fromApiJsonHelper.extractIntegerNamed(LoanProductConstants.daysInMonthTypeParameterName,
                    element, Locale.getDefault());
            baseDataValidator.reset().parameter(LoanProductConstants.daysInMonthTypeParameterName).value(daysInMonthType).notNull()
                    .isOneOfTheseValues(1, 30);
        }

        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.accountMovesOutOfNPAOnlyOnArrearsCompletionParamName, element)) {
            Boolean npaChangeConfig = this.fromApiJsonHelper.extractBooleanNamed(
                    LoanProductConstants.accountMovesOutOfNPAOnlyOnArrearsCompletionParamName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.accountMovesOutOfNPAOnlyOnArrearsCompletionParamName)
                    .value(npaChangeConfig).notNull().isOneOfTheseValues(true, false);
        }
        
        boolean isEqualAmortization = loanProduct.isEqualAmortization();
        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.isEqualAmortizationParam, element)) {
            isEqualAmortization = this.fromApiJsonHelper.extractBooleanNamed(LoanProductConstants.isEqualAmortizationParam, element);
            baseDataValidator.reset().parameter(LoanProductConstants.isEqualAmortizationParam).value(isEqualAmortization).ignoreIfNull()
                    .validateForBooleanValue();
        }


        ///loan sacco products added 26/10/2020

        boolean isSaccoProduct = loanProduct.isSaccoProduct();
        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.isSaccoProductParam, element)) {
            isSaccoProduct = this.fromApiJsonHelper.extractBooleanNamed(LoanProductConstants.isSaccoProductParam, element);
            baseDataValidator.reset().parameter(LoanProductConstants.isSaccoProductParam).value(isSaccoProduct).ignoreIfNull()
                    .validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists("loanFactor", element)) {
            final Integer loanFactor = this.fromApiJsonHelper.extractIntegerNamed("loanFactor", element, Locale.getDefault());
            baseDataValidator.reset().parameter("loanFactor").value(loanFactor).notNull().integerZeroOrGreater();
        }

        if (this.fromApiJsonHelper.parameterExists("shareAccountValidity", element)) {
            final Integer shareAccountValidity = this.fromApiJsonHelper.extractIntegerNamed("shareAccountValidity", element, Locale.getDefault());
            baseDataValidator.reset().parameter("shareAccountValidity").value(shareAccountValidity).ignoreIfNull().integerZeroOrGreater();
        }


        if (this.fromApiJsonHelper.parameterExists("saccoLoanLock", element)) {
            final Integer saccoLoanLock = this.fromApiJsonHelper.extractIntegerNamed("saccoLoanLock", element, Locale.getDefault());
            baseDataValidator.reset().parameter("saccoLoanLock").value(saccoLoanLock).ignoreIfNull();
        }


        // Interest recalculation settings
        Boolean isInterestRecalculationEnabled = loanProduct.isInterestRecalculationEnabled();
        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.isInterestRecalculationEnabledParameterName, element)) {
            isInterestRecalculationEnabled = this.fromApiJsonHelper.extractBooleanNamed(
                    LoanProductConstants.isInterestRecalculationEnabledParameterName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.isInterestRecalculationEnabledParameterName)
                    .value(isInterestRecalculationEnabled).notNull().isOneOfTheseValues(true, false);
        }

        if (isInterestRecalculationEnabled != null) {
            if (isInterestRecalculationEnabled) {
                if (isEqualAmortization) { throw new EqualAmortizationUnsupportedFeatureException("interest.recalculation",
                        "interest recalculation"); }
                validateInterestRecalculationParams(element, baseDataValidator, loanProduct);
            }
        }

        // interest rates
        boolean isLinkedToFloatingInterestRates = loanProduct.isLinkedToFloatingInterestRate();
        if (this.fromApiJsonHelper.parameterExists("isLinkedToFloatingInterestRates", element)) {
            isLinkedToFloatingInterestRates = this.fromApiJsonHelper.extractBooleanNamed("isLinkedToFloatingInterestRates", element);
        }
        if (isLinkedToFloatingInterestRates) {
            if(isEqualAmortization){
                throw new EqualAmortizationUnsupportedFeatureException("floating.interest.rate", "floating interest rate");
            }
            if (this.fromApiJsonHelper.parameterExists("interestRatePerPeriod", element)) {
                baseDataValidator
                        .reset()
                        .parameter("interestRatePerPeriod")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.true",
                                "interestRatePerPeriod param is not supported when isLinkedToFloatingInterestRates is true");
            }

            if (this.fromApiJsonHelper.parameterExists("minInterestRatePerPeriod", element)) {
                baseDataValidator
                        .reset()
                        .parameter("minInterestRatePerPeriod")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.true",
                                "minInterestRatePerPeriod param is not supported when isLinkedToFloatingInterestRates is true");
            }

            if (this.fromApiJsonHelper.parameterExists("maxInterestRatePerPeriod", element)) {
                baseDataValidator
                        .reset()
                        .parameter("maxInterestRatePerPeriod")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.true",
                                "maxInterestRatePerPeriod param is not supported when isLinkedToFloatingInterestRates is true");
            }

            if (this.fromApiJsonHelper.parameterExists("interestRateFrequencyType", element)) {
                baseDataValidator
                        .reset()
                        .parameter("interestRateFrequencyType")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.true",
                                "interestRateFrequencyType param is not supported when isLinkedToFloatingInterestRates is true");
            }

            Integer interestType = this.fromApiJsonHelper.parameterExists("interestType", element) ? this.fromApiJsonHelper
                    .extractIntegerNamed("interestType", element, Locale.getDefault()) : loanProduct.getLoanProductRelatedDetail()
                    .getInterestMethod().getValue();
            if ((interestType == null || interestType != InterestMethod.DECLINING_BALANCE.getValue())
                    || (isInterestRecalculationEnabled == null || isInterestRecalculationEnabled == false)) {
                baseDataValidator
                        .reset()
                        .parameter("isLinkedToFloatingInterestRates")
                        .failWithCode("supported.only.for.declining.balance.interest.recalculation.enabled",
                                "Floating interest rates are supported only for declining balance and interest recalculation enabled loan products");
            }

            Long floatingRatesId = loanProduct.getFloatingRates() == null ? null : loanProduct.getFloatingRates().getFloatingRate().getId();
            if (this.fromApiJsonHelper.parameterExists("floatingRatesId", element)) {
                floatingRatesId = this.fromApiJsonHelper.extractLongNamed("floatingRatesId", element);
            }
            baseDataValidator.reset().parameter("floatingRatesId").value(floatingRatesId).notNull();

            BigDecimal interestRateDifferential = loanProduct.getFloatingRates() == null ? null : loanProduct.getFloatingRates()
                    .getInterestRateDifferential();
            if (this.fromApiJsonHelper.parameterExists("interestRateDifferential", element)) {
                interestRateDifferential = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("interestRateDifferential", element);
            }
            baseDataValidator.reset().parameter("interestRateDifferential").value(interestRateDifferential).notNull()
                    .zeroOrPositiveAmount();

            final String minDifferentialLendingRateParameterName = "minDifferentialLendingRate";
            BigDecimal minDifferentialLendingRate = loanProduct.getFloatingRates() == null ? null : loanProduct.getFloatingRates()
                    .getMinDifferentialLendingRate();
            if (this.fromApiJsonHelper.parameterExists(minDifferentialLendingRateParameterName, element)) {
                minDifferentialLendingRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        minDifferentialLendingRateParameterName, element);
            }
            baseDataValidator.reset().parameter(minDifferentialLendingRateParameterName).value(minDifferentialLendingRate).notNull()
                    .zeroOrPositiveAmount();

            final String defaultDifferentialLendingRateParameterName = "defaultDifferentialLendingRate";
            BigDecimal defaultDifferentialLendingRate = loanProduct.getFloatingRates() == null ? null : loanProduct.getFloatingRates()
                    .getDefaultDifferentialLendingRate();
            if (this.fromApiJsonHelper.parameterExists(defaultDifferentialLendingRateParameterName, element)) {
                defaultDifferentialLendingRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        defaultDifferentialLendingRateParameterName, element);
            }
            baseDataValidator.reset().parameter(defaultDifferentialLendingRateParameterName).value(defaultDifferentialLendingRate)
                    .notNull().zeroOrPositiveAmount();

            final String maxDifferentialLendingRateParameterName = "maxDifferentialLendingRate";
            BigDecimal maxDifferentialLendingRate = loanProduct.getFloatingRates() == null ? null : loanProduct.getFloatingRates()
                    .getMaxDifferentialLendingRate();
            if (this.fromApiJsonHelper.parameterExists(maxDifferentialLendingRateParameterName, element)) {
                maxDifferentialLendingRate = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                        maxDifferentialLendingRateParameterName, element);
            }
            baseDataValidator.reset().parameter(maxDifferentialLendingRateParameterName).value(maxDifferentialLendingRate).notNull()
                    .zeroOrPositiveAmount();

            if (defaultDifferentialLendingRate != null && defaultDifferentialLendingRate.compareTo(BigDecimal.ZERO) != -1) {
                if (minDifferentialLendingRate != null && minDifferentialLendingRate.compareTo(BigDecimal.ZERO) != -1) {
                    baseDataValidator.reset().parameter("defaultDifferentialLendingRate").value(defaultDifferentialLendingRate)
                            .notLessThanMin(minDifferentialLendingRate);
                }
            }

            if (maxDifferentialLendingRate != null && maxDifferentialLendingRate.compareTo(BigDecimal.ZERO) != -1) {
                if (minDifferentialLendingRate != null && minDifferentialLendingRate.compareTo(BigDecimal.ZERO) != -1) {
                    baseDataValidator.reset().parameter("maxDifferentialLendingRate").value(maxDifferentialLendingRate)
                            .notLessThanMin(minDifferentialLendingRate);
                }
            }

            if (maxDifferentialLendingRate != null && maxDifferentialLendingRate.compareTo(BigDecimal.ZERO) != -1) {
                if (defaultDifferentialLendingRate != null && defaultDifferentialLendingRate.compareTo(BigDecimal.ZERO) != -1) {
                    baseDataValidator.reset().parameter("maxDifferentialLendingRate").value(maxDifferentialLendingRate)
                            .notLessThanMin(defaultDifferentialLendingRate);
                }
            }

            Boolean isFloatingInterestRateCalculationAllowed = loanProduct.getFloatingRates() == null ? null : loanProduct
                    .getFloatingRates().isFloatingInterestRateCalculationAllowed();
            if (this.fromApiJsonHelper.parameterExists("isFloatingInterestRateCalculationAllowed", element)) {
                isFloatingInterestRateCalculationAllowed = this.fromApiJsonHelper.extractBooleanNamed(
                        "isFloatingInterestRateCalculationAllowed", element);
            }
            baseDataValidator.reset().parameter("isFloatingInterestRateCalculationAllowed").value(isFloatingInterestRateCalculationAllowed)
                    .notNull().isOneOfTheseValues(true, false);
        } else {
            if (this.fromApiJsonHelper.parameterExists("floatingRatesId", element)) {
                baseDataValidator
                        .reset()
                        .parameter("floatingRatesId")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.false",
                                "floatingRatesId param is not supported when isLinkedToFloatingInterestRates is not supplied or false");
            }

            if (this.fromApiJsonHelper.parameterExists("interestRateDifferential", element)) {
                baseDataValidator
                        .reset()
                        .parameter("interestRateDifferential")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.false",
                                "interestRateDifferential param is not supported when isLinkedToFloatingInterestRates is not supplied or false");
            }

            if (this.fromApiJsonHelper.parameterExists("minDifferentialLendingRate", element)) {
                baseDataValidator
                        .reset()
                        .parameter("minDifferentialLendingRate")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.false",
                                "minDifferentialLendingRate param is not supported when isLinkedToFloatingInterestRates is not supplied or false");
            }

            if (this.fromApiJsonHelper.parameterExists("defaultDifferentialLendingRate", element)) {
                baseDataValidator
                        .reset()
                        .parameter("defaultDifferentialLendingRate")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.false",
                                "defaultDifferentialLendingRate param is not supported when isLinkedToFloatingInterestRates is not supplied or false");
            }

            if (this.fromApiJsonHelper.parameterExists("maxDifferentialLendingRate", element)) {
                baseDataValidator
                        .reset()
                        .parameter("maxDifferentialLendingRate")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.false",
                                "maxDifferentialLendingRate param is not supported when isLinkedToFloatingInterestRates is not supplied or false");
            }

            if (this.fromApiJsonHelper.parameterExists("isFloatingInterestRateCalculationAllowed", element)) {
                baseDataValidator
                        .reset()
                        .parameter("isFloatingInterestRateCalculationAllowed")
                        .failWithCode("not.supported.when.isLinkedToFloatingInterestRates.is.false",
                                "isFloatingInterestRateCalculationAllowed param is not supported when isLinkedToFloatingInterestRates is not supplied or false");
            }

            final String minInterestRatePerPeriodParameterName = "minInterestRatePerPeriod";
            BigDecimal minInterestRatePerPeriod = loanProduct.getMinNominalInterestRatePerPeriod();
            if (this.fromApiJsonHelper.parameterExists(minInterestRatePerPeriodParameterName, element)) {
                minInterestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minInterestRatePerPeriodParameterName,
                        element);
            }
            baseDataValidator.reset().parameter(minInterestRatePerPeriodParameterName).value(minInterestRatePerPeriod).ignoreIfNull()
                    .zeroOrPositiveAmount();

            final String maxInterestRatePerPeriodParameterName = "maxInterestRatePerPeriod";
            BigDecimal maxInterestRatePerPeriod = loanProduct.getMaxNominalInterestRatePerPeriod();
            if (this.fromApiJsonHelper.parameterExists(maxInterestRatePerPeriodParameterName, element)) {
                maxInterestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(maxInterestRatePerPeriodParameterName,
                        element);
            }
            baseDataValidator.reset().parameter(maxInterestRatePerPeriodParameterName).value(maxInterestRatePerPeriod).ignoreIfNull()
                    .zeroOrPositiveAmount();

            BigDecimal interestRatePerPeriod = loanProduct.getLoanProductRelatedDetail().getNominalInterestRatePerPeriod();
            if (this.fromApiJsonHelper.parameterExists("interestRatePerPeriod", element)) {
                interestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("interestRatePerPeriod", element);
            }
            baseDataValidator.reset().parameter("interestRatePerPeriod").value(interestRatePerPeriod).notNull().zeroOrPositiveAmount();

            Integer interestRateFrequencyType = loanProduct.getLoanProductRelatedDetail().getInterestPeriodFrequencyType().getValue();
            if (this.fromApiJsonHelper.parameterExists("interestRateFrequencyType", element)) {
                interestRateFrequencyType = this.fromApiJsonHelper.extractIntegerNamed("interestRateFrequencyType", element,
                        Locale.getDefault());
            }
            baseDataValidator.reset().parameter("interestRateFrequencyType").value(interestRateFrequencyType).notNull().inMinMaxRange(0, 4);
        }

        // Guarantee Funds
        Boolean holdGuaranteeFunds = loanProduct.isHoldGuaranteeFundsEnabled();
        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.holdGuaranteeFundsParamName, element)) {
            holdGuaranteeFunds = this.fromApiJsonHelper.extractBooleanNamed(LoanProductConstants.holdGuaranteeFundsParamName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.holdGuaranteeFundsParamName).value(holdGuaranteeFunds).notNull()
                    .isOneOfTheseValues(true, false);
        }

        if (holdGuaranteeFunds != null) {
            if (holdGuaranteeFunds) {
                validateGuaranteeParams(element, baseDataValidator, null);
            }
        }

        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.principalThresholdForLastInstallmentParamName, element)) {
            BigDecimal principalThresholdForLastInstallment = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                    LoanProductConstants.principalThresholdForLastInstallmentParamName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.principalThresholdForLastInstallmentParamName)
                    .value(principalThresholdForLastInstallment).notNull().notLessThanMin(BigDecimal.ZERO)
                    .notGreaterThanMax(BigDecimal.valueOf(100));
        }
        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.canDefineEmiAmountParamName, element)) {
            final Boolean canDefineInstallmentAmount = this.fromApiJsonHelper.extractBooleanNamed(
                    LoanProductConstants.canDefineEmiAmountParamName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.canDefineEmiAmountParamName).value(canDefineInstallmentAmount)
                    .isOneOfTheseValues(true, false);
        }

        if (this.fromApiJsonHelper.parameterExists(LoanProductConstants.installmentAmountInMultiplesOfParamName, element)) {
            final Integer installmentAmountInMultiplesOf = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(
                    LoanProductConstants.installmentAmountInMultiplesOfParamName, element);
            baseDataValidator.reset().parameter(LoanProductConstants.installmentAmountInMultiplesOfParamName)
                    .value(installmentAmountInMultiplesOf).ignoreIfNull().integerGreaterThanZero();
        }

        final Integer accountingRuleType = this.fromApiJsonHelper.extractIntegerNamed("accountingRule", element, Locale.getDefault());
        baseDataValidator.reset().parameter("accountingRule").value(accountingRuleType).ignoreIfNull().inMinMaxRange(1, 4);

        final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue()).value(fundAccountId).ignoreIfNull()
                .integerGreaterThanZero();

        final Long loanPortfolioAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue()).value(loanPortfolioAccountId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long transfersInSuspenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue())
                .value(transfersInSuspenseAccountId).ignoreIfNull().integerGreaterThanZero();

        final Long incomeFromInterestId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue()).value(incomeFromInterestId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long incomeFromFeeId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(),
                element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue()).value(incomeFromFeeId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long incomeFromPenaltyId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue()).value(incomeFromPenaltyId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long incomeFromRecoveryAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_RECOVERY.getValue())
                .value(incomeFromRecoveryAccountId).ignoreIfNull().integerGreaterThanZero();

        final Long writeOffAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue()).value(writeOffAccountId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long overpaymentAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(),
                element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue()).value(overpaymentAccountId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long receivableInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue())
                .value(receivableInterestAccountId).ignoreIfNull().integerGreaterThanZero();

        final Long receivableFeeAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue()).value(receivableFeeAccountId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long receivablePenaltyAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue())
                .value(receivablePenaltyAccountId).ignoreIfNull().integerGreaterThanZero();

        validateMinMaxConstraints(element, baseDataValidator);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }


    public void validateMinMaxConstraints(final JsonElement element, final DataValidatorBuilder baseDataValidator) {

        BigDecimal rateOfDecay = null;
        BigDecimal minRateOfDecay = null;
        BigDecimal maxRateOfDecay = null;

        if (this.fromApiJsonHelper.parameterExists(DepreciationProductConstants.rateOfDecay, element)) {
            rateOfDecay = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(DepreciationProductConstants.rateOfDecay, element);
            minRateOfDecay = minmaxValues.get(DepreciationProductConstants.minRateOfDecay);
            maxRateOfDecay = minmaxValues.get(DepreciationProductConstants.maxUsefullLife);
        }

        if ((minRateOfDecay != null && minRateOfDecay.compareTo(BigDecimal.ZERO) == 1)
                && (maxRateOfDecay != null && maxRateOfDecay.compareTo(BigDecimal.ZERO) == 1)) {
            baseDataValidator.reset().parameter(DepreciationProductConstants.rateOfDecay).value(rateOfDecay)
                    .inMinAndMaxAmountRange(minRateOfDecay, maxRateOfDecay);
        } else {
            if (minRateOfDecay != null && minRateOfDecay.compareTo(BigDecimal.ZERO) == 1) {
                baseDataValidator.reset().parameter(DepreciationProductConstants.rateOfDecay).value(rateOfDecay).notLessThanMin(minRateOfDecay);
            } else if (maxRateOfDecay != null && maxRateOfDecay.compareTo(BigDecimal.ZERO) == 1) {
                baseDataValidator.reset().parameter(DepreciationProductConstants.rateOfDecay).value(rateOfDecay).notGreaterThanMax(maxRateOfDecay);
            }
        }

        Integer usefullLife = null;
        Integer minUsefullLife = null;
        Integer maxUsefullLife  = null;
        if (this.fromApiJsonHelper.parameterExists(DepreciationProductConstants.usefullLife, element)) {
            usefullLife = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(DepreciationProductConstants.usefullLife, element);
            if (minmaxValues.get(DepreciationProductConstants.usefullLife) != null) {
                minUsefullLife = minmaxValues.get(DepreciationProductConstants.minUsefullLife).intValueExact();
            }
            if (minmaxValues.get(DepreciationProductConstants.maxUsefullLife) != null) {
                maxUsefullLife = minmaxValues.get(DepreciationProductConstants.maxUsefullLife).intValueExact();
            }
        }

        if (maxUsefullLife != null && maxUsefullLife.compareTo(0) == 1) {
            if (minUsefullLife != null && minUsefullLife.compareTo(0) == 1) {
                baseDataValidator.reset().parameter(DepreciationProductConstants.usefullLife).value(usefullLife)
                        .inMinMaxRange(minUsefullLife, maxUsefullLife);
            } else {
                baseDataValidator.reset().parameter(DepreciationProductConstants.usefullLife).value(usefullLife)
                        .notGreaterThanMax(maxUsefullLife);
            }
        } else if (minUsefullLife != null && minUsefullLife.compareTo(0) == 1) {
            baseDataValidator.reset().parameter(DepreciationProductConstants.usefullLife).value(usefullLife)
                    .notLessThanMin(minUsefullLife);
        }


    }

    private boolean isCashBasedAccounting(final Integer accountingRuleType) {
        return AccountingRuleType.CASH_BASED.getValue().equals(accountingRuleType);
    }

    private boolean isAccrualBasedAccounting(final Integer accountingRuleType) {
        return isUpfrontAccrualAccounting(accountingRuleType) || isPeriodicAccounting(accountingRuleType);
    }

    private boolean isUpfrontAccrualAccounting(final Integer accountingRuleType) {
        return AccountingRuleType.ACCRUAL_UPFRONT.getValue().equals(accountingRuleType);
    }

    private boolean isPeriodicAccounting(final Integer accountingRuleType) {
        return AccountingRuleType.ACCRUAL_PERIODIC.getValue().equals(accountingRuleType);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors);
        }
    }
}
