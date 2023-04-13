/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 April 2023 at 04:11
 */
package org.apache.fineract.presentation.screen.handler;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.charge.service.ChargeWritePlatformService;
import org.apache.fineract.presentation.screen.service.ScreenWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "SCREEN", action = "CREATE")
public class CreateScreenCommandHandler implements NewCommandSourceHandler {

    private final ScreenWritePlatformService writePlatformService;

    @Autowired
    public CreateScreenCommandHandler(ScreenWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }


    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        System.err.println("------------------whose calling this function ? ");
        return this.writePlatformService.create(command);
    }
}
