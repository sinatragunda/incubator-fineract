/*

    Created by Sinatra Gunda
    At 3:32 AM on 1/6/2022

*/
package org.apache.fineract.portfolio.commissions.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.commissions.service.LoanAgentWritePlatformService;
import org.apache.fineract.portfolio.commissions.service.LoansFromAgentsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "LOANSFROMAGENTS", action = "CREATE")
public class CreateLoansFromAgentsCommandHandler implements NewCommandSourceHandler {

    private final LoansFromAgentsWritePlatformService loansFromAgentsWritePlatformService;

    @Autowired
    public CreateLoansFromAgentsCommandHandler(final LoansFromAgentsWritePlatformService loansFromAgentsWritePlatformService) {
        this.loansFromAgentsWritePlatformService = loansFromAgentsWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand jsonCommand) {
        return this.loansFromAgentsWritePlatformService.create(jsonCommand);
    }

}
