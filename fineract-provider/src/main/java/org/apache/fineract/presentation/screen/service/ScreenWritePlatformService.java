/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:12
 */
package org.apache.fineract.presentation.screen.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ScreenWritePlatformService {
    public CommandProcessingResult create(JsonCommand command);
    public CommandProcessingResult update(Long id ,JsonCommand command);
    public CommandProcessingResult deleteOrDisable(Long id ,Boolean disable);
}
