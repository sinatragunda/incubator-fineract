/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 22 December 2022 at 05:26
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
@CommandType(entity = "TRANSACTION_CODE", action = "UPDATE")
public class UpdateTransactionCodeCommandHandler implements NewCommandSourceHandler {

    private final TransactionCodeWritePlatformService transactionCodeWritePlatformService;

    @Autowired
    public UpdateTransactionCodeCommandHandler(TransactionCodeWritePlatformService transactionCodeWritePlatformService) {
        this.transactionCodeWritePlatformService = transactionCodeWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.transactionCodeWritePlatformService.update(command.entityId(),command);
    }
}
