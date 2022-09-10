/*

    Created by Sinatra Gunda
    At 12:24 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.handler;

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.accounting.journalentry.service.TransactionCodeWritePlatformService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "TRANSACTION_CODE", action = "CREATE")
public class TransactionCodeCreateCommandHandler implements NewCommandSourceHandler {

    private final TransactionCodeWritePlatformService transactionCodeWritePlatformService;

    @Autowired
    public TransactionCodeCreateCommandHandler(TransactionCodeWritePlatformService transactionCodeWritePlatformService) {
        this.transactionCodeWritePlatformService = transactionCodeWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.transactionCodeWritePlatformService.create(command);
    }
}
