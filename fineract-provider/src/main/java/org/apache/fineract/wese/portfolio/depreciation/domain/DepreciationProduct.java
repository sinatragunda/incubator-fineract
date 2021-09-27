/*

    Created by Sinatra Gunda
    At 8:11 AM on 9/19/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.domain;

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
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.codes.domain.Code;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.wese.enumerations.DURATION_TYPE;
import org.apache.fineract.wese.portfolio.depreciation.enumerations.DEPRECIATION_METHOD;
import org.joda.time.LocalDate;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Loan products allow for categorisation of an organisations loans into
 * something meaningful to them.
 *
 * They provide a means of simplifying creation/maintenance of loans. They can
 * also allow for product comparison to take place when reporting.
 *
 * They allow for constraints to be added at product level.
 */
@Entity
@Table(name = "m_depreciation_product", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "unq_name"),
        @UniqueConstraint(columnNames = { "external_id" }, name = "external_id_UNIQUE"),
        @UniqueConstraint(columnNames = { "short_name" }, name = "unq_short_name") })
public class DepreciationProduct extends AbstractPersistableCustom<Long> {


    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "short_name", nullable = false, unique = true)
    private String shortName;

    @Column(name = "description")
    private String description;

    @Column(name = "currency_code" ,nullable = false)
    private String currencyCode;

    @Column(name = "tag" ,nullable = false)
    private String tag;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    @Column(name = "salvage_value", nullable = true)
    private BigDecimal salvageValue;

    @OneToOne(cascade = CascadeType.ALL, name = "asset_class_id", orphanRemoval = true, fetch=FetchType.EAGER)
    private CodeData assetClass;

    @Column(name ="is_appreciating_asset", nullable = true)
    private boolean isAppreciatingAsset = false;

    @Column(name = "is_zero_salvaged_asset", nullable = true)
    private boolean isZeroSalvagedAsset = false;

    @Column(name = "rate_of_decay", nullable = true)
    private BigDecimal rateOfDecay;

    @Column(name = "min_rate_of_decay", nullable = true)
    private BigDecimal minRateOfDecay;

    @Column(name = "max_rate_of_decay", nullable = true)
    private BigDecimal maxRateOfDecay;

    @Column(name = "usefull_life", nullable = true)
    private Integer usefullLife;

    @Column(name = "min_usefull_life", nullable = true)
    private Integer minUsefullLife;

    @Column(name = "max_usefull_life", nullable = true)
    private Integer maxUsefullLife;

    @Column(name = "userfull_life_timer", nullable = true)
    private DURATION_TYPE usefullLifeTimer;

    @Column(name = "depreciation_method", nullable = true)
    private DEPRECIATION_METHOD depreciationMethod ;

    @Column(name = "accounting_type", nullable = false)
    private Integer accountingRule;


    public static DepreciationProduct assembleFromJson(final JsonCommand command) {
        final String name = command.stringValueOfParameterNamed("name");
        final String shortName = command.stringValueOfParameterNamed(LoanProductConstants.shortName);
        final String description = command.stringValueOfParameterNamed("description");
        final String currencyCode = command.stringValueOfParameterNamed("currencyCode");
        final String tag = command.stringValueOfParameterNamed(DepreciationProductConstants.tag);
        final String externalId = command.stringValueOfParameterNamed(DepreciationProductConstants.externalId);


        final Long assetClassId = command.longValueOfParameterNamed(DepreciationProductConstants.assetClass);
        CodeData assetClass = null ;
        if(assetClassId != null){
            assetClass = CodeData.instance(assetClassId ,null ,false);
        }

        final BigDecimal salvageValue = command.bigDecimalValueOfParameterNamed(DepreciationProductConstants.salvageValue);
        final BigDecimal rateOfDecay = command.bigDecimalValueOfParameterNamed(DepreciationProductConstants.rateOfDecay);
        final BigDecimal minRateOfDecay = command.bigDecimalValueOfParameterNamed(DepreciationProductConstants.minRateOfDecay);
        final BigDecimal maxRateOfDecay = command.bigDecimalValueOfParameterNamed(DepreciationProductConstants.maxRateOfDecay);

        final DEPRECIATION_METHOD depreciationMethod = DEPRECIATION_METHOD.fromInt(command.integerValueOfParameterNamed("depreciationMethod"));

        final DURATION_TYPE usefullLifeTimer = DURATION_TYPE.fromInt(command.integerValueOfParameterNamed(DepreciationProductConstants.durationType));

        final AccountingRuleType accountingRuleType = AccountingRuleType.fromInt(command.integerValueOfParameterNamed("accountingRule"));

        final Integer usefullLife = command.integerValueOfParameterNamed("usefullLife");
        final Integer minUsefullLife = command.integerValueOfParameterNamed("minUsefullLife");
        final Integer maxUsefullLife = command.integerValueOfParameterNamed("maxUsefullLife");

        final boolean isZeroSalvagedAsset = command.booleanPrimitiveValueOfParameterNamed(DepreciationProductConstants.isZeroSalvagedAsset);

        final boolean isAppreciatingAsset = command
                .booleanPrimitiveValueOfParameterNamed(DepreciationProductConstants.isAppreciatingAsset);


        return new DepreciationProduct(name, shortName, description,currencyCode,tag ,externalId,salvageValue ,assetClass ,isAppreciatingAsset,isZeroSalvagedAsset, rateOfDecay, minRateOfDecay,
                maxRateOfDecay,usefullLife ,minUsefullLife ,maxUsefullLife ,usefullLifeTimer ,depreciationMethod ,accountingRuleType.getValue());    }

    public DepreciationProduct(String name, String shortName, String description, String currencyCode, String tag, String externalId, BigDecimal salvageValue, CodeData assetClass, boolean isAppreciatingAsset, boolean isZeroSalvagedAsset, BigDecimal rateOfDecay, BigDecimal minRateOfDecay, BigDecimal maxRateOfDecay, Integer usefullLife, Integer minUsefullLife, Integer maxUsefullLife, DURATION_TYPE userfullLifeTimer, DEPRECIATION_METHOD depreciationMethod, Integer accountingRule) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.currencyCode = currencyCode;
        this.tag = tag;
        this.externalId = externalId;
        this.salvageValue = salvageValue;
        this.assetClass = assetClass;
        this.isAppreciatingAsset = isAppreciatingAsset;
        this.isZeroSalvagedAsset = isZeroSalvagedAsset;
        this.rateOfDecay = rateOfDecay;
        this.minRateOfDecay = minRateOfDecay;
        this.maxRateOfDecay = maxRateOfDecay;
        this.usefullLife = usefullLife;
        this.minUsefullLife = minUsefullLife;
        this.maxUsefullLife = maxUsefullLife;
        this.usefullLifeTimer = userfullLifeTimer;
        this.depreciationMethod = depreciationMethod;
        this.accountingRule = accountingRule;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(20);

        if (command.isChangeInBooleanParameterNamed(DepreciationProductConstants.isAppreciatingAsset, this.isAppreciatingAsset)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(DepreciationProductConstants.isAppreciatingAsset);
            actualChanges.put(DepreciationProductConstants.isAppreciatingAsset, newValue);
            this.isAppreciatingAsset = newValue;
        }


        final String accountingTypeParamName = "accountingRule";
        if (command.isChangeInIntegerParameterNamed(accountingTypeParamName, this.accountingRule)) {
            final Integer newValue = command.integerValueOfParameterNamed(accountingTypeParamName);
            actualChanges.put(accountingTypeParamName, newValue);
            this.accountingRule = newValue;
        }

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = newValue;
        }

        if (command.isChangeInStringParameterNamed(DepreciationProductConstants.externalId, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(DepreciationProductConstants.externalId);
            actualChanges.put(DepreciationProductConstants.externalId, newValue);
            this.externalId = newValue;
        }

        if (command.isChangeInStringParameterNamed(DepreciationProductConstants.tag, this.tag)) {
            final String newValue = command.stringValueOfParameterNamed(DepreciationProductConstants.tag);
            actualChanges.put(DepreciationProductConstants.tag, newValue);
            this.tag = newValue;
        }

        final String shortNameParamName = LoanProductConstants.shortName;
        if (command.isChangeInStringParameterNamed(shortNameParamName, this.shortName)) {
            final String newValue = command.stringValueOfParameterNamed(shortNameParamName);
            actualChanges.put(shortNameParamName, newValue);
            this.shortName = newValue;
        }

        final String descriptionParamName = "description";
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.description = newValue;
        }

        if (command.isChangeInStringParameterNamed(DepreciationProductConstants.currencyCode, this.currencyCode)) {
            final String newValue = command.stringValueOfParameterNamed(DepreciationProductConstants.currencyCode);
            actualChanges.put(DepreciationProductConstants.currencyCode, newValue);
            this.currencyCode = newValue;
        }


        Long assetClassId = null;
        if (this.assetClass == null) {
            assetClassId = this.assetClass.getCodeId();
        }

        if (command.isChangeInLongParameterNamed(DepreciationProductConstants.assetClass, assetClassId)) {
            final Long newValue = command.longValueOfParameterNamed(DepreciationProductConstants.assetClass);
            actualChanges.put(DepreciationProductConstants.assetClass, newValue);
        }


        if (command.isChangeInBooleanParameterNamed(DepreciationProductConstants.isZeroSalvagedAsset, this.isZeroSalvagedAsset)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(DepreciationProductConstants.isZeroSalvagedAsset);
            actualChanges.put(DepreciationProductConstants.isZeroSalvagedAsset, newValue);
            this.isZeroSalvagedAsset = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(DepreciationProductConstants.salvageValue, this.salvageValue)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(DepreciationProductConstants.salvageValue);
            actualChanges.put(DepreciationProductConstants.salvageValue, newValue);
            this.salvageValue = newValue;
        }
        if (command.isChangeInBigDecimalParameterNamed(DepreciationProductConstants.rateOfDecay, this.rateOfDecay)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(DepreciationProductConstants.rateOfDecay);
            actualChanges.put(DepreciationProductConstants.rateOfDecay, newValue);
            this.rateOfDecay = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(DepreciationProductConstants.maxRateOfDecay, this.maxRateOfDecay)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(DepreciationProductConstants.maxRateOfDecay);
            actualChanges.put(DepreciationProductConstants.maxRateOfDecay, newValue);
            this.maxRateOfDecay = newValue;
        }
        if (command.isChangeInBigDecimalParameterNamed(DepreciationProductConstants.rateOfDecay, this.minRateOfDecay)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(DepreciationProductConstants.minRateOfDecay);
            actualChanges.put(DepreciationProductConstants.minRateOfDecay, newValue);
            this.minRateOfDecay = newValue ;
        }

        if (command.isChangeInIntegerParameterNamed(DepreciationProductConstants.usefullLife,
                this.usefullLife)) {
            Integer newValue = command
                    .integerValueOfParameterNamed(DepreciationProductConstants.usefullLife);
            actualChanges.put(DepreciationProductConstants.usefullLife, newValue);
            this.usefullLife = newValue;
        }
        if (command.isChangeInIntegerParameterNamed(DepreciationProductConstants.minUsefullLife,
                this.minUsefullLife)) {
            Integer newValue = command
                    .integerValueOfParameterNamed(DepreciationProductConstants.minUsefullLife);
            actualChanges.put(DepreciationProductConstants.minUsefullLife, newValue);
            this.minUsefullLife = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(DepreciationProductConstants.maxUsefullLife,
                this.maxUsefullLife)) {
            Integer newValue = command
                    .integerValueOfParameterNamed(DepreciationProductConstants.maxUsefullLife);
            actualChanges.put(DepreciationProductConstants.maxUsefullLife, newValue);
            this.maxUsefullLife = newValue;
        }


        if (command.isChangeInIntegerParameterNamed(DepreciationProductConstants.depreciationMethod,
                this.depreciationMethod.ordinal())) {
            final Integer newValue = command.integerValueOfParameterNamed(DepreciationProductConstants.depreciationMethod);
            actualChanges.put(DepreciationProductConstants.depreciationMethod, newValue);
            this.depreciationMethod = DEPRECIATION_METHOD.fromInt(newValue);
        }

        if (command.isChangeInIntegerParameterNamed(DepreciationProductConstants.usefullLifeTimer,
                this.usefullLifeTimer.ordinal())) {
            final Integer newValue = command.integerValueOfParameterNamed(DepreciationProductConstants.usefullLifeTimer);
            actualChanges.put(DepreciationProductConstants.usefullLifeTimer, newValue);
            this.usefullLifeTimer = DURATION_TYPE.fromInt(newValue);
        }

        return actualChanges;
    }


    public boolean isAccountingDisabled() {
        return AccountingRuleType.NONE.getValue().equals(this.accountingRule);
    }

    public boolean isCashBasedAccountingEnabled() {
        return AccountingRuleType.CASH_BASED.getValue().equals(this.accountingRule);
    }

    public boolean isAccrualBasedAccountingEnabled() {
        return isUpfrontAccrualAccountingEnabled() || isPeriodicAccrualAccountingEnabled();
    }

    public boolean isUpfrontAccrualAccountingEnabled() {
        return AccountingRuleType.ACCRUAL_UPFRONT.getValue().equals(this.accountingRule);
    }

    public boolean isPeriodicAccrualAccountingEnabled() {
        return AccountingRuleType.ACCRUAL_PERIODIC.getValue().equals(this.accountingRule);
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getTag() {
        return tag;
    }

    public String getExternalId() {
        return externalId;
    }

    public BigDecimal getSalvageValue() {
        return salvageValue;
    }

    public CodeData getAssetClass() {
        return assetClass;
    }

    public boolean isAppreciatingAsset() {
        return isAppreciatingAsset;
    }

    public boolean isZeroSalvagedAsset() {
        return isZeroSalvagedAsset;
    }

    public BigDecimal getRateOfDecay() {
        return rateOfDecay;
    }

    public BigDecimal getMinRateOfDecay() {
        return minRateOfDecay;
    }

    public BigDecimal getMaxRateOfDecay() {
        return maxRateOfDecay;
    }

    public Integer getUsefullLife() {
        return usefullLife;
    }

    public Integer getMinUsefullLife() {
        return minUsefullLife;
    }

    public Integer getMaxUsefullLife() {
        return maxUsefullLife;
    }

    public DURATION_TYPE getUsefullLifeTimer() {
        return usefullLifeTimer;
    }

    public DEPRECIATION_METHOD getDepreciationMethod() {
        return depreciationMethod;
    }

    public Integer getAccountingRule() {
        return accountingRule;
    }
}
