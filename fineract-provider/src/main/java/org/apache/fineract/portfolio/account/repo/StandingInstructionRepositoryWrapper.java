/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 24 April 2023 at 10:38
 */
package org.apache.fineract.portfolio.account.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.account.domain.AccountTransferStandingInstruction;
import org.apache.fineract.portfolio.account.domain.StandingInstructionRepository;
import org.apache.fineract.portfolio.account.exception.StandingInstructionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StandingInstructionRepositoryWrapper {

    private StandingInstructionRepository repository ;

    @Autowired
    public StandingInstructionRepositoryWrapper(StandingInstructionRepository repository) {
        this.repository = repository;
    }

    public AccountTransferStandingInstruction findOneWithNotFoundDetection(Long id){

        AccountTransferStandingInstruction accountTransferStandingInstruction = repository.findOne(id) ;
        boolean has = OptionalHelper.isPresent(accountTransferStandingInstruction);
        if(!has){
            throw new StandingInstructionNotFoundException(id);
        }
        return accountTransferStandingInstruction;
    }
}
