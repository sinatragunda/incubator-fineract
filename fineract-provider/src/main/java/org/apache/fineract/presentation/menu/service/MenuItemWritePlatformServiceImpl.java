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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.presentation.menu.domain.MenuItem;
import org.apache.fineract.presentation.menu.repo.MenuItemRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuItemWritePlatformServiceImpl implements MenuItemWritePlatformService {

    private final MenuItemRepositoryWrapper menuItemRepositoryWrapper;
    private final MenuAssembler menuAssembler;

    @Autowired
    public MenuItemWritePlatformServiceImpl(final MenuAssembler menuAssembler ,final MenuItemRepositoryWrapper menuItemRepositoryWrapper) {
        this.menuAssembler = menuAssembler;
        this.menuItemRepositoryWrapper = menuItemRepositoryWrapper;
    }



    @Override
    public CommandProcessingResult create(final JsonCommand command) {

        final List<MenuItem> menuItems = menuAssembler.menuItemsFromJson(command);

        Consumer<MenuItem> consume = (e)->{
            this.menuItemRepositoryWrapper.save(e);
        };

        menuItems.stream().forEach(consume);

        MenuItem menuItem = menuItems.stream().findFirst().get();

        return new CommandProcessingResultBuilder() //
                .withEntityId(menuItem.getId()) //
                .build();

    }

    @Override
    public CommandProcessingResult update(Long id ,final JsonCommand command) {

        final MenuItem menuItem = this.menuItemRepositoryWrapper.findOneWithNotFoundDetection(id);

        final Map<String, Object> changes = menuItem.update(command);

        if (!changes.isEmpty()) {
            this.menuItemRepositoryWrapper.save(menuItem);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .with(changes) //
                .build();
    }


    @Override
    public CommandProcessingResult delete(final Long id) {

        final MenuItem menuItem = this.menuItemRepositoryWrapper.findOneWithNotFoundDetection(id);
        this.menuItemRepositoryWrapper.delete(menuItem);

        return new CommandProcessingResultBuilder() //
                .withCommandId(null) //
                .build();
    }
}
