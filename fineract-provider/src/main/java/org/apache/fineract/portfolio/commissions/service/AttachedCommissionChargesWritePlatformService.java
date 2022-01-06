/*

    Created by Sinatra Gunda
    At 3:42 AM on 1/6/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.commissions.data.LoanAgentDataBridge;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;

public interface AttachedCommissionChargesWritePlatformService {

    CommandProcessingResult create(LoansFromAgents loansFromAgents , CommissionCharge commissionCharge);
    CommandProcessingResult create(LoanAgentDataBridge loanAgentDataBridge);
}
