/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:07
 */
package org.apache.fineract.presentation.screen.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.presentation.screen.data.ScreenData;
import org.apache.fineract.presentation.screen.domain.Screen;

import java.util.Collection;
import java.util.List;

public interface ScreenReadPlatformService {
    public Collection<ScreenData> retrieveAll();
    public ScreenData retrieveOne(Long id);

    public ScreenData retrieveOneByName(String name);
}
