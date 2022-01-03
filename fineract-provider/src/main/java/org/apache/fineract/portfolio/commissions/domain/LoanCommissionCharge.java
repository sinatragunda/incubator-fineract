/*

    Created by Sinatra Gunda
    At 10:18 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
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
public class LoanCommissionCharge extends AbstractPersistableCustom<Long>{

    @Enumerated
    @Column(name = "charge_calculation_criteria", nullable = false)
    private CHARGE_CALCULATION_CRITERIA chargeCalculationCriteria;

    @Column(name="amount" ,nullable=false)
    private BigDecimal amount ;

    @Column(name = "loan_commission_charge_time", nullable = false)
    private LOAN_COMMISSION_CHARGE_TIME loanCommissionChargeTime ;


    protected LoanCommissionCharge(){}


    public CHARGE_CALCULATION_CRITERIA getChargeCalculationCriteria() {
        return chargeCalculationCriteria;
    }

    public void setChargeCalculationCriteria(CHARGE_CALCULATION_CRITERIA chargeCalculationCriteria) {
        this.chargeCalculationCriteria = chargeCalculationCriteria;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LOAN_COMMISSION_CHARGE_TIME getLoanCommissionChargeTime() {
        return loanCommissionChargeTime;
    }

    public void setLoanCommissionChargeTime(LOAN_COMMISSION_CHARGE_TIME loanCommissionChargeTime) {
        this.loanCommissionChargeTime = loanCommissionChargeTime;
    }
}

