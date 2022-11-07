/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 November 2022 at 03:09
 */
package org.apache.fineract.portfolio.remittance.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class RxDealNotFoundException extends AbstractPlatformResourceNotFoundException {

    public RxDealNotFoundException(final Long id) {
        super("Rx Deal not found", "Rx Deal with transaction " + id + " does not exist", id);
    }


    public RxDealNotFoundException(String accountNumber) {
        super("Rx Key not found", "Rx transaction with key "+accountNumber+" not found.");
    }
}
