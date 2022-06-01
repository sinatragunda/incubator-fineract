/*

    Created by Sinatra Gunda
    At 2:00 PM on 5/30/2022

*/
package org.apache.fineract.portfolio.loanproduct.data;

public class LoanProductSettingsData {

    private Long settlementAccountId ;

    public LoanProductSettingsData(){}

    public LoanProductSettingsData(Long settlementAccountId){
        this.settlementAccountId = settlementAccountId ;
    }
}
