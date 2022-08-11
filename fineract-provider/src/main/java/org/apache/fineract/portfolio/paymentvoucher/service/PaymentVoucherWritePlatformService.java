/*

    Created by Sinatra Gunda
    At 10:55 AM on 8/10/2022

*/
package org.apache.fineract.portfolio.paymentvoucher.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.paymentvoucher.domain.PaymentVoucher;

import java.util.List;

public interface PaymentVoucherWritePlatformService {

    public List<PaymentVoucher> findJournalEntries(JsonCommand command);
    public CommandProcessingResult createEntriesForVoucher(JsonCommand jsonCommand);


}


