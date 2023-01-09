/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 January 2023 at 15:43
 */
package org.apache.fineract.portfolio.loanaccount.service;


import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanTransactionReadPlatformServiceImpl implements LoanTransactionReadPlatformService{

    
    public LoanTransactionReadPlatformServiceImpl(){}

    @Override
    public BigDecimal interestAccrued(LoanReadPlatformService loanReadPlatformService, Long loanId) {

        Collection<LoanTransactionData> loanTransactionDataCollection = loanReadPlatformService.retrieveLoanTransactions(loanId);

        Predicate<LoanTransactionData> accrual = (e)-> e.getTransactionType().isAccrual();

        BigDecimal totalAccrued = loanTransactionDataCollection.stream().filter(accrual).map((e)-> e.getAmount()).reduce(BigDecimal.ZERO ,BigDecimal::add);

        return totalAccrued;
    }
}
