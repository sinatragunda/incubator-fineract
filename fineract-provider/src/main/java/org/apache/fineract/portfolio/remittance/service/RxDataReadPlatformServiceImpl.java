package org.apache.fineract.portfolio.remittance.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.remittance.data.RxData;
import org.apache.fineract.portfolio.remittance.enumerations.IDENTIFICATION_TYPE;
import org.apache.fineract.portfolio.remittance.enumerations.RX_PROVIDER;

import java.util.Collection;

import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.enumerations.ACCOUNT_PROTOTYPE;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
public class RxDataReadPlatformServiceImpl implements RxReadPlatformService{

    private PlatformSecurityContext context ;
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService;

    @Autowired
    public RxDataReadPlatformServiceImpl(final PlatformSecurityContext context ,final SavingsAccountReadPlatformService savingsAccountReadPlatformService) {
        this.context = context;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
    }

    public RxData template(){

        Collection<EnumOptionData> accountTypeOptions = null;
        Collection<EnumOptionData> providerOptions = RX_PROVIDER.template();
        Collection<EnumOptionData> identificationTypeOptions = IDENTIFICATION_TYPE.template();
        Collection<SavingsAccountData> savingsAccounts = savingsAccountReadPlatformService.retrieveAllByAccountType(ACCOUNT_PROTOTYPE.SETTLEMENT);

        return new RxData(providerOptions ,identificationTypeOptions ,null ,savingsAccounts);
    }


}
