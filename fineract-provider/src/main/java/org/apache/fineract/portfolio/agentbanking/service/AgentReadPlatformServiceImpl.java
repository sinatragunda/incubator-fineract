/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 15 February 2023 at 07:00
 */
package org.apache.fineract.portfolio.agentbanking.service;

import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.agentbanking.data.AgentData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentReadPlatformServiceImpl implements AgentReadPlatformService{

    private LoanReadPlatformService loanReadPlatformService;
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private ClientReadPlatformService clientReadPlatformService;

    @Autowired
    public AgentReadPlatformServiceImpl(LoanReadPlatformService loanReadPlatformService ,final SavingsAccountReadPlatformService savingsAccountReadPlatformService ,final ClientReadPlatformService clientReadPlatformService) {
        this.loanReadPlatformService = loanReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
    }

    @Override
    public AgentData retrieveOne(StaffData staffData){

        boolean isAgent = staffData.isAgent();

        if(isAgent){
            //System.err.println("-------------------------------is agent load data now ");
            Long id = staffData.getId();
            return agentById(id);
        }
        return null;
    }

    @Override
    public AgentData retrieveOne(Long id){
        //System.err.println("-------------------------------is agent load data now ");
        return agentById(id);
    }

    private AgentData agentById(Long id){

        ClientData clientData = clientReadPlatformService.retrieveByStaff(id);

        Long clientId = clientData.getId();

        List<LoanAccountData> loanAccountDataList = loanReadPlatformService.retrieveAllForLoanOfficer(id);

        Collection<SavingsAccountData> savingsAccountDataList = savingsAccountReadPlatformService.retrieveAllForLookup(clientId);

        //System.err.println("--------------------------------account should be "+savingsAccountDataList.size());

        AgentData agentData = new AgentData(id, clientData ,null, loanAccountDataList, new ArrayList<>(savingsAccountDataList));
        return agentData;

    }

}
