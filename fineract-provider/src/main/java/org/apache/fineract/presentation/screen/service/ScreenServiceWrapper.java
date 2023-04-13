/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:11
 */
package org.apache.fineract.presentation.screen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScreenServiceWrapper {

    private ScreenReadPlatformService screenReadPlatformService;
    private ScreenWritePlatformService screenWritePlatformService;

    @Autowired
    public ScreenServiceWrapper(ScreenReadPlatformService screenReadPlatformService, ScreenWritePlatformService screenWritePlatformService) {
        this.screenReadPlatformService = screenReadPlatformService;
        this.screenWritePlatformService = screenWritePlatformService;
    }

    public ScreenReadPlatformService getScreenReadPlatformService() {
        return screenReadPlatformService;
    }

    public ScreenWritePlatformService getScreenWritePlatformService() {
        return screenWritePlatformService;
    }
}
