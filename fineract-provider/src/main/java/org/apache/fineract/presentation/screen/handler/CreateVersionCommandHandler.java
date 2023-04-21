/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 10:11
 */
package org.apache.fineract.presentation.screen.handler;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.presentation.screen.service.VersionWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "VERSION", action = "CREATE")
public class CreateVersionCommandHandler implements NewCommandSourceHandler {

    private final VersionWritePlatformService writePlatformService;

    @Autowired
    public CreateVersionCommandHandler(VersionWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        System.err.println("------------------create version command ? ");
        return this.writePlatformService.create(command);
    }
}
