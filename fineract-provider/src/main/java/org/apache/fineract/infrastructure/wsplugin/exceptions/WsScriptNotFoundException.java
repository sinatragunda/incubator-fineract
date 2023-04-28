/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 12:03
 */
package org.apache.fineract.infrastructure.wsplugin.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class WsScriptNotFoundException extends AbstractPlatformResourceNotFoundException {


    public WsScriptNotFoundException(Long id) {
        super(String.format("Function with id %d not found" ,id), String.format("Function with id %d not found" ,id), id);
    }
}
