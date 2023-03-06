/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 01:00
 */
package org.apache.fineract.portfolio.localref.service;

import org.apache.fineract.infrastructure.codes.domain.Code;
import org.apache.fineract.infrastructure.codes.domain.CodeRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.localref.api.LocalRefApiConstants;
import org.apache.fineract.portfolio.localref.data.LocalRefDataValidator;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.domain.LocalRefAssembler;
import org.apache.fineract.portfolio.localref.helper.LocalRefConstants;
import org.apache.fineract.portfolio.localref.repo.LocalRefRepository;
import org.apache.fineract.portfolio.localref.repo.LocalRefRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LocalRefWritePlatformServiceImpl implements LocalRefWritePlatformService{

    private LocalRefDataValidator localRefDataValidator;
    private LocalRefAssembler localRefAssembler;
    private LocalRefRepository localRefRepository;

    private LocalRefRepositoryWrapper localRefRepositoryWrapper;
    private CodeRepository codeRepository;

    @Autowired
    public LocalRefWritePlatformServiceImpl(LocalRefDataValidator localRefDataValidator, LocalRefAssembler localRefAssembler, LocalRefRepository localRefRepository ,LocalRefRepositoryWrapper localRefRepositoryWrapper) {
        this.localRefDataValidator = localRefDataValidator;
        this.localRefAssembler = localRefAssembler;
        this.localRefRepository = localRefRepository;
        this.localRefRepositoryWrapper = localRefRepositoryWrapper;
    }

    @Override
    public CommandProcessingResult create(JsonCommand jsonCommand){

        System.err.println("==========is it create or update ? ");
        localRefDataValidator.validateForCreate(jsonCommand.json());
        LocalRef localRef = localRefAssembler.assembleFromJson(jsonCommand);
        localRefRepository.save(localRef);

        CommandProcessingResult commandProcessingResult = new CommandProcessingResultBuilder().withEntityId(localRef.getId()).build();

        return commandProcessingResult;
    }

    @Override
    public CommandProcessingResult update(Long id ,JsonCommand command){

        System.err.println("============update this now son "+id);

        LocalRef localRef = localRefRepositoryWrapper.findOneWithoutNotFoundDetection(id);

        Map<String ,Object> changes = localRef.update(command);

        System.err.println("============update this now son  with changes "+changes.size());


        boolean hasChanges = !changes.isEmpty();
        
        if(hasChanges){

            if(changes.containsKey(LocalRefConstants.codeIdParam)){
                Long codeId = (Long) changes.get(LocalRefConstants.codeIdParam);
                //Code code = codeRepository.findOne(codeId);
                //localRef.updateCode(code);
            }

            if(changes.containsKey(LocalRefConstants.descriptionParam)){

            }
        }

        localRefRepositoryWrapper.save(localRef);

        CommandProcessingResult result = new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(id) //
                    .with(changes) //
                    .build();

        return result;
    }
    
}
