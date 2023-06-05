/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 May 2023 at 11:45
 */
package org.apache.fineract.portfolio.localref.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class RefTableNotFoundException extends AbstractPlatformResourceNotFoundException {

    public RefTableNotFoundException(String name) {
        super(String.format("%s reference table not found",name), String.format("%s reference table not found",name), name);
    }
}
