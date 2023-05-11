/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 May 2023 at 08:07
 */
package org.apache.fineract.presentation.screen.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.service.VersionPortfolioCommandWritePlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;

import java.util.List ;

public class VersionPortfolioCommandFactory {

    public static CommandProcessingResult create(VersionPortfolioCommandWritePlatformService service , List<ScreenElement> screenElementList , REF_TABLE refTable){
        
        String payload = VersionPayloadConstructor.construct(screenElementList);
        CommandProcessingResult result = service.create(payload, refTable);
        return result;
    }
}
