/*

    Created by Sinatra Gunda
    At 1:20 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface LoanAgentWritePlatformService {
    CommandProcessingResult createLoanAgent(JsonCommand jsonCommand);
}
