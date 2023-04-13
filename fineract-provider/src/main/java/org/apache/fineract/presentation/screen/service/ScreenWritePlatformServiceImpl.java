/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:13
 */
package org.apache.fineract.presentation.screen.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.apache.fineract.presentation.screen.repo.ScreenRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScreenWritePlatformServiceImpl implements ScreenWritePlatformService {

    private ScreenAssembler screenAssembler;
    private ScreenRepositoryWrapper screenRepositoryWrapper;

    @Autowired
    public ScreenWritePlatformServiceImpl(final ScreenAssembler screenAssembler ,final ScreenRepositoryWrapper screenRepositoryWrapper) {
        this.screenAssembler = screenAssembler;
        this.screenRepositoryWrapper = screenRepositoryWrapper;
    }

    @Override
    public CommandProcessingResult create(JsonCommand command) {

        Screen screen = screenAssembler.fromJson(command);
        screenRepositoryWrapper.save(screen);
        CommandProcessingResult result = new CommandProcessingResultBuilder().withEntityId(screen.getId()).build();
        return result;

    }

    @Override
    public CommandProcessingResult deleteOrDisable(Long id, Boolean disable) {
        return null;
    }

    @Override
    public CommandProcessingResult update(Long id, JsonCommand command) {
        return null;
    }
}
