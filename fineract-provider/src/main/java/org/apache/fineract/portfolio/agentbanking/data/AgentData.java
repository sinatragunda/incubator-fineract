/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 15 February 2023 at 04:12
 */
package org.apache.fineract.portfolio.agentbanking.data;

import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.useradministration.data.AppUserData;

import java.util.List;

public class AgentData {

    private Long id ;
    private AppUserData appUserData ;
    private ClientData clientData;
    private List<LoanAccountData> loanAccountDataList;
    private List<SavingsAccountData> savingsAccountDataList;

    public AgentData(Long id, ClientData clientData ,AppUserData appUserData, List<LoanAccountData> loanAccountDataList, List<SavingsAccountData> savingsAccountDataList) {
        this.id = id;
        this.appUserData = appUserData;
        this.clientData = clientData;
        this.loanAccountDataList = loanAccountDataList;
        this.savingsAccountDataList = savingsAccountDataList;
    }

    public Long clientId(){
        return this.clientData.getId();
    }
}
