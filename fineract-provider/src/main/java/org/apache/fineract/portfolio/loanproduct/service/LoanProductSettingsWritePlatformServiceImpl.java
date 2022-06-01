/*

    Created by Sinatra Gunda
    At 12:25 AM on 5/31/2022

*/
package org.apache.fineract.portfolio.loanproduct.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.loanproduct.api.LoanProductSettingsConstants;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductSettings;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductSettingsRepository;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class LoanProductSettingsWritePlatformServiceImpl implements LoanProductSettingsWritePlatformService {

    private LoanProductRepository loanProductRepository;
    private LoanProductSettingsRepository loanProductSettingsRepository ;

    @Autowired
    public LoanProductSettingsWritePlatformServiceImpl(LoanProductSettingsRepository loanProductSettingsRepository ,LoanProductRepository loanProductRepository){
        this.loanProductSettingsRepository = loanProductSettingsRepository ;
        this.loanProductRepository = loanProductRepository;
    }

    @Override
    public CommandProcessingResult create(JsonCommand command ,LoanProduct loanProduct){

        //command.hasParameter()

        final Long settlementAccountId = command.longValueOfParameterNamed(LoanProductSettingsConstants.settlementAccountIdParam);

        // not ideal to put optional here but lets put it here
        // loan product id how to set it now ?
        boolean isLoanProductPresent = Optional.ofNullable(loanProduct).isPresent();
        boolean isSettlmentAccountPresent = Optional.ofNullable(settlementAccountId).isPresent();

        if(!isLoanProductPresent){
            Long loanProductId = command.longValueOfParameterNamed(LoanProductSettingsConstants.loanProductIdParam);
            loanProduct = loanProductRepository.findOne(loanProductId);
        }

        // validate this settings now if all stuff is present but for now check only one item settlement account id
        if(!isSettlmentAccountPresent){
            return null;
        }

        LoanProductSettings loanProductSettings = new LoanProductSettings(loanProduct, settlementAccountId);

        loanProductSettingsRepository.save(loanProductSettings);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanProductSettings.getId()) //
                .build();

    }
}
