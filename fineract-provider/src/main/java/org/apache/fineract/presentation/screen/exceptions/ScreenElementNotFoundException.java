/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 09:18
 */
package org.apache.fineract.presentation.screen.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ScreenElementNotFoundException extends AbstractPlatformDomainRuleException {

    public ScreenElementNotFoundException(Long id) {
        super("Screen element not found ", String.format("Screen element with id %d not found in system" ,id),id);
    }
}
