/*

    Created by Sinatra Gunda
    At 4:31 AM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.domain;

import org.apache.fineract.portfolio.savings.domain.interest.EquityGrowthDividends;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="m_equity_growth_savings_account")
public class EquityGrowthOnSavingsAccount{


    @Column(name="equity_growth_dividends_id")
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
