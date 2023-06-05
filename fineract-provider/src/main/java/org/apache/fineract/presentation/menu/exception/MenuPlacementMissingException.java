/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 29 May 2023 at 12:05
 */
package org.apache.fineract.presentation.menu.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class MenuPlacementMissingException extends AbstractPlatformResourceNotFoundException {

    public MenuPlacementMissingException() {
        super("Menu placement is missing ,please add","Menu placement is missing");
    }
}
