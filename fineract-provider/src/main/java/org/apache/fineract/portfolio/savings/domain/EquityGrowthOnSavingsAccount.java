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

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


@Entity
@Table(name="m_equity_growth_savings_account")
public class EquityGrowthOnSavingsAccount extends AbstractPersistableCustom<Long>{


    @Column(name="equity_growth_dividend_id")
    @JoinColumn(name = "equity_growth_dividend_id", nullable = false)
    private EquityGrowthDividends equityGrowthDividends;

    @Column(name="savings_account_id")
    private Long savingsAccountId;

    @Column(name="percentage_of_profit")
    private Double percentageOfProfit;

    @Column(name="amount")
    private BigDecimal amount ;

    @Column(name="note")
    private String note ;


    public EquityGrowthOnSavingsAccount(){}


    public EquityGrowthOnSavingsAccount(EquityGrowthDividends equityGrowthDividends, Long savingsAccountId,BigDecimal amount,Double pecentage, String note) {
        this.equityGrowthDividends = equityGrowthDividends;
        this.savingsAccountId = savingsAccountId;
        this.amount = amount;
        this.note = note;
        this.percentageOfProfit = pecentage;
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
}
