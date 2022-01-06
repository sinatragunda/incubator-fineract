/*

    Created by Sinatra Gunda
    At 4:47 AM on 1/5/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.commissions.constants.CommissionsApiConstants;
import org.apache.fineract.portfolio.commissions.data.LoanAgentData;
import org.apache.fineract.portfolio.commissions.domain.AttachedCommissionCharges;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.domain.LoanAgent;
import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;
import org.apache.fineract.portfolio.commissions.helper.CommissionsHelper;
import org.apache.fineract.portfolio.commissions.repo.AttachedCommissionChargesRepository;
import org.apache.fineract.portfolio.commissions.repo.LoanAgentRepository;
import org.apache.fineract.portfolio.commissions.repo.LoansFromAgentsRepository;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class LoansFromAgentsWritePlatformServiceImpl implements LoansFromAgentsWritePlatformService{

    private AttachedCommissionChargesRepository attachedCommissionChargesRepository;
    private LoanAgentRepository loanAgentRepository ;
    private LoansFromAgentsRepository loansFromAgentsRepository;
    private LoanRepository loanRepository ;


    @Autowired
    public LoansFromAgentsWritePlatformServiceImpl(AttachedCommissionChargesRepository attachedCommissionChargesRepository, LoanAgentRepository loanAgentRepository, LoansFromAgentsRepository loansFromAgentsRepository, LoanRepository loanRepository) {
        this.attachedCommissionChargesRepository = attachedCommissionChargesRepository;
        this.loanAgentRepository = loanAgentRepository;
        this.loansFromAgentsRepository = loansFromAgentsRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public CommandProcessingResult create(JsonCommand jsonCommand){

        // is string utils empty class here
        Long loanAgentId  = jsonCommand.longValueOfParameterNamed(CommissionsApiConstants.loanFromAgentsIdParam);
        Long loanId  = jsonCommand.longValueOfParameterNamed(CommissionsApiConstants.loanIdParam);

        System.err.println("----------------------create loans from agents through repo ----------------");

        // should we have any validations here or by the time it comes here should have been validated already ? 
        LoanAgent loanAgent = this.loanAgentRepository.findOne(loanAgentId);
        Loan loan = this.loanRepository.findOne(loanId);

        LoansFromAgents loansFromAgents = new LoansFromAgents(loan ,loanAgent);
        this.loansFromAgentsRepository.save(loansFromAgents);

        System.err.println("-------------------value created with id of --------------"+loansFromAgents.getId());

        return new CommandProcessingResultBuilder() //
                .withEntityId(loansFromAgents.getId()) //
                .build();
        
    }
}
