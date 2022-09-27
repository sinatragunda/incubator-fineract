
package org.apache.fineract.portfolio.loanaccount.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "LOAN", action = "ERROR_CORRECTION")
public class LoanErrorCorrectionHandler implements NewCommandSourceHandler {

    private final LoanWritePlatformService writePlatformService;

    @Autowired
    public LoanErrorCorrectionHandler(final LoanWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.writePlatformService.errorCorrection(command.getLoanId());
    }

}