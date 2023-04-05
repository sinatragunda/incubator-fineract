/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 16:41
 */
package org.apache.fineract.presentation.menu.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class MenuItemNotFoundException extends AbstractPlatformResourceNotFoundException {

    public MenuItemNotFoundException(Long id) {
        super("Menu item not found", String.format("Menu item with id %d not found" ,id), id);
    }
}
