/*

    Created by Sinatra Gunda
    At 4:08 PM on 4/10/2022

*/
package org.apache.fineract.portfolio.loanaccount.template;

import java.math.BigDecimal;

public class NkwaziLoanTemplate {

    private BigDecimal maxAllowable ;
    private BigDecimal balanceAllowable ;
    private Integer loanFactor ;
    private BigDecimal savingsAccountBalance ;
    private Boolean isSet = false ;

    public NkwaziLoanTemplate(){

    }

    public NkwaziLoanTemplate(BigDecimal maxAllowable, BigDecimal balanceAllowable, Integer loanFactor, BigDecimal savingsAccountBalance) {
        this.maxAllowable = maxAllowable;
        this.balanceAllowable = balanceAllowable;
        this.loanFactor = loanFactor;
        this.savingsAccountBalance = savingsAccountBalance;
        this.isSet = true ;
    }

    public BigDecimal getMaxAllowable() {
        return maxAllowable;
    }

    public void setMaxAllowable(BigDecimal maxAllowable) {
        this.maxAllowable = maxAllowable;
    }

    public BigDecimal getBalanceAllowable() {
        return balanceAllowable;
    }

    public void setBalanceAllowable(BigDecimal balanceAllowable) {
        this.balanceAllowable = balanceAllowable;
    }

    public Integer getLoanFactor() {
        return loanFactor;
    }

    public void setLoanFactor(Integer loanFactor) {
        this.loanFactor = loanFactor;
    }

    public BigDecimal getSavingsAccountBalance() {
        return savingsAccountBalance;
    }

    public void setSavingsAccountBalance(BigDecimal savingsAccountBalance) {
        this.savingsAccountBalance = savingsAccountBalance;
    }
}
