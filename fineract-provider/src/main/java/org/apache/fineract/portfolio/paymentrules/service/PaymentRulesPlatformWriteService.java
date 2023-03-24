/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 March 2023 at 12:22
 */
package org.apache.fineract.portfolio.paymentrules.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface PaymentRulesPlatformWriteService {

    public CommandProcessingResult create(JsonCommand command);
    public CommandProcessingResult update(Long id ,JsonCommand command);

}
