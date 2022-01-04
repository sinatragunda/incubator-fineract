/*

    Created by Sinatra Gunda
    At 1:17 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.commissions.service.LoanAgentWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "LOANAGENT", action = "CREATE")
public class CreateLoanAgentCommandHandler implements NewCommandSourceHandler {

    private final LoanAgentWritePlatformService loanAgentWritePlatformService;

    @Autowired
    public CreateLoanAgentCommandHandler(final LoanAgentWritePlatformService loanAgentWritePlatformService) {
        this.loanAgentWritePlatformService = loanAgentWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand jsonCommand) {
        return this.loanAgentWritePlatformService.createLoanAgent(jsonCommand);
    }

}
