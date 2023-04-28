/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 13:14
 */
package org.apache.fineract.infrastructure.wsplugin.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;

public interface WsScriptWritePlatformService {

    public CommandProcessingResult create(Document document, JsonCommand command);
}
