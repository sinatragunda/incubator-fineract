/*

    Created by Sinatra Gunda
    At 1:21 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.commissions.constants.LoanAgentApiConstants;
import org.apache.fineract.portfolio.commissions.domain.LoanAgent;
import org.apache.fineract.portfolio.commissions.exceptions.LoanAgentRequireSavingsAccountException;
import org.apache.fineract.portfolio.commissions.repo.LoanAgentRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;


@Service
public class LoanAgentWritePlatformServiceImpl implements LoanAgentWritePlatformService {


    private SavingsAccountRepositoryWrapper savingsAccountRepositoryWrapper;
    private ClientRepositoryWrapper clientRepositoryWrapper ;
    private LoanAgentRepository loanAgentRepository ;


    @Autowired
    public LoanAgentWritePlatformServiceImpl(SavingsAccountRepositoryWrapper savingsAccountRepositoryWrapper, ClientRepositoryWrapper clientRepositoryWrapper, LoanAgentRepository loanAgentRepository) {
        this.savingsAccountRepositoryWrapper = savingsAccountRepositoryWrapper;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.loanAgentRepository = loanAgentRepository;
    }

    @Override
    public CommandProcessingResult createLoanAgent(JsonCommand jsonCommand){

        Long clientId = jsonCommand.longValueOfParameterNamed(LoanAgentApiConstants.clientIdParam);

        Long savingsAccountId = jsonCommand.longValueOfParameterNamed("savingsAccountId");
        
        final Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);

        // will be a problem if client doesnt have a savings account ....
        
        boolean hasSavingsAccount = Optional.ofNullable(savingsAccountId).isPresent();

        if(!hasSavingsAccount){
            // has no savings account then reverse everything now son
            throw new LoanAgentRequireSavingsAccountException(null);
        }

        SavingsAccount savingsAccount = this.savingsAccountRepositoryWrapper.findOneWithNotFoundDetection(savingsAccountId);

        final LoanAgent loanAgent = new LoanAgent(client ,savingsAccount);
        this.loanAgentRepository.save(loanAgent);

        return new CommandProcessingResultBuilder() //
                .withCommandId(jsonCommand.commandId()) //
                .withEntityId(loanAgent.getId()) //
                .withClientId(client.getId()) //
                .withOfficeId(client.officeId()) //
                .build();

    }
}
