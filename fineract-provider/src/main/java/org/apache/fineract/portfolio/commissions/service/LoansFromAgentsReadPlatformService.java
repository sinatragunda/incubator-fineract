/*

    Created by Sinatra Gunda
    At 4:53 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.portfolio.commissions.data.LoansFromAgentsData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;

import java.util.List;

public interface LoansFromAgentsReadPlatformService {

    public LoansFromAgentsData retrieveOne(Long id);
    public LoansFromAgentsData retrieveOneByLoan(Long id);
    public List<LoanAccountData> retrieveAllLoansForAgent(Long agentId);
    public List<LoansFromAgentsData> retrieveAllForCommissionCharge(Long chargeId);

}
