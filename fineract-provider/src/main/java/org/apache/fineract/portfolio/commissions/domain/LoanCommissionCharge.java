/*

    Created by Sinatra Gunda
    At 10:18 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.domain;

import org.apache.fineract.portfolio.commissions.enumerations.LOAN_COMMISSION_CHARGE_TIME;
import org.apache.fineract.wese.enumerations.CHARGE_CALCULATION_CRITERIA;

import java.math.BigDecimal;

public class LoanCommissionCharge {


    private CHARGE_CALCULATION_CRITERIA chargeCalculationCriteria;
    private BigDecimal amount ;
    private LOAN_COMMISSION_CHARGE_TIME loanCommissionChargeTime ;


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

