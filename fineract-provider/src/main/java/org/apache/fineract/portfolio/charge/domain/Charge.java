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
package org.apache.fineract.portfolio.charge.domain;

import java.math.BigDecimal;
import java.util.*;



import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.CascadeType;
import javax.persistence.OneToOne;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.journalentry.data.TransactionCodeData;
import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.data.ChargePropertiesData;
import org.apache.fineract.portfolio.charge.data.ChargeTierData;
import org.apache.fineract.portfolio.charge.exception.ChargeDueAtDisbursementCannotBePenaltyException;
import org.apache.fineract.portfolio.charge.exception.ChargeMustBePenaltyException;
import org.apache.fineract.portfolio.charge.exception.ChargeParameterUpdateNotSupportedException;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.apache.fineract.portfolio.tax.domain.TaxGroup;
import org.joda.time.MonthDay;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "m_charge", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "name") })
public class Charge extends AbstractPersistableCustom<Long> {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "charge_applies_to_enum", nullable = false)
    private Integer chargeAppliesTo;

    @Column(name = "charge_time_enum", nullable = false)
    private Integer chargeTimeType;

    @Column(name = "charge_calculation_enum")
    private Integer chargeCalculation;

    @Column(name = "charge_payment_mode_enum", nullable = true)
    private Integer chargePaymentMode;

    @Column(name = "fee_on_day", nullable = true)
    private Integer feeOnDay;

    @Column(name = "fee_interval", nullable = true)
    private Integer feeInterval;

    @Column(name = "fee_on_month", nullable = true)
    private Integer feeOnMonth;

    @Column(name = "is_penalty", nullable = false)
    private boolean penalty;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "min_cap", scale = 6, precision = 19, nullable = true)
    private BigDecimal minCap;

    @Column(name = "max_cap", scale = 6, precision = 19, nullable = true)
    private BigDecimal maxCap;

    @Column(name = "fee_frequency", nullable = true)
    private Integer feeFrequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_or_liability_account_id")
    private GLAccount account;

    @ManyToOne
    @JoinColumn(name = "tax_group_id")
    private TaxGroup taxGroup;

    /**
     * Added 10/09/2022 ar 0425
     * Intention is to have some charges have optional transaction codes ie account to debit and credit 
     */

    @ManyToOne
    @JoinColumn(name = "transaction_code_id")
    private TransactionCode transactionCode;

    /**
     * Added 12/10/2022 at 1407
     */
    @Transient
    private List<ChargeTier> chargeTierList;

    /**
     * Added 16/02/2023 at 0225
     * Charge additional settings .
     */
    @OneToOne
    @JoinColumn(name = "charge_properties_id")
    private ChargeProperties chargeProperties;

    public static Charge fromJson(final JsonCommand command, final GLAccount account, final TaxGroup taxGroup,final TransactionCode transactionCode ,final ChargeProperties chargeProperties) {

        final String name = command.stringValueOfParameterNamed("name");
        final BigDecimal amount = command.bigDecimalValueOfParameterNamed("amount");
        final String currencyCode = command.stringValueOfParameterNamed("currencyCode");

        final ChargeAppliesTo chargeAppliesTo = ChargeAppliesTo.fromInt(command.integerValueOfParameterNamed("chargeAppliesTo"));
        final ChargeTimeType chargeTimeType = ChargeTimeType.fromInt(command.integerValueOfParameterNamed("chargeTimeType"));
        final ChargeCalculationType chargeCalculationType = ChargeCalculationType.fromInt(command
                .integerValueOfParameterNamed("chargeCalculationType"));
        final Integer chargePaymentMode = command.integerValueOfParameterNamed("chargePaymentMode");

        final ChargePaymentMode paymentMode = chargePaymentMode == null ? null : ChargePaymentMode.fromInt(chargePaymentMode);

        final boolean penalty = command.booleanPrimitiveValueOfParameterNamed("penalty");
        final boolean active = command.booleanPrimitiveValueOfParameterNamed("active");
        final MonthDay feeOnMonthDay = command.extractMonthDayNamed("feeOnMonthDay");
        final Integer feeInterval = command.integerValueOfParameterNamed("feeInterval");
        final BigDecimal minCap = command.bigDecimalValueOfParameterNamed("minCap");
        final BigDecimal maxCap = command.bigDecimalValueOfParameterNamed("maxCap");
        final Integer feeFrequency = command.integerValueOfParameterNamed("feeFrequency");

        return new Charge(name, amount, currencyCode, chargeAppliesTo, chargeTimeType, chargeCalculationType, penalty, active, paymentMode,
                feeOnMonthDay, feeInterval, minCap, maxCap, feeFrequency, account, taxGroup ,transactionCode,chargeProperties);
    }

    protected Charge() {
        //
    }

    private Charge(final String name, final BigDecimal amount, final String currencyCode, final ChargeAppliesTo chargeAppliesTo,
            final ChargeTimeType chargeTime, final ChargeCalculationType chargeCalculationType, final boolean penalty,
            final boolean active, final ChargePaymentMode paymentMode, final MonthDay feeOnMonthDay, final Integer feeInterval,
            final BigDecimal minCap, final BigDecimal maxCap, final Integer feeFrequency, final GLAccount account, final TaxGroup taxGroup ,final TransactionCode transactionCode,final ChargeProperties chargeProperties) {
        this.name = name;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.chargeAppliesTo = chargeAppliesTo.getValue();
        this.chargeTimeType = chargeTime.getValue();
        this.chargeCalculation = chargeCalculationType.getValue();
        this.penalty = penalty;
        this.active = active;
        this.account = account;
        this.taxGroup = taxGroup;
        this.chargePaymentMode = paymentMode == null ? null : paymentMode.getValue();
        this.transactionCode = transactionCode;
        this.chargeProperties = chargeProperties;

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("charges");

        if (isMonthlyFee() || isAnnualFee()) {
            this.feeOnMonth = feeOnMonthDay.getMonthOfYear();
            this.feeOnDay = feeOnMonthDay.getDayOfMonth();
        }
        this.feeInterval = feeInterval;
        this.feeFrequency = feeFrequency;

        if (isSavingsCharge()) {
            // TODO vishwas, this validation seems unnecessary as identical
            // validation is performed in the write service
            if (!isAllowedSavingsChargeTime()) {
                baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                        .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.savings");
            }
            // TODO vishwas, this validation seems unnecessary as identical
            // validation is performed in the writeservice
            if (!isAllowedSavingsChargeCalculationType()) {
                baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                        .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.calculation.type.for.savings");
            }
            /**
             * Modified 06/11/2022 at 2003
             * Modified to allow deposit fee to allow calculation by percentage
             */
            if (!(ChargeTimeType.fromInt(getChargeTimeType()).isWithdrawalFee() || ChargeTimeType.fromInt(getChargeTimeType()).isDepositFee() || ChargeTimeType.fromInt(getChargeTimeType()).isSavingsNoActivityFee())
                    && ChargeCalculationType.fromInt(getChargeCalculation()).isPercentageOfAmount()) {
                baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                        .failWithCodeNoParameterAddedToErrorCode("savings.charge.calculation.type.percentage.allowed.only.for.withdrawal.or.NoActivity");
            }

        } else if (isLoanCharge()) {

            if (penalty && (chargeTime.isTimeOfDisbursement() || chargeTime.isTrancheDisbursement())) { throw new ChargeDueAtDisbursementCannotBePenaltyException(
                    name); }
            if (!penalty && chargeTime.isOverdueInstallment()) { throw new ChargeMustBePenaltyException(name); }
            // TODO vishwas, this validation seems unnecessary as identical
            // validation is performed in the write service
            if (!isAllowedLoanChargeTime()) {
                baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                        .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.loan");
            }
        }
        if (isPercentageOfApprovedAmount()) {
            this.minCap = minCap;
            this.maxCap = maxCap;
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final LoanCharge rhs = (LoanCharge) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)) //
                .append(getId(), rhs.getId()) //
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 5) //
                .append(getId()) //
                .toHashCode();
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Integer getChargeTimeType() {
        return this.chargeTimeType;
    }

    public Integer getChargeCalculation() {
        return this.chargeCalculation;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isPenalty() {
        return this.penalty;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public boolean isLoanCharge() {
        return ChargeAppliesTo.fromInt(this.chargeAppliesTo).isLoanCharge();
    }

    public boolean isAllowedLoanChargeTime() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isAllowedLoanChargeTime();
    }

    public boolean isAllowedClientChargeTime() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isAllowedClientChargeTime();
    }

    public boolean isSavingsCharge() {
        return ChargeAppliesTo.fromInt(this.chargeAppliesTo).isSavingsCharge();
    }

    public boolean isClientCharge() {
        return ChargeAppliesTo.fromInt(this.chargeAppliesTo).isClientCharge();
    }

    public boolean isAllowedSavingsChargeTime() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isAllowedSavingsChargeTime();
    }

    public boolean isAllowedSavingsChargeCalculationType() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isAllowedSavingsChargeCalculationType();
    }

    public boolean isAllowedClientChargeCalculationType() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isAllowedClientChargeCalculationType();
    }

    public boolean isPercentageOfApprovedAmount() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isPercentageOfAmount();
    }

    public boolean isPercentageOfDisbursementAmount() {
        return ChargeCalculationType.fromInt(this.chargeCalculation).isPercentageOfDisbursementAmount();
    }

    public BigDecimal getMinCap() {
        return this.minCap;
    }

    public BigDecimal getMaxCap() {
        return this.maxCap;
    }

    public TransactionCode getTransactionCode(){
        return this.transactionCode;
    }

    public Long getTransactionCodeId(){
        boolean hasCode = Optional.ofNullable(this.transactionCode).isPresent();
        Long transactionCodeId = null ;
        if(hasCode){
            transactionCodeId = this.transactionCode.getId();
        }
        return transactionCodeId;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String localeAsInput = command.locale();

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("charges");

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = newValue;
        }

        final String currencyCodeParamName = "currencyCode";
        if (command.isChangeInStringParameterNamed(currencyCodeParamName, this.currencyCode)) {
            final String newValue = command.stringValueOfParameterNamed(currencyCodeParamName);
            actualChanges.put(currencyCodeParamName, newValue);
            this.currencyCode = newValue;
        }

        final String amountParamName = "amount";
        if (command.isChangeInBigDecimalParameterNamed(amountParamName, this.amount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(amountParamName);
            actualChanges.put(amountParamName, newValue);
            actualChanges.put("locale", localeAsInput);
            this.amount = newValue;
        }

        final String chargeTimeParamName = "chargeTimeType";
        if (command.isChangeInIntegerParameterNamed(chargeTimeParamName, this.chargeTimeType)) {
            final Integer newValue = command.integerValueOfParameterNamed(chargeTimeParamName);
            actualChanges.put(chargeTimeParamName, newValue);
            actualChanges.put("locale", localeAsInput);
            this.chargeTimeType = ChargeTimeType.fromInt(newValue).getValue();

            if (isSavingsCharge()) {
                if (!isAllowedSavingsChargeTime()) {
                    baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.savings");
                }
                // if charge time is changed to monthly then validate for
                // feeOnMonthDay and feeInterval
                if (isMonthlyFee()) {
                    final MonthDay monthDay = command.extractMonthDayNamed("feeOnMonthDay");
                    baseDataValidator.reset().parameter("feeOnMonthDay").value(monthDay).notNull();

                    final Integer feeInterval = command.integerValueOfParameterNamed("feeInterval");
                    baseDataValidator.reset().parameter("feeInterval").value(feeInterval).notNull().inMinMaxRange(1, 12);
                }
            } else if (isLoanCharge()) {
                if (!isAllowedLoanChargeTime()) {
                    baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.loan");
                }
            } else if (isClientCharge()) {
                if (!isAllowedLoanChargeTime()) {
                    baseDataValidator.reset().parameter("chargeTimeType").value(this.chargeTimeType)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.time.for.client");
                }
            }
        }

        final String chargeAppliesToParamName = "chargeAppliesTo";
        if (command.isChangeInIntegerParameterNamed(chargeAppliesToParamName, this.chargeAppliesTo)) {
            /*
             * final Integer newValue =
             * command.integerValueOfParameterNamed(chargeAppliesToParamName);
             * actualChanges.put(chargeAppliesToParamName, newValue);
             * actualChanges.put("locale", localeAsInput); this.chargeAppliesTo
             * = ChargeAppliesTo.fromInt(newValue).getValue();
             */

            // AA: Do not allow to change chargeAppliesTo.
            final String errorMessage = "Update of Charge applies to is not supported";
            throw new ChargeParameterUpdateNotSupportedException("charge.applies.to", errorMessage);
        }

        final String chargeCalculationParamName = "chargeCalculationType";
        if (command.isChangeInIntegerParameterNamed(chargeCalculationParamName, this.chargeCalculation)) {
            final Integer newValue = command.integerValueOfParameterNamed(chargeCalculationParamName);
            actualChanges.put(chargeCalculationParamName, newValue);
            actualChanges.put("locale", localeAsInput);
            this.chargeCalculation = ChargeCalculationType.fromInt(newValue).getValue();

            if (isSavingsCharge()) {
                if (!isAllowedSavingsChargeCalculationType()) {
                    baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.calculation.type.for.savings");
                }

                if (!(ChargeTimeType.fromInt(getChargeTimeType()).isWithdrawalFee() || ChargeTimeType.fromInt(getChargeTimeType()).isDepositFee() || ChargeTimeType.fromInt(getChargeTimeType()).isSavingsNoActivityFee())
                        && ChargeCalculationType.fromInt(getChargeCalculation()).isPercentageOfAmount()) {
                    baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                            .failWithCodeNoParameterAddedToErrorCode("charge.calculation.type.percentage.allowed.only.for.withdrawal.or.noactivity");
                }
            } else if (isClientCharge()) {
                if (!isAllowedClientChargeCalculationType()) {
                    baseDataValidator.reset().parameter("chargeCalculationType").value(this.chargeCalculation)
                            .failWithCodeNoParameterAddedToErrorCode("not.allowed.charge.calculation.type.for.client");
                }
            }
        }

        if (isLoanCharge()) {// validate only for loan charge
            final String paymentModeParamName = "chargePaymentMode";
            if (command.isChangeInIntegerParameterNamed(paymentModeParamName, this.chargePaymentMode)) {
                final Integer newValue = command.integerValueOfParameterNamed(paymentModeParamName);
                actualChanges.put(paymentModeParamName, newValue);
                actualChanges.put("locale", localeAsInput);
                this.chargePaymentMode = ChargePaymentMode.fromInt(newValue).getValue();
            }
        }

        if (command.hasParameter("feeOnMonthDay")) {
            final MonthDay monthDay = command.extractMonthDayNamed("feeOnMonthDay");
            final String actualValueEntered = command.stringValueOfParameterNamed("feeOnMonthDay");
            final Integer dayOfMonthValue = monthDay.getDayOfMonth();
            if (this.feeOnDay != dayOfMonthValue) {
                actualChanges.put("feeOnMonthDay", actualValueEntered);
                actualChanges.put("locale", localeAsInput);
                this.feeOnDay = dayOfMonthValue;
            }

            final Integer monthOfYear = monthDay.getMonthOfYear();
            if (this.feeOnMonth != monthOfYear) {
                actualChanges.put("feeOnMonthDay", actualValueEntered);
                actualChanges.put("locale", localeAsInput);
                this.feeOnMonth = monthOfYear;
            }
        }

        final String feeInterval = "feeInterval";
        if (command.isChangeInIntegerParameterNamed(feeInterval, this.feeInterval)) {
            final Integer newValue = command.integerValueOfParameterNamed(feeInterval);
            actualChanges.put(feeInterval, newValue);
            actualChanges.put("locale", localeAsInput);
            this.feeInterval = newValue;
        }

        final String feeFrequency = "feeFrequency";
        if (command.isChangeInIntegerParameterNamed(feeFrequency, this.feeFrequency)) {
            final Integer newValue = command.integerValueOfParameterNamed(feeFrequency);
            actualChanges.put(feeFrequency, newValue);
            actualChanges.put("locale", localeAsInput);
            this.feeFrequency = newValue;
        }

        if (this.feeFrequency != null) {
            baseDataValidator.reset().parameter("feeInterval").value(this.feeInterval).notNull();
        }

        final String penaltyParamName = "penalty";
        if (command.isChangeInBooleanParameterNamed(penaltyParamName, this.penalty)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(penaltyParamName);
            actualChanges.put(penaltyParamName, newValue);
            this.penalty = newValue;
        }

        final String activeParamName = "active";
        if (command.isChangeInBooleanParameterNamed(activeParamName, this.active)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(activeParamName);
            actualChanges.put(activeParamName, newValue);
            this.active = newValue;
        }
        // allow min and max cap to be only added to PERCENT_OF_AMOUNT for now
        if (isPercentageOfApprovedAmount()) {
            final String minCapParamName = "minCap";
            if (command.isChangeInBigDecimalParameterNamed(minCapParamName, this.minCap)) {
                final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(minCapParamName);
                actualChanges.put(minCapParamName, newValue);
                actualChanges.put("locale", localeAsInput);
                this.minCap = newValue;
            }
            final String maxCapParamName = "maxCap";
            if (command.isChangeInBigDecimalParameterNamed(maxCapParamName, this.maxCap)) {
                final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(maxCapParamName);
                actualChanges.put(maxCapParamName, newValue);
                actualChanges.put("locale", localeAsInput);
                this.maxCap = newValue;
            }

        }

        if (this.penalty && ChargeTimeType.fromInt(this.chargeTimeType).isTimeOfDisbursement()) { throw new ChargeDueAtDisbursementCannotBePenaltyException(
                this.name); }
        if (!penalty && ChargeTimeType.fromInt(this.chargeTimeType).isOverdueInstallment()) { throw new ChargeMustBePenaltyException(name); }

        if (command.isChangeInLongParameterNamed(ChargesApiConstants.glAccountIdParamName, getIncomeAccountId())) {
            final Long newValue = command.longValueOfParameterNamed(ChargesApiConstants.glAccountIdParamName);
            actualChanges.put(ChargesApiConstants.glAccountIdParamName, newValue);
        }

        if (command.isChangeInLongParameterNamed(ChargesApiConstants.taxGroupIdParamName, getTaxGroupId())) {
            final Long newValue = command.longValueOfParameterNamed(ChargesApiConstants.taxGroupIdParamName);
            actualChanges.put(ChargesApiConstants.taxGroupIdParamName, newValue);
            if(taxGroup != null){
                baseDataValidator.reset().parameter(ChargesApiConstants.taxGroupIdParamName).failWithCode("modification.not.supported");
            }
        }
        
        if (command.isChangeInLongParameterNamed(ChargesApiConstants.transactionCodeIdParamName, getTransactionCodeId())){
            final Long newValue = command.longValueOfParameterNamed(ChargesApiConstants.transactionCodeIdParamName);
            System.err.println("-------------------new value is "+newValue);
            actualChanges.put(ChargesApiConstants.transactionCodeIdParamName, newValue);
            if(newValue != null){
                baseDataValidator.reset().parameter(ChargesApiConstants.transactionCodeIdParamName).notNull().positiveAmount();
            }
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

        return actualChanges;
    }

    /**
     * Delete is a <i>soft delete</i>. Updates flag on charge so it wont appear
     * in query/report results.
     * 
     * Any fields with unique constraints and prepended with id of record.
     */
    public void delete() {
        this.deleted = true;
        this.name = getId() + "_" + this.name;
    }

    public ChargeData toData() {

        final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(this.chargeTimeType);
        final EnumOptionData chargeAppliesTo = ChargeEnumerations.chargeAppliesTo(this.chargeAppliesTo);
        final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(this.chargeCalculation);
        final EnumOptionData chargePaymentmode = ChargeEnumerations.chargePaymentMode(this.chargePaymentMode);
        final EnumOptionData feeFrequencyType = ChargeEnumerations.chargePaymentMode(this.feeFrequency);
        GLAccountData accountData = null;
        if (account != null) {
            accountData = new GLAccountData(account.getId(), account.getName(), account.getGlCode());
        }
        TaxGroupData taxGroupData = null;
        if (this.taxGroup != null) {
            taxGroupData = TaxGroupData.lookup(taxGroup.getId(), taxGroup.getName());
        }

        TransactionCodeData transactionCodeData[] = {null};
        Optional.ofNullable(this.transactionCode).ifPresent(e->{
            transactionCodeData[0] = TransactionCodeData.looup(transactionCode.getId() ,transactionCode.getCode() ,transactionCode.getName());
        });

        ChargePropertiesData chargePropertiesData = null ;

        /**
         * Added 01/11/2022 at 0728
         * Class still under construction ,need to just edit this line of code to make it compile
         */
        List<ChargeTierData> chargeTierDataList = new ArrayList<>();

        final CurrencyData currency = new CurrencyData(this.currencyCode, null, 0, 0, null, null);
        return ChargeData.instance(getId(), this.name, this.amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType,
                chargePaymentmode, getFeeOnMonthDay(), this.feeInterval, this.penalty, this.active, this.minCap, this.maxCap,
                feeFrequencyType, accountData, taxGroupData ,transactionCodeData[0] ,chargeTierDataList,chargePropertiesData);
    }

    public Integer getChargePaymentMode() {
        return this.chargePaymentMode;
    }

    public Integer getFeeInterval() {
        return this.feeInterval;
    }

    public boolean isMonthlyFee() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isMonthlyFee();
    }

    public boolean isAnnualFee() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isAnnualFee();
    }

    public boolean isOverdueInstallment() {
        return ChargeTimeType.fromInt(this.chargeTimeType).isOverdueInstallment();
    }

    public MonthDay getFeeOnMonthDay() {
        MonthDay feeOnMonthDay = null;
        if (this.feeOnDay != null && this.feeOnMonth != null) {
            feeOnMonthDay = new MonthDay(this.feeOnMonth, this.feeOnDay);
        }
        return feeOnMonthDay;
    }

    public Integer feeInterval() {
        return this.feeInterval;
    }

    public Integer feeFrequency() {
        return this.feeFrequency;
    }

    public GLAccount getAccount() {
        return this.account;
    }

    public void setAccount(GLAccount account) {
        this.account = account;
    }

    private Long getIncomeAccountId() {
        Long incomeAccountId = null;
        if (this.account != null) {
            incomeAccountId = this.account.getId();
        }
        return incomeAccountId;
    }

    private Long getTaxGroupId() {
        Long taxGroupId = null;
        if (this.taxGroup != null) {
            taxGroupId = this.taxGroup.getId();
        }
        return taxGroupId;
    }

    public boolean isDisbursementCharge() {
        return ChargeTimeType.fromInt(this.chargeTimeType).equals(ChargeTimeType.DISBURSEMENT)
                || ChargeTimeType.fromInt(this.chargeTimeType).equals(ChargeTimeType.TRANCHE_DISBURSEMENT);
    }

    public TaxGroup getTaxGroup() {
        return this.taxGroup;
    }

    public void setTaxGroup(TaxGroup taxGroup) {
        this.taxGroup = taxGroup;
    }


    public List<ChargeTier> getChargeTier(){
        return this.chargeTierList;
    }

    public void setChargeTier(List<ChargeTier> chargeTierList){
        this.chargeTierList = chargeTierList;
    }

    public ChargeProperties getChargeProperties(){
        return this.chargeProperties;
    }

    public boolean isAgentCharge(){
        // this for old classes without properties set at start
        boolean hasProperties = Optional.ofNullable(this.chargeProperties).isPresent();
        //System.err.println("------------get charge properties for charge "+this.name+"------------- hasProperties "+hasProperties);

        if(hasProperties){
            hasProperties = this.chargeProperties.isCommissionedCharge();    
        }
        return hasProperties;
    }
}