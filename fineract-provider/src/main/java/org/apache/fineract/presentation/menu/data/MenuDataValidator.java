/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 29 May 2023 at 11:54
 */
package org.apache.fineract.presentation.menu.data;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.generic.exceptions.GenericErrorResourceNotFound;
import org.apache.fineract.presentation.menu.domain.Menu;
import org.apache.fineract.presentation.menu.domain.MenuItem;
import org.apache.fineract.presentation.menu.domain.MenuTable;
import org.apache.fineract.presentation.menu.enumerations.MENU_PLACEMENT;
import org.apache.fineract.presentation.menu.exception.MenuPlacementDoesNotAllowMultiplicity;
import org.apache.fineract.presentation.menu.exception.MenuPlacementMissingException;
import org.apache.fineract.presentation.menu.repo.MenuRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List ;
@Component
public class MenuDataValidator {

    private MenuRepositoryWrapper menuRepositoryWrapper;

    @Autowired
    public MenuDataValidator(MenuRepositoryWrapper menuRepositoryWrapper) {
        this.menuRepositoryWrapper = menuRepositoryWrapper;
    }

    public void validateForCreate(Menu menu){

        MENU_PLACEMENT menuPlacement = OptionalHelper.optionalOf(menu.getMenuPlacement() ,null);
        boolean hasPlacement = OptionalHelper.has(menuPlacement);

        if(!hasPlacement){
            throw new MenuPlacementMissingException();
        }

        Menu tempMenu = menuRepositoryWrapper.findOneByMenuPlacement(menuPlacement);

        if(!menuPlacement.isAllowMultiplicity()){
            boolean has = OptionalHelper.has(tempMenu);
            if(has){
                throw new MenuPlacementDoesNotAllowMultiplicity(menuPlacement.getCode());
            }
        }
    }

    public void validateForUpdate(Menu menu){

        MENU_PLACEMENT menuPlacement = OptionalHelper.optionalOf(menu.getMenuPlacement() ,null);
        boolean hasPlacement = OptionalHelper.has(menuPlacement);

        if(!hasPlacement){
            throw new MenuPlacementMissingException();
        }

        List<MenuTable> menuTableList = menu.getMenuTableList();
        if(menuTableList.isEmpty()){
            throw new GenericErrorResourceNotFound("Menu items is empty ,should contain at least one item");
        }
    }
}
