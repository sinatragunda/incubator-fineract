/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 13:26
 */
package org.apache.fineract.presentation.menu.domain;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.presentation.menu.enumerations.MENU_PLACEMENT;
import org.apache.fineract.presentation.menu.helper.MenuConstants;
import org.apache.fineract.utility.helper.EnumTemplateHelper;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.CascadeType ;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.*;

@Entity
@Table(name="m_menu")
public class Menu extends AbstractPersistableCustom<Long> {

    @Column(name="name")
    private String name ;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "menu_placement")
    private MENU_PLACEMENT menuPlacement;

    @OneToMany(mappedBy="menu",cascade=CascadeType.ALL ,fetch = FetchType.EAGER)
    private List<MenuTable> menuTableList ;

    @JoinColumn(name ="parent_menu_id")
    private Menu parentMenu ;

    private List<MenuItem> menuItemList;

    public Menu(String name, MENU_PLACEMENT menuPlacement, List<MenuTable> menuTableList) {
        this.name = name;
        this.menuPlacement = menuPlacement;
        this.menuTableList = menuTableList;
    }

    public Menu(String name,Menu parentMenu, MENU_PLACEMENT menuPlacement, List<MenuTable> menuTableList, List<MenuItem> menuItemList) {
        this.name = name;
        this.menuPlacement = menuPlacement;
        this.menuTableList = menuTableList;
        this.menuItemList = menuItemList;
        this.parentMenu = parentMenu;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInStringParameterNamed(GenericConstants.nameParam, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(GenericConstants.nameParam);
            actualChanges.put(GenericConstants.nameParam, newValue);
            //this.name = StringUtils.defaultIfEmpty(newValue, null);
            this.name = newValue;
        }

        if(command.isChangeInIntegerParameterNamed(MenuConstants.menuPlacementParam ,this.menuPlacement.ordinal())){
            final Integer newValue = command.integerValueOfParameterNamed(MenuConstants.menuPlacementParam);
            actualChanges.put(MenuConstants.menuPlacementParam ,newValue);
            this.menuPlacement = (MENU_PLACEMENT) EnumTemplateHelper.fromInt(MENU_PLACEMENT.values() ,newValue);
        }

        if(command.hasParameter(MenuConstants.parentMenuIdParam)){
            Boolean has = OptionalHelper.has(this.parentMenu);
            if(has){
                if(command.isChangeInLongParameterNamed(MenuConstants.parentMenuIdParam ,this.parentMenu.getId())){
                    final Long newValue = command.longValueOfParameterNamed(MenuConstants.parentMenuIdParam);
                    actualChanges.put(MenuConstants.parentMenuIdParam ,newValue);
                }
            }
            else{
                final Long newValue = command.longValueOfParameterNamed(MenuConstants.parentMenuIdParam);
                actualChanges.put(MenuConstants.parentMenuIdParam ,newValue);
            }
        }
        return actualChanges;
    }


    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public MENU_PLACEMENT getMenuPlacement() {
        return menuPlacement;
    }

    public List<MenuItem> getMenuItemList() {
        return menuItemList;
    }

    public void setMenuTableList(List<MenuTable> menuTableList) {
        this.menuTableList = menuTableList;
    }

    public List<MenuTable> getMenuTableList() {
        return menuTableList;
    }
}
