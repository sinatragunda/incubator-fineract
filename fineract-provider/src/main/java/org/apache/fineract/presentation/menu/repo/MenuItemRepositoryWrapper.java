/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 13:10
 */
package org.apache.fineract.presentation.menu.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.presentation.menu.domain.Menu;
import org.apache.fineract.presentation.menu.domain.MenuItem;
import org.apache.fineract.presentation.menu.exception.MenuItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuItemRepositoryWrapper {

    private final MenuItemRepository repository;

    @Autowired
    public MenuItemRepositoryWrapper(final MenuItemRepository repository) {
        this.repository = repository;
    }

    public MenuItem findOneWithNotFoundDetection(final Long id) {

        final MenuItem menu = this.repository.findOne(id);
        boolean has = OptionalHelper.isPresent(menu);
        if (!has) { throw new MenuItemNotFoundException(id); }

        return menu;
    }

    public MenuItem findByShortcut(String shortcut){
        final MenuItem menuItem = this.repository.findByShortcut(shortcut);
        return menuItem;
    }

    public MenuItem save(MenuItem menu){
        this.repository.saveAndFlush(menu);
        return menu ;
    }

    public void delete(MenuItem menu){
        this.repository.delete(menu);
    }

}
