/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 January 2023 at 15:42
 */
package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.utility.service.DataEnumerationService;

import java.math.BigDecimal;
import java.util.Collection;

public interface LoanTransactionReadPlatformService extends DataEnumerationService {

    public BigDecimal interestAccrued(LoanReadPlatformService loanReadPlatformService, final Long loanId);
    public LoanTransactionData retrieveOne(LoanReadPlatformService loanReadPlatformService , Long loanTransactionId);

    public Collection<LoanTransactionData> retrieveAll();
}
