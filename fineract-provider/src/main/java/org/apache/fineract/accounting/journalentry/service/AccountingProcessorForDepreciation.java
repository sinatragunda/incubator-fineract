/*

    Created by Sinatra Gunda
    At 7:44 AM on 9/14/2021

*/
package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.journalentry.data.DepreciationDTO;

public interface AccountingProcessorForDepreciation {

    public void createJournalEntriesForDepreciation(DepreciationDTO depreciationDTO);
}
