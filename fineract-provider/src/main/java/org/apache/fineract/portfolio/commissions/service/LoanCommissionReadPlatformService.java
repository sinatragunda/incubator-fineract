/*

    Created by Sinatra Gunda
    At 4:53 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.portfolio.commissions.data.LoanCommissionData;

import java.util.List;

public interface LoanCommissionReadPlatformService {

    public LoanCommissionData retrieveOne(Long id);
    public LoanCommissionData retrieveOneByLoan(Long id);
    public List<LoanCommissionData> retrieveAllForAgent(Long agentId);
    public List<LoanCommissionData> retrieveAllForCommissionCharge(Long chargeId);

}
