/*

    Created by Sinatra Gunda
    At 12:41 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.journalentry.data.TransactionCodeData;

import java.util.List;

public interface TransactionCodeReadPlatformService {

    TransactionCodeData retrieveOne(Long id);
    List<TransactionCodeData> retrieveAll();
}
