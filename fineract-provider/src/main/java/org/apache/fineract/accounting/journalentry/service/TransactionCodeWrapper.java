/*

    Created by Sinatra Gunda
    At 12:19 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.journalentry.domain.TransactionCode;

public interface TransactionCodeWrapper {
    TransactionCode findOneWithNotFoundException(Long id);
    Long save(TransactionCode transactionCode);
}
