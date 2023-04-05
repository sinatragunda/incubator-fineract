/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 16:39
 */
package org.apache.fineract.presentation.menu.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class MenuNotFoundException extends AbstractPlatformResourceNotFoundException {

    public MenuNotFoundException(Long id) {
        super("Menu record not found", String.format("Menu with id % not found" ,id), id);
    }
}
