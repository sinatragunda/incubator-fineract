/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 10:08
 */
package org.apache.fineract.presentation.screen.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface VersionWritePlatformService {
    public CommandProcessingResult create(JsonCommand command);
}
