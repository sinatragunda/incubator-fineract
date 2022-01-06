/*

    Created by Sinatra Gunda
    At 8:17 PM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.data;

import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.wese.helper.JsonHelper;

import java.util.List;

public class LoanAgentDataBridge {

    private LoanAgentData loanAgentData ;
    private LoansFromAgents loansFromAgents ;
    private List<CommissionChargeData> commissionChargesList ;

    public LoanAgentDataBridge(){}


    public static LoanAgentDataBridge fromJson(String json){
        return JsonHelper.serializeFromHttpResponse(new LoanAgentDataBridge(),json);
    }

    public void setLoanAgentData(LoanAgentData loanAgentData) {
        this.loanAgentData = loanAgentData;
    }

    public void setCommissionChargesList(List<CommissionChargeData> commissionChargesList) {
        this.commissionChargesList = commissionChargesList;
    }

    public void setLoansFromAgents(LoansFromAgents loansFromAgents){
        this.loansFromAgents = loansFromAgents ;
    }

    public LoansFromAgents getLoansFromAgents(){
        return this.loansFromAgents;
    }

    public LoanAgentData getLoanAgentData() {
        return loanAgentData;
    }

    public List<CommissionChargeData> getCommissionChargesList() {
        return commissionChargesList;
    }
}
