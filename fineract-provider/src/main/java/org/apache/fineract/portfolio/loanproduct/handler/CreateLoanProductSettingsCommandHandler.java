/*

    Created by Sinatra Gunda
    At 12:23 AM on 5/31/2022

*/
package org.apache.fineract.portfolio.loanproduct.handler;


import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductSettingsWritePlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "LOANPRODUCTSETTINGS", action = "CREATE")
public class CreateLoanProductSettingsCommandHandler implements NewCommandSourceHandler {

    private final LoanProductSettingsWritePlatformService writePlatformService;

    @Autowired
    public CreateLoanProductSettingsCommandHandler(final LoanProductSettingsWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.create(command,null);
    }
}
