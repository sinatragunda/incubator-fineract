/*

    Created by Sinatra Gunda
    At 9:45 AM on 9/19/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface DepreciationProductWritePlatformService {

    CommandProcessingResult createDepreciationProduct(JsonCommand command);
    CommandProcessingResult updateDepreciationProduct(Long productId, JsonCommand command);

}
