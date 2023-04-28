/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 13:15
 */
package org.apache.fineract.infrastructure.wsplugin.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScriptContainer;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScriptContainerAssembler;
import org.apache.fineract.infrastructure.wsplugin.repo.WsScriptContainerRepositoryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WsScriptWritePlatformServiceImpl implements WsScriptWritePlatformService{


    private WsScriptContainerRepositoryWrapper wsScriptContainerRepositoryWrapper;
    private WsScriptContainerAssembler wsScriptContainerAssembler;

    @Autowired
    public WsScriptWritePlatformServiceImpl(WsScriptContainerAssembler wsScriptContainerAssembler, WsScriptContainerRepositoryWrapper wsScriptContainerRepositoryWrapper) {
        this.wsScriptContainerAssembler = wsScriptContainerAssembler;
        this.wsScriptContainerRepositoryWrapper = wsScriptContainerRepositoryWrapper;
    }

    @Override
    public CommandProcessingResult create(Document document, JsonCommand command){

        WsScriptContainer wsScriptContainer = wsScriptContainerAssembler.assembleFromJson(command);
        wsScriptContainer.setDocument(document);
        wsScriptContainerRepositoryWrapper.save(wsScriptContainer);
        Long id = wsScriptContainer.getId();
        CommandProcessingResult result = new CommandProcessingResultBuilder().withEntityId(id).build();
        return result;
    }
}
