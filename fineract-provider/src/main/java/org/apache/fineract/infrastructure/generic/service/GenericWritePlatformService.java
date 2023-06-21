/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 16:50
 */
package org.apache.fineract.infrastructure.generic.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface GenericWritePlatformService {
    CommandProcessingResult create(JsonCommand command);
    CommandProcessingResult update(Long id ,JsonCommand command);
    CommandProcessingResult delete(Long id);
}