/*

    Created by Sinatra Gunda
    At 4:50 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.data;

public class LoansFromAgentsData {

    private Long id ;
    private Long loanAgentId;
    private Long loanId ;

    public LoansFromAgentsData(Long id, Long loanAgentId, Long loanId) {
        this.id = id;
        this.loanAgentId = loanAgentId;
        this.loanId = loanId;
    }

    public Long getId() {
        return id;
    }

    public Long getLoanAgentId() {
        return loanAgentId;
    }

    public Long getLoanId() {
        return loanId;
    }

}
