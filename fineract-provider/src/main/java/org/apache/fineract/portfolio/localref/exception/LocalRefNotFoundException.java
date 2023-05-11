/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 01:59
 */
package org.apache.fineract.portfolio.localref.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LocalRefNotFoundException extends AbstractPlatformResourceNotFoundException {

    public LocalRefNotFoundException(final Long id){
        super("Local Ref value invalid", "Local ref with identifier " + id + " does not exist", id);
    }

    public LocalRefNotFoundException(final String name){
        super("Local Ref value invalid", "Local ref with name " + name + " does not exist", name);
    }
}
