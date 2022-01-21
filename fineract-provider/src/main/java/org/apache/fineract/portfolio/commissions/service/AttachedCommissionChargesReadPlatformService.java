/*

    Created by Sinatra Gunda
    At 4:52 PM on 1/6/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.portfolio.commissions.data.AttachedCommissionChargesData;

import java.util.List;

public interface AttachedCommissionChargesReadPlatformService {

    List<AttachedCommissionChargesData> retrieveAllByLoanAgent(Long loanAgentId);
    List<AttachedCommissionChargesData> retrieveAllByLoan(Long loanId);
}
