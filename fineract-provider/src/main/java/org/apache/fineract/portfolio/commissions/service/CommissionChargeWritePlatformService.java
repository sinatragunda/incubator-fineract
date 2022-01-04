/*

    Created by Sinatra Gunda
    At 12:46 PM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface CommissionChargeWritePlatformService {

    CommandProcessingResult create(JsonCommand jsonCommand);
}
