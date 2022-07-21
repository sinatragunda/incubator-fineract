/*

    Created by Sinatra Gunda
    At 2:23 AM on 7/20/2022

*/
package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthOnSavingsAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;

@Service
public class SavingsAccountServiceWrapperImpl implements SavingsAccountServiceWrapper {


    private SavingsAccountAssembler savingsAccountAssembler ;
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository;
    private SavingsAccountDomainService savingsAccountDomainService;

    @Autowired
    public SavingsAccountServiceWrapperImpl(SavingsAccountAssembler savingsAccountAssembler, SavingsAccountReadPlatformService savingsAccountReadPlatformService, EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository, SavingsAccountDomainService savingsAccountDomainService) {
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.equityGrowthOnSavingsAccountRepository = equityGrowthOnSavingsAccountRepository;
        this.savingsAccountDomainService = savingsAccountDomainService;
    }

    @Override
    public SavingsAccountAssembler savingsAccountAssember() {
        return savingsAccountAssembler;
    }

    @Override
    public SavingsAccountReadPlatformService savingsAccountReadPlatformService() {
        return savingsAccountReadPlatformService;
    }

    @Override
    public EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository() {
        return equityGrowthOnSavingsAccountRepository;
    }

    @Override
    public SavingsAccountDomainService savingsAccountDomainService() {
        return savingsAccountDomainService;
    }
}
