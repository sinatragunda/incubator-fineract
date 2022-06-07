/*

    Created by Sinatra Gunda
    At 1:26 AM on 9/29/2021

*/
package org.apache.fineract.portfolio.loanaccount.helper;

// Class that does the actual verification of wether one client is elliglbe for this loan or that and how much do they deserve etc

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.enumerations.LOAN_FACTOR_SOURCE_ACCOUNT_TYPE;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LoanFactorLoanResolver {


    // function returns nothing since it throws errors if something is wrong
    public static void loanFactor(LoanReadPlatformService loanReadPlatformService , SavingsAccountReadPlatformService savingsAccountReadPlatformService, LoanProductRepository loanProductRepository, FromJsonHelper fromJsonHelper , JsonCommand command, LoanProduct loanProduct, Client client) {

        LOAN_FACTOR_SOURCE_ACCOUNT_TYPE loanFactorSourceAccountType = loanProduct.getLoanFactorSourceAccountType();

        System.err.println("-----------------loan factor here is ---------------"+loanFactorSourceAccountType);

        LoanFactorSavingsAccountHelper loanFactorSavingsAccountHelper = new LoanFactorSavingsAccountHelper(loanProductRepository ,loanFactorSourceAccountType);

        boolean doLoanFactor = false;

        switch (loanFactorSourceAccountType){
            case NONE:
                boolean hasCrossLink = loanFactorSavingsAccountHelper.hasCrossLink();
                if(hasCrossLink){
                    doLoanFactor = true;
                    System.err.println("-----------------system is cross linked - -------------------");
                }
                break;
            ///none factored account then lets check if there is cross link product
            case SAVINGS:
                doLoanFactor = true;
                break;
        }

        if(doLoanFactor){

            Long loanFactorAccountId = fromJsonHelper.extractLongNamed(LoanApiConstants.loanFactorAccountIdParam ,command.parsedJson());

            BigDecimal principal = command.bigDecimalValueOfParameterNamed("principal");

            System.err.println("---------------proposed principal is --------------"+principal.doubleValue());

            System.err.println("---------------- is loan product present ?-------"+ Optional.ofNullable(loanProduct).isPresent());

            boolean transact = loanFactorSavingsAccountHelper.transact(savingsAccountReadPlatformService ,loanReadPlatformService ,loanProduct ,client ,loanFactorAccountId, principal);
            //if successful just proceed with this loan and throw no errors
            System.err.println("------------proceed with transaction -------------"+transact);
        }
    }

    public static void maxBalance(LoanProductRepository loanProductRepository ,Long productId){


        LoanProduct loanProduct = loanProductRepository.findOne(productId);

        Integer loanFactor = loanProduct.loanFactor();

        if(loanFactor > 0){
            // we using this class loan factor value
            return ;
        }

        // we looking for cross linked product
        List<LoanProduct> loanProductList = loanProductRepository.findAll();

        List<LoanProduct> crossLinkedProducts = loanProductList.stream().filter(e->{
            return e.isCrossLink();
        }).collect(Collectors.toList());

        Comparator<Integer> max = (left ,right)->{
            return left.compareTo(right) ;
        };

        loanFactor = crossLinkedProducts.stream().map(LoanProduct::loanFactor).max(max).orElse(0);

    }
//
//    private static LoanFactor setLoanFactor(BigDecimal totalLoanBalance ,BigDecimal savingsBalance ,int loanFactor){
//
//        LoanFactor loanFactor = new LoanFactor(totalLoanBalance);
//
//    }

}
