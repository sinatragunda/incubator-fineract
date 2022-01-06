/*

    Created by Sinatra Gunda
    At 12:39 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.data;

import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;

import java.util.List;

public class LoanAgentData {

    private Long id ;
    private Long clientId ;
    private Long savingsAccountId ;
    private String clientAccountNo;
    private String displayName ;

    private ClientData clientData ;
    private SavingsAccountData savingsAccountData ;
    private List<Loan> loanList ;

    public LoanAgentData(){}

    public LoanAgentData(Long id ,ClientData clientData ,SavingsAccountData savingsAccountData){
        this.id = id ;
        this.clientData = clientData ;
        this.savingsAccountData = savingsAccountData;
    }

    public LoanAgentData(Long id, Long clientId, Long savingsAccountId ,String clientAccountNo, String displayName) {
        this.id = id;
        this.clientId = clientId;
        this.clientAccountNo = clientAccountNo;
        this.displayName = displayName;
        this.savingsAccountId = savingsAccountId;
    }

    public static LoanAgentData instance(Long id ,Long clientId ,Long savingsAccountId ,String clientAccountNo ,String displayName){
        return new LoanAgentData(id ,clientId ,savingsAccountId ,clientAccountNo ,displayName);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setSavingsAccountId(Long savingsAccountId) {
        this.savingsAccountId = savingsAccountId;
    }

    public String getClientAccountNo() {
        return clientAccountNo;
    }

    public void setClientAccountNo(String clientAccountNo) {
        this.clientAccountNo = clientAccountNo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
