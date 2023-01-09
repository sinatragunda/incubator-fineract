/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 January 2023 at 15:42
 */
package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;

import java.math.BigDecimal;

public interface LoanTransactionReadPlatformService {

    public BigDecimal interestAccrued(LoanReadPlatformService loanReadPlatformService, final Long loanId);
}
