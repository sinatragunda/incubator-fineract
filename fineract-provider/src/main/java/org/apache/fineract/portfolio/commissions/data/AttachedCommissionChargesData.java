/*

    Created by Sinatra Gunda
    At 9:37 AM on 1/5/2022

*/
package org.apache.fineract.portfolio.commissions.data;

import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.commissions.domain.AttachedCommissionCharges;
import org.apache.fineract.portfolio.commissions.domain.LoanAgent;

import java.math.BigDecimal;

public class AttachedCommissionChargesData {

    private Long id ;
    private Long loansFromAgentId ;
    private Long commissionChargeId;
    private Boolean isDeposited ;
    private BigDecimal amount ;
    private Integer chargeTimeInt ;

    private Long loanId;
    private Long loanAgentId ;
    private Long clientId ;
    private Long savingsAccountId;


    //relatable data
    private LoanAgentData loanAgentData ;
    private CommissionChargeData commissionChargeData;

    private ChargeTimeType chargeTimeType ;

    protected AttachedCommissionChargesData(){}

    public AttachedCommissionChargesData(LoanAgentData loanAgentData ,CommissionChargeData commissionChargeData) {
        this.loanAgentData = loanAgentData;
        this.commissionChargeData = commissionChargeData ;
    }

    public AttachedCommissionChargesData(Long id, Long loansFromAgentId, Long commissionChargeId, BigDecimal amount ,Boolean isDeposited ,Integer chargeTimeTypeInt,Long loanAgentId , Long clientId ,Long savingsAccountId) {
        this.id = id;
        this.loansFromAgentId = loansFromAgentId;
        this.commissionChargeId = commissionChargeId;
        this.isDeposited = isDeposited;
        this.amount = amount;
        this.chargeTimeType = ChargeTimeType.fromInt(chargeTimeTypeInt);
        this.loanAgentId = loanAgentId ;
        this.clientId = clientId ;
        this.savingsAccountId = savingsAccountId;
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


    public static AttachedCommissionChargesData fullLoad(Long id, Long loansFromAgentId, Long commissionChargeId, BigDecimal amount ,Boolean isDeposited ,Integer chargeTimeTypeInt,Long loanAgentId ,Long clientId ,Long savingsAccountId){
        return new AttachedCommissionChargesData(id ,loansFromAgentId ,commissionChargeId ,amount ,isDeposited ,chargeTimeTypeInt ,loanAgentId, clientId ,savingsAccountId);
    }


    public LoanAgentData getLoanAgentData() {
        return loanAgentData;
    }

    public CommissionChargeData getCommissionChargeData() {
        return commissionChargeData;
    }


    public Long getId() {
        return id;
    }

    public Long getLoanAgentId() {
        return loanAgentId;
    }

    public Long getCommissionChargeId() {
        return commissionChargeId;
    }

    public Boolean isDeposited() {
        return isDeposited;
    }

    public void setIsDeposited(boolean value){
        this.isDeposited = value;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getChargeTimeInt() {
        return chargeTimeInt;
    }

    public Long getLoanId() {
        return loanId;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getSavingsAccountId() {
        return savingsAccountId;
    }

    public ChargeTimeType getChargeTimeType() {
        return chargeTimeType;
    }
}
