/*

    Created by Sinatra Gunda
    At 12:16 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.exception;

import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TransactionCodeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public TransactionCodeNotFoundException(final Long id) {
        super("error.msg.transaction.id.invalid", "Transaction code " + id + " not found ", id);
    }
}
