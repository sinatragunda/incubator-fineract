/*

    Created by Sinatra Gunda
    At 4:12 PM on 4/10/2022

*/
package org.apache.fineract.portfolio.loanaccount.template.service;


import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.template.NkwaziLoanTemplate;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.wese.helper.ComparatorUtility;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NkwaziLoanTemplateReadPlatformServiceImpl implements NkwaziLoanTemplateReadPlatformService {

    private SavingsAccountReadPlatformService savingsAccountReadPlatformService ;
    private LoanReadPlatformService loanReadPlatformService ;
    private LoanProductReadPlatformService loanProductReadPlatformService ;
    private ClientReadPlatformService clientReadPlatformService ;

    @Autowired
    public NkwaziLoanTemplateReadPlatformServiceImpl(SavingsAccountReadPlatformService savingsAccountReadPlatformService, LoanReadPlatformService loanReadPlatformService, LoanProductReadPlatformService loanProductReadPlatformService, ClientReadPlatformService clientReadPlatformService) {
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
    }

    public NkwaziLoanTemplate retrieveOne(Long clientId){

        Integer loanFactor = getLoanFactor();

        boolean isPresent = isLoanFactorPresent(loanFactor);

        // if not present return null then no factor is present
        if(!isPresent){
            return new NkwaziLoanTemplate() ;
        }

        BigDecimal savingsBalance = savingsAccountBalance(clientId);
        BigDecimal maxAllowable = savingsBalance.multiply(new BigDecimal(loanFactor));
        BigDecimal totalLoansOutstanding = totalLoansOutstanding(clientId);
        BigDecimal balanceAllowable = maxAllowable.subtract(totalLoansOutstanding);

        NkwaziLoanTemplate nkwaziLoanTemplate = new NkwaziLoanTemplate(maxAllowable ,balanceAllowable ,loanFactor ,savingsBalance);

        return nkwaziLoanTemplate ;

    }

    private Boolean isLoanFactorPresent(Integer loanFactor){

        System.err.println("-------------loan factor -------------"+loanFactor);

        int cmp = loanFactor.compareTo(0);

        boolean isEqualTo0 = ComparatorUtility.cmpToBoolean(cmp);

        /// return true since 0 is equal to loan factor
        // if its equal to 0 then its not set so return !

        System.err.println("======================is set ? ==========="+isEqualTo0);

        return !isEqualTo0;
    }

    private BigDecimal totalLoansOutstanding(Long clientId){

        Predicate<LoanAccountData> activeLoans = (e)->{
            return e.isActive();
        };

        Function<LoanAccountData ,BigDecimal> totalOutstanding = (e)->{
            BigDecimal balance =  e.getTotalOutstandingAmount();
            return balance;
        };

        Collection<LoanAccountData> loanAccountDataCollections = loanReadPlatformService.retrieveAllForClient(clientId);
        BigDecimal loansOutstanding = loanAccountDataCollections.stream().filter(activeLoans).map(totalOutstanding).reduce(BigDecimal.ZERO ,BigDecimal::add);
        System.err.println("--------------loans outstanding ----------------"+loansOutstanding);
        return loansOutstanding ;
    }

    private BigDecimal savingsAccountBalance(Long clientId){

        Collection<SavingsAccountData> savingsAccountDataCollection = savingsAccountReadPlatformService.retrieveAllForLookup(clientId);
        BigDecimal totalBalance = savingsAccountDataCollection.stream().map(SavingsAccountData::getAccountBalance).reduce(BigDecimal.ZERO ,BigDecimal::add);
        return totalBalance ;
    }


    private Integer getLoanFactor(){

        Integer loanFactor[] = {0};

        Predicate<LoanProductData> crossLinkPredicate = (e)->{
            return e.isCrossLink();
        };

        Comparator maxFactor = Comparator.comparing(LoanProductData::loanFactor);

        Collection<LoanProductData> loanProductCollection = loanProductReadPlatformService.retrieveAllLoanProducts();
        Optional maxFactorProduct = loanProductCollection.stream().filter(crossLinkPredicate).max(maxFactor);

        maxFactorProduct.ifPresent(e->{

            // not sure what actual value we have here
            LoanProductData loanProductData = (LoanProductData) maxFactorProduct.get();
            loanFactor[0] = loanProductData.loanFactor();
        });

        return loanFactor[0];
    }
}
