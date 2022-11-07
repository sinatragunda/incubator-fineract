/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 04 November 2022 at 03:03
 */
package org.apache.fineract.portfolio.remittance.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface RxDealWritePlatformService {

    public CommandProcessingResult createRxDeal(JsonCommand jsonCommand);
    public CommandProcessingResult updateRxDeal(JsonCommand jsonCommand);

}
