/*

    Created by Sinatra Gunda
    At 12:18 PM on 9/27/2022

*/
package org.apache.fineract.accounting.journalentry.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class TransactionCodeDuplicateException extends AbstractPlatformResourceNotFoundException {

    public TransactionCodeDuplicateException(final Long code) {
        super("error.msg.transaction.code.duplicate", "Transaction code " + code + " already exist in the system ", code);
    }
}
