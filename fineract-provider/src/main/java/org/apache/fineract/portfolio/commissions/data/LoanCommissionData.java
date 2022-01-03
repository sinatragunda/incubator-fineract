/*

    Created by Sinatra Gunda
    At 4:50 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.data;

public class LoanCommissionData{

    private Long id ;
    private Long loanAgentId;
    private Long loanId ;
    private Long loanCommissionChargeId ;
    private Boolean isDeposited ;


    public LoanCommissionData(Long id, Long loanAgentId, Long loanId, Long loanCommissionChargeId, Boolean isDeposited) {
        this.id = id;
        this.loanAgentId = loanAgentId;
        this.loanId = loanId;
        this.loanCommissionChargeId = loanCommissionChargeId;
        this.isDeposited = isDeposited;
    }
}
