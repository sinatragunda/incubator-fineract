package org.apache.fineract.presentation.menu.service; /**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.presentation.menu.data.MenuDataValidator;
import org.apache.fineract.presentation.menu.domain.Menu;
import org.apache.fineract.presentation.menu.domain.MenuItem;
import org.apache.fineract.presentation.menu.domain.MenuTable;
import org.apache.fineract.presentation.menu.helper.MenuConstants;
import org.apache.fineract.presentation.menu.repo.MenuRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuWritePlatformServiceImpl implements MenuWritePlatformService {

    private final MenuAssembler menuAssembler;
    private final MenuRepositoryWrapper menuRepositoryWrapper;
    private final MenuDataValidator menuDataValidator;

    @Autowired
    public MenuWritePlatformServiceImpl(final MenuAssembler menuAssembler,final MenuRepositoryWrapper menuRepositoryWrapper ,final MenuDataValidator menuDataValidator) {
        this.menuAssembler = menuAssembler;
        this.menuRepositoryWrapper = menuRepositoryWrapper;
        this.menuDataValidator = menuDataValidator;
    }

    public CommandProcessingResult create(final JsonCommand command) {

        System.err.println("-------------------create menu here ");
        
        Menu menu  = menuAssembler.menuFromJson(command);
        
        // need some validation here that includes menu values to not allow multiplicity
        menuDataValidator.validateForCreate(menu);

        final List<MenuItem> menuItems = menu.getMenuItemList();

        List<MenuTable> menuTableList = new ArrayList<>();
        for(MenuItem menuItem  : menuItems){
            MenuTable menuTable = new MenuTable(menu ,menuItem);
            menuTableList.add(menuTable);
        }

        menu.setMenuTableList(menuTableList);

        System.err.println("==============now save this shit -==================");
        menuRepositoryWrapper.save(menu);

        return new CommandProcessingResultBuilder() //
                .withEntityId(menu.getId()) //
                .build();

    }

    public CommandProcessingResult update(Long id ,final JsonCommand command) {

        System.err.println("-------------update record son ----------");

        final Menu menu = this.menuRepositoryWrapper.findOneWithNotFoundDetection(id);

        final Map<String, Object> changes = menu.update(command);

        if(!changes.isEmpty()){

            if(changes.containsKey(MenuConstants.parentMenuIdParam)){
                Long parentMenuId = (Long)changes.get(MenuConstants.parentMenuIdParam);
                Menu parentMenu = this.menuRepositoryWrapper.findOneWithNotFoundDetection(parentMenuId);
                menu.setParentMenu(parentMenu);
            }
        }

        boolean hasChanges = !changes.isEmpty();

        List<MenuItem> changesMenuItemList = menuAssembler.menuItemsFromJsonIdKeys(command);
        List<MenuTable> menuTables = menu.getMenuTableList();
        List<MenuItem> currentItems = new ArrayList<>();

        Consumer<MenuTable> getMenuItems = (e)->{
            MenuItem item = e.getMenuItem();
            currentItems.add(item);
        };

        menuTables.stream().forEach(getMenuItems);

        boolean equals = currentItems.equals(changesMenuItemList);

        if(!equals){

            hasChanges = true ;

            List<MenuTable> menuTableList = new ArrayList<>();
            Consumer<MenuItem> addToMenuTable = (e)->{
                MenuTable menuTable = new MenuTable(menu ,e);
                menuTableList.add(menuTable);
            };
            changesMenuItemList.stream().forEach(addToMenuTable);
            menu.setMenuTableList(menuTableList);
        }

        System.err.println("-------------------has changes ? "+hasChanges);

        if(hasChanges) {
            menuDataValidator.validateForUpdate(menu);
            this.menuRepositoryWrapper.save(menu);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .with(changes) //
                .build();
    }


    @Override
    public CommandProcessingResult delete(Long id) {

        final Menu menuItem = this.menuRepositoryWrapper.findOneWithNotFoundDetection(id);
        this.menuRepositoryWrapper.delete(menuItem);

        return new CommandProcessingResultBuilder() //
                .withCommandId(null) //
                .build();
    }


}