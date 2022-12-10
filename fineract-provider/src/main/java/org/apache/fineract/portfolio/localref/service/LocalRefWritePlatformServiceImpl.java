/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 01:00
 */
package org.apache.fineract.portfolio.localref.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.localref.data.LocalRefDataValidator;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.domain.LocalRefAssembler;
import org.apache.fineract.portfolio.localref.repo.LocalRefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class LocalRefWritePlatformServiceImpl implements LocalRefWritePlatformService{

    private LocalRefDataValidator localRefDataValidator;
    private LocalRefAssembler localRefAssembler;
    private LocalRefRepository localRefRepository;

    @Autowired
    public LocalRefWritePlatformServiceImpl(LocalRefDataValidator localRefDataValidator, LocalRefAssembler localRefAssembler, LocalRefRepository localRefRepository) {
        this.localRefDataValidator = localRefDataValidator;
        this.localRefAssembler = localRefAssembler;
        this.localRefRepository = localRefRepository;
    }

    @Override
    public CommandProcessingResult create(JsonCommand jsonCommand){

        localRefDataValidator.validateForCreate(jsonCommand.json());
        LocalRef localRef = localRefAssembler.assembleFromJson(jsonCommand);
        localRefRepository.save(localRef);

        CommandProcessingResult commandProcessingResult = new CommandProcessingResultBuilder().withEntityId(localRef.getId()).build();

        return commandProcessingResult;
    }
}
