/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 02:24
 */
package org.apache.fineract.presentation.screen.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ScreenNotFoundException extends AbstractPlatformDomainRuleException {

    public ScreenNotFoundException(String name) {
        super("Screen not found ", String.format("Version screen %s not found in system" ,name),name);
    }

    public ScreenNotFoundException(Long id) {
        super("Screen not found ", String.format("Version screen with id %d not found in system" ,id),id);
    }
}
