/*

    Created by Sinatra Gunda
    At 4:41 AM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.domain;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


@Entity
@Table(name="m_equity_growth_dividends")
public class EquityGrowthDividends extends AbstractPersistableCustom<Long>{


    @Column(name="start_period")
    private Date startPeriod ;

    @Column(name="end_period")
    private Date endPeriod ;

    @Column(name="total_profits")
    private BigDecimal amount ;

    @Column(name="beneficiaries")
    private int beneficiaries;

    @Column(name="savings_product_id")
    private Long savingsProductId;

    // Added 28/12/2021
    @Column(name="office_id")
    private Long officeId;


    public EquityGrowthDividends(){}

    public EquityGrowthDividends(Long savingsProductId , Date startPeriod, Date endPeriod, BigDecimal amount, int beneficiaries ,Long officeId) {
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.amount = amount;
        this.beneficiaries = beneficiaries;
        this.savingsProductId = savingsProductId ;
        this.officeId = officeId;
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

    public Long getSavingsProductId() {
        return savingsProductId;
    }

    public void setSavingsProductId(Long savingsProductId) {
        this.savingsProductId = savingsProductId;
    }

    public void setOfficeId(Long id){
        this.officeId = officeId;
    }

    public Long getOfficeId(){
        return this.officeId;
    }
}
