/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 May 2023 at 08:59
 */
package org.apache.fineract.portfolio.ssbpayments.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SsbTransactionNotFound extends AbstractPlatformResourceNotFoundException {

    public SsbTransactionNotFound(Long id) {
        super(String.format("SSB Transaction with id %d not found" ,id),String.format("SSB Transaction with id %d not found" ,id) , id);
    }
}
