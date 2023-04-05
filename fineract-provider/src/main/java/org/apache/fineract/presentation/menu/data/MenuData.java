/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 13:26
 */
package org.apache.fineract.presentation.menu.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.presentation.menu.enumerations.MENU_PLACEMENT;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.List;

public class MenuData implements EnumeratedData {

    private Long id ;
    private String name ;
    private MENU_PLACEMENT menuPlacement;
    private List<MenuItemData> menuItemDataList;
    private List<EnumOptionData> menuPlacementOptions;
    private List<EnumOptionData> menuItemsOptions ;
    private EnumOptionData menuPlacementData;

    public static MenuData template(List menuItemsOptions ,List menuPlacementOptions){
        return new MenuData(menuItemsOptions ,menuPlacementOptions);
    }

    public MenuData(Long id, String name, EnumOptionData menuPlacementData) {
        this.id = id;
        this.name = name;
        this.menuPlacementData = menuPlacementData;
    }

    public MenuData(List<EnumOptionData> menuItemsOptions ,List<EnumOptionData> menuPlacementOptions) {
        this.menuPlacementOptions = menuPlacementOptions;
        this.menuItemsOptions = menuItemsOptions;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setMenuItemDataList(List<MenuItemData> menuItemDataList) {
        this.menuItemDataList = menuItemDataList;
    }
}
