/*

    Created by Sinatra Gunda
    At 12:35 PM on 5/25/2022

*/
package org.apache.fineract.portfolio.hirepurchase.data;

import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;

public class HirePurchaseData {

    private String name ;
    private LoanAccountData loanAccountData ;

    public HirePurchaseData(){}

    public HirePurchaseData(String name, LoanAccountData loanAccountData) {
        this.name = name;
        this.loanAccountData = loanAccountData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LoanAccountData getLoanAccountData() {
        return loanAccountData;
    }

    public void setLoanAccountData(LoanAccountData loanAccountData) {
        this.loanAccountData = loanAccountData;
    }
}
