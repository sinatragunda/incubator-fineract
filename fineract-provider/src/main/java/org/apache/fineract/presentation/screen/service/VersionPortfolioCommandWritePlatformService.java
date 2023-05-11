/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 May 2023 at 08:09
 */
package org.apache.fineract.presentation.screen.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;

public interface VersionPortfolioCommandWritePlatformService {

    public CommandProcessingResult create(String payload , REF_TABLE refTable);
}
