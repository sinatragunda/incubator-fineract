/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 May 2023 at 00:25
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
@CommandType(entity = "MENU", action = "UPDATE")
public class UpdateMenuCommandHandler implements NewCommandSourceHandler {

    private MenuWritePlatformService writePlatformService;

    @Autowired
    public UpdateMenuCommandHandler(final MenuWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.update(command.entityId(), command);
    }
}

