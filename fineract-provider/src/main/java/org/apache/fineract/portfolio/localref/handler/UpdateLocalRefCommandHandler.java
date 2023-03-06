/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 01 March 2022 at 2353
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
@CommandType(entity = "LOCAL_REF", action = "UPDATE")
public class UpdateLocalRefCommandHandler implements NewCommandSourceHandler {

    private LocalRefWritePlatformService localRefWritePlatformService;

    @Autowired
    public UpdateLocalRefCommandHandler(final LocalRefWritePlatformService localRefWritePlatformService) {
        this.localRefWritePlatformService = localRefWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        Long id = command.entityId();
        return this.localRefWritePlatformService.update(id ,command);
    }
}