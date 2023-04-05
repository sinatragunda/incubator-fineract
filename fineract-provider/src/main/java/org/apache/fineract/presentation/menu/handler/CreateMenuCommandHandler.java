/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 31 March 2023 at 01:57
 */
package org.apache.fineract.presentation.menu.handler;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.presentation.menu.service.MenuWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "MENU", action = "CREATE")
public class CreateMenuCommandHandler implements NewCommandSourceHandler {

    private MenuWritePlatformService writePlatformService;

    @Autowired
    public CreateMenuCommandHandler(final MenuWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.create(command);
    }
}

