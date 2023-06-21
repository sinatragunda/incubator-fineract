/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 June 2023 at 01:48
 */
package org.apache.fineract.portfolio.account.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "ACCOUNTTRANSFER", action = "REVERSETRANSFER")
public class ReverseTransferCommandHandler implements NewCommandSourceHandler {

    private final AccountTransfersWritePlatformService writePlatformService;

    @Autowired
    public ReverseTransferCommandHandler(final AccountTransfersWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.reverseTransfer(command.entityId());
    }
}
