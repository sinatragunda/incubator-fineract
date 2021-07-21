/*

    Created by Sinatra Gunda
    At 4:41 AM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="equity_growth_dividends")
public class EquityGrowthDividends {

    @Column(name="start_period")
    private Date startPeriod ;

    @Column(name="end_period")
    private Date endPeriod ;

    @Column(name="amount")
    private BigDecimal amount ;

    @Column(name="beneficiaries")
    private int beneficiaries;


    public EquityGrowthDividends(){}

    public EquityGrowthDividends(Date startPeriod, Date endPeriod, BigDecimal amount, int beneficiaries) {
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.amount = amount;
        this.beneficiaries = beneficiaries;
    }

    public Date getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(Date startPeriod) {
        this.startPeriod = startPeriod;
    }

    public Date getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(Date endPeriod) {
        this.endPeriod = endPeriod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(int beneficiaries) {
        this.beneficiaries = beneficiaries;
    }
}
