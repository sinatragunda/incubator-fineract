/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 29 May 2023 at 12:00
 */
package org.apache.fineract.presentation.menu.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class MenuPlacementDoesNotAllowMultiplicity extends AbstractPlatformDomainRuleException {

    public MenuPlacementDoesNotAllowMultiplicity(String arg) {
        super(String.format("Menu placement type does not allow multiplicity ,edit the existing one or delete it" ,arg),String.format("Menu placement type does not allow multiplicity ,edit the existing one or delete it" ,arg), arg);
    }
}
