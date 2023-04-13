/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:05
 */
package org.apache.fineract.presentation.screen.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.presentation.screen.data.ScreenData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ScreenCommandReciever {

    private final ScreenServiceWrapper screenServiceWrapper;
    private ScreenWritePlatformService screenWritePlatformService;
    private ScreenReadPlatformService screenReadPlatformService;

    @Autowired
    public ScreenCommandReciever(ScreenServiceWrapper screenServiceWrapper) {
        this.screenServiceWrapper = screenServiceWrapper;
        this.screenWritePlatformService = screenServiceWrapper.getScreenWritePlatformService();
        this.screenReadPlatformService = screenServiceWrapper.getScreenReadPlatformService();
    }

    public CommandProcessingResult createScreen(JsonCommand command){
        System.err.println("===================create screen son ");
        return screenWritePlatformService.create(command);
    }

    public Collection<ScreenData> retrieveAll(){
        return  screenReadPlatformService.retrieveAll();
    }

    public ScreenData retrieveOne(Long id){
        return screenReadPlatformService.retrieveOne(id);
    }




}
