/*

    Created by Sinatra Gunda
    At 2:22 AM on 7/20/2022

*/
package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthOnSavingsAccountRepository;

public interface SavingsAccountServiceWrapper {

    SavingsAccountAssembler savingsAccountAssember();
    SavingsAccountReadPlatformService savingsAccountReadPlatformService();
    EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository();
    SavingsAccountDomainService savingsAccountDomainService();
}
