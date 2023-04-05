/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 17:17
 */
package org.apache.fineract.presentation.menu.handler;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.presentation.menu.service.MenuItemWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "MENUITEM", action = "CREATE")
public class CreateMenuItemCommandHandler implements NewCommandSourceHandler {

    private MenuItemWritePlatformService writePlatformService;

    @Autowired
    public CreateMenuItemCommandHandler(final MenuItemWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.create(command);
    }
}
