/*

    Created by Sinatra Gunda
    At 10:18 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.charge.domain.ChargeAppliesTo;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.commissions.enumerations.LOAN_COMMISSION_CHARGE_TIME;
import org.apache.fineract.wese.enumerations.CHARGE_CALCULATION_CRITERIA;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import javax.persistence.Enumerated;

@Entity
@Table(name = "m_loan_commission_charge")
public class CommissionCharge extends AbstractPersistableCustom<Long>{

    @Column(name="name" ,nullable=false)
    private String name ;


    @Column(name="currency_code" ,nullable=false)
    private String currencyCode ;


    @Column(name="amount" ,nullable=false)
    private BigDecimal amount ;

    @Enumerated
    @Column(name = "charge_time_type", nullable = false)
    private ChargeTimeType chargeTimeType;

    @Enumerated
    @Column(name = "charge_applies_to", nullable = false)
    private ChargeAppliesTo chargeAppliesTo;

    @Enumerated
    @Column(name = "charge_calculation_type", nullable = false)
    private ChargeCalculationType chargeCalculationType;

    @Column(name="is_active" ,nullable=false)
    private Boolean isActive ;

    protected CommissionCharge(){}

    public CommissionCharge(String name,String currencyCode , BigDecimal amount, ChargeTimeType chargeTimeType, ChargeAppliesTo chargeAppliesTo, ChargeCalculationType chargeCalculationType, Boolean isActive) {
        this.name = name;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.chargeTimeType = chargeTimeType;
        this.chargeAppliesTo = chargeAppliesTo;
        this.chargeCalculationType = chargeCalculationType;
        this.isActive = isActive;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ChargeTimeType getChargeTimeType() {
        return chargeTimeType;
    }

    public void setChargeTimeType(ChargeTimeType chargeTimeType) {
        this.chargeTimeType = chargeTimeType;
    }

    public ChargeAppliesTo getChargeAppliesTo() {
        return chargeAppliesTo;
    }

    public void setChargeAppliesTo(ChargeAppliesTo chargeAppliesTo) {
        this.chargeAppliesTo = chargeAppliesTo;
    }

    public ChargeCalculationType getChargeCalculationType() {
        return chargeCalculationType;
    }

    public void setChargeCalculationType(ChargeCalculationType chargeCalculationType) {
        this.chargeCalculationType = chargeCalculationType;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}

