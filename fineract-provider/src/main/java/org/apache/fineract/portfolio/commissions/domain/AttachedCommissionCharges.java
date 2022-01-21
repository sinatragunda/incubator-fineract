/*

    Created by Sinatra Gunda
    At 8:19 AM on 1/5/2022

*/
package org.apache.fineract.portfolio.commissions.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

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
import java.math.BigDecimal;

@Entity
@Table(name = "m_attached_commission_charges")
public class AttachedCommissionCharges extends AbstractPersistableCustom<Long>{


    @ManyToOne
    @Column(name="loan_from_agent_id")
    private LoansFromAgents loanFromAgent ;

    @ManyToOne
    @Column(name="loan_commission_charge_id")
    private CommissionCharge commissionCharge ;

    @Column(name="is_deposited")
    private Boolean deposited ;

    @Column(name="amount")
    private BigDecimal amount ;


    public AttachedCommissionCharges(){}


    public AttachedCommissionCharges(LoansFromAgents loanFromAgent, CommissionCharge commissionCharge, Boolean deposited, BigDecimal amount) {
        this.loanFromAgent = loanFromAgent;
        this.commissionCharge = commissionCharge;
        this.deposited = deposited;
        this.amount = amount;
    }

    public LoansFromAgents getLoanFromAgent() {
        return loanFromAgent;
    }

    public void setLoanFromAgent(LoansFromAgents loanFromAgent) {
        this.loanFromAgent = loanFromAgent;
    }

    public CommissionCharge getCommissionCharge() {
        return commissionCharge;
    }

    public void setCommissionCharge(CommissionCharge commissionCharge) {
        this.commissionCharge = commissionCharge;
    }

    public Boolean getDeposited() {
        return deposited;
    }

    public void setDeposited(Boolean deposited) {
        this.deposited = deposited;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
