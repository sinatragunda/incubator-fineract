/*

    Created by Sinatra Gunda
    At 4:47 AM on 1/5/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;

public interface LoansFromAgentsWritePlatformService {

    CommandProcessingResult create(JsonCommand jsonCommand);

}
