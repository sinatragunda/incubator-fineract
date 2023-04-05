package org.apache.fineract.presentation.menu.repo; /**
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
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.presentation.menu.domain.Menu;
import org.apache.fineract.presentation.menu.exception.MenuNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Component
public class MenuRepositoryWrapper {

    private final MenuRepository repository;

    @Autowired
    public MenuRepositoryWrapper(final MenuRepository repository) {
        this.repository = repository;
    }

    public Menu findOneWithNotFoundDetection(final Long id) {

        final Menu menu = this.repository.findOne(id);
        boolean has = OptionalHelper.isPresent(menu);

        if (!has) { throw new MenuNotFoundException(id); }

        return menu;
    }

    public Menu save(Menu menu){
        this.repository.saveAndFlush(menu);
        return menu ;
    }

    public void delete(Menu menu){
        this.repository.delete(menu);
    }

}
