/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 20:04
 */
package org.apache.fineract.presentation.menu.service;

import org.apache.fineract.infrastructure.generic.service.GenericReadPlatformService;
import org.apache.fineract.presentation.menu.data.MenuData;
import org.apache.fineract.utility.service.EnumeratedData;

public interface MenuReadPlatformService extends GenericReadPlatformService {
    public MenuData template();
    MenuData retrieveOne(Long id);
}
