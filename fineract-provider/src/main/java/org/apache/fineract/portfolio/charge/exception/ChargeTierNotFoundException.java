/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 April 2023 at 01:49
 */
package org.apache.fineract.portfolio.charge.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ChargeTierNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ChargeTierNotFoundException(Long id) {
        super("Charge tier not found",String.format("Charge tier with id %d not found ",id), id);
    }
}
