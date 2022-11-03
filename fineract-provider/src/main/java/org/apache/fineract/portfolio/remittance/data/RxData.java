package org.apache.fineract.portfolio.remittance.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;

import java.util.Collection;

public class RxData {

    private Collection<EnumOptionData> providersOptions ;
    private Collection<EnumOptionData> identificationTypeOptions ;
    private Collection<EnumOptionData> transactionStateOptions ;
    private Collection<SavingsAccountData> savingsAccounts ;

    public RxData(Collection<EnumOptionData> providersOptions, Collection<EnumOptionData> identificationTypeOptions, Collection<EnumOptionData> transactionStateOptions ,Collection<SavingsAccountData> savingsAccounts) {
        this.providersOptions = providersOptions;
        this.identificationTypeOptions = identificationTypeOptions;
        this.transactionStateOptions = transactionStateOptions;
        this.savingsAccounts = savingsAccounts;
    }

    public Collection<SavingsAccountData> getSavingsAccounts() {
        return savingsAccounts;
    }

    public void setSavingsAccounts(Collection<SavingsAccountData> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }

    public Collection<EnumOptionData> getProvidersOptions() {
        return providersOptions;
    }

    public Collection<EnumOptionData> getIdentificationTypeOptions() {
        return identificationTypeOptions;
    }

    public Collection<EnumOptionData> getTransactionStateOptions() {
        return transactionStateOptions;
    }
}
