/*

    Created by Sinatra Gunda
    At 9:37 AM on 1/5/2022

*/
package org.apache.fineract.portfolio.commissions.data;

import org.apache.fineract.portfolio.commissions.domain.AttachedCommissionCharges;
import org.apache.fineract.portfolio.commissions.domain.LoanAgent;

import java.math.BigDecimal;

public class AttachedCommissionChargesData {

    private Long id ;
    private Long loanAgentId ;
    private Long commissionChargeId;
    private Boolean isDeposited ;
    private BigDecimal amount ;


    //relatable data
    private LoanAgentData loanAgentData ;
    private CommissionChargeData commissionChargeData;


    protected AttachedCommissionChargesData(){}

    public AttachedCommissionChargesData(LoanAgentData loanAgentData ,CommissionChargeData commissionChargeData) {
        this.loanAgentData = loanAgentData;
        this.commissionChargeData = commissionChargeData ;
    }

    public AttachedCommissionChargesData(Long id, Long loanAgentId, Long commissionChargeId, Boolean isDeposited, BigDecimal amount) {
        this.id = id;
        this.loanAgentId = loanAgentId;
        this.commissionChargeId = commissionChargeId;
        this.isDeposited = isDeposited;
        this.amount = amount;
    }

    public AttachedCommissionChargesData(Long id, Long loanAgentId, Long commissionChargeId, Boolean isDeposited, BigDecimal amount, LoanAgentData loanAgentData, CommissionChargeData commissionChargeData) {
        this.id = id;
        this.loanAgentId = loanAgentId;
        this.commissionChargeId = commissionChargeId;
        this.isDeposited = isDeposited;
        this.amount = amount;
        this.loanAgentData = loanAgentData;
        this.commissionChargeData = commissionChargeData;
    }


    public LoanAgentData getLoanAgentData() {
        return loanAgentData;
    }

    public CommissionChargeData getCommissionChargeData() {
        return commissionChargeData;
    }
}
