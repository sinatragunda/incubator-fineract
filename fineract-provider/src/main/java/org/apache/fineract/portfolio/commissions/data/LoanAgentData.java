/*

    Created by Sinatra Gunda
    At 12:39 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.data;

public class LoanAgentData {

    private Long id ;
    private Long clientId ;
    private Long savingsAccountId ;


    public LoanAgentData(Long id, Long clientId, Long savingsAccountId) {
        this.id = id;
        this.clientId = clientId;
        this.savingsAccountId = savingsAccountId;
    }

    public Long getId() {
        return id;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getSavingsAccountId() {
        return savingsAccountId;
    }
}
