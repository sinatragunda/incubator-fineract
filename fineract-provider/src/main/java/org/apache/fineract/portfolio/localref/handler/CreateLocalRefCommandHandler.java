/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 02:02
 */
package org.apache.fineract.portfolio.localref.handler;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductWritePlatformService;
import org.apache.fineract.portfolio.localref.service.LocalRefWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "LOCAL_REF", action = "CREATE")
public class CreateLocalRefCommandHandler implements NewCommandSourceHandler {

    private LocalRefWritePlatformService localRefWritePlatformService;

    @Autowired
    public CreateLocalRefCommandHandler(final LocalRefWritePlatformService localRefWritePlatformService) {
        this.localRefWritePlatformService = localRefWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.localRefWritePlatformService.create(command);
    }
}