/*

    Created by Sinatra Gunda
    At 4:31 AM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.domain;

import org.apache.fineract.portfolio.savings.domain.EquityGrowthDividends;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


@Entity
@Table(name="m_equity_growth_savings_account")
public class EquityGrowthOnSavingsAccount extends AbstractPersistableCustom<Long>{

    @ManyToOne
    @JoinColumn(name ="equity_growth_dividends_id", nullable = false)
    private EquityGrowthDividends equityGrowthDividends;

    @Column(name="savings_account_id" ,nullable=false)
    private Long savingsAccountId;

    @Column(name="percentage_of_profit")
    private Double percentageOfProfit;

    @Column(name="amount")
    private BigDecimal amount ;

    @Column(name="average_savings")
    private BigDecimal averageSavings;

    @Column(name="note")
    private String note ;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="transfer_account_id" ,nullable=false)
    private SavingsAccount transferAccount;


    @Transient
    private String clientName;


    public EquityGrowthOnSavingsAccount(){}


    public EquityGrowthOnSavingsAccount(EquityGrowthDividends equityGrowthDividends,Long savingsAccountId,BigDecimal averageSavings, BigDecimal amount,Double pecentage, String note ,String clientName) {
        this.equityGrowthDividends = equityGrowthDividends;
        this.savingsAccountId = savingsAccountId;
        this.amount = amount;
        this.note = note;
        this.percentageOfProfit = pecentage;
        this.clientName = clientName ;
        this.averageSavings = averageSavings;
    }

    public BigDecimal getAverageSavings() {
        return averageSavings;
    }

    public void setAverageSavings(BigDecimal averageSavings) {
        this.averageSavings = averageSavings;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public EquityGrowthDividends getEquityGrowthDividends() {
        return equityGrowthDividends;
    }

    public void setEquityGrowthDividends(EquityGrowthDividends equityGrowthDividends) {
        this.equityGrowthDividends = equityGrowthDividends;
    }

    public Double getPercentageOfProfit() {
        return percentageOfProfit;
    }

    public void setPercentageOfProfit(Double percentageOfProfit) {
        this.percentageOfProfit = percentageOfProfit;
    }

    public Long getSavingsAccountId() {
        return savingsAccountId;
    }

    public void setSavingsAccountId(Long savingsAccountId) {
        this.savingsAccountId = savingsAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public SavingsAccount getTransferAccount() {
        return this.transferAccount;
    }

    public void setTransferAccount(SavingsAccount transferAccount) {
        this.transferAccount = transferAccount;
    }
}
