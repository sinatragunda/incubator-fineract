/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 00:59
 */
package org.apache.fineract.portfolio.localref.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface LocalRefWritePlatformService {

    public CommandProcessingResult create(JsonCommand jsonCommand);
}
