/*

    Created by Sinatra Gunda
    At 12:27 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface TransactionCodeWritePlatformService {
    CommandProcessingResult create(JsonCommand jsonCommand);
    CommandProcessingResult update(Long id , JsonCommand command);
}
