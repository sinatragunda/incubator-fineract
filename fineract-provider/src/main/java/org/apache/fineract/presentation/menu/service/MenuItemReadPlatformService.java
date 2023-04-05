/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 12:56
 */
package org.apache.fineract.presentation.menu.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.generic.service.GenericReadPlatformService;
import org.apache.fineract.presentation.menu.data.MenuItemData;

import java.util.Collection;
import java.util.List;

public interface MenuItemReadPlatformService extends GenericReadPlatformService {
    MenuItemData template();
    MenuItemData retrieveOne(Long id);
    List<MenuItemData> retrieveAllByMenuId(Long id);
}
