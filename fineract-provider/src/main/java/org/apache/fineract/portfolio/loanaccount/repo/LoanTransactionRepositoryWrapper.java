/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 March 2023 at 13:23
 */
package org.apache.fineract.portfolio.loanaccount.repo;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.exception.LoanTransactionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanTransactionRepositoryWrapper {

    private LoanTransactionRepository loanTransactionRepository;

    @Autowired
    public LoanTransactionRepositoryWrapper(LoanTransactionRepository loanTransactionRepository) {
        this.loanTransactionRepository = loanTransactionRepository;
    }

    public LoanTransaction findOneWithNotFoundDetection(Long id){
        
        LoanTransaction loanTransaction = loanTransactionRepository.findOne(id);
        
        Boolean has = OptionalHelper.isPresent(loanTransaction);
        if(!has){
            throw new LoanTransactionNotFoundException(id);
        }
        return loanTransaction;
    }
}
