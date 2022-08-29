/*

    Created by Sinatra Gunda
    At 12:32 AM on 8/21/2021

*/
package org.apache.fineract.portfolio.loanaccount.helper;

/*Created by Sinatra Gunda
  At 10:27 AM on 12/10/2020 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.*;
import java.math.BigDecimal ;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.exception.NonAttachedLoanFactorAccountException;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.enumerations.LOAN_FACTOR_SOURCE_ACCOUNT_TYPE;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountSummary;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountData ;
import org.apache.fineract.portfolio.accountdetails.data.ShareAccountSummaryData;
import  org.apache.fineract.portfolio.loanaccount.data.LoanAccountData ;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;

////added  10/27/2020
import org.apache.fineract.portfolio.loanaccount.exception.LoanFactorException ;

/// added 30/10/2020
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class LoanFactorSavingsAccountHelper {

    private ShareAccountSummaryData shareAccountSummaryData ;
    private ShareAccountData shareAccountData ;
    private SavingsAccountData savingsAccountData;
    private SavingsAccountSummary savingsAccountSummary;
    private LoanReadPlatformService loanReadPlatformService ;
    private BigDecimal principal ;
    private Double totalShareValue = null ;
    private LoanProduct crossLinkLoanProduct = null ;
    private LoanProductRepository loanProductRepository;
    private int loanFactor ;
    private LOAN_FACTOR_SOURCE_ACCOUNT_TYPE loanFactorSourceAccount = null ;


    public LoanFactorSavingsAccountHelper(ShareAccountData shareAccountData , LoanReadPlatformService loanReadPlatformService , LoanProductRepository loanProductRepository, BigDecimal principal){
        this.shareAccountData = shareAccountData ;
        this.loanReadPlatformService = loanReadPlatformService ;
        this.principal = principal ;
        this.loanProductRepository = loanProductRepository;

        // init function
        crossLinking(loanProductRepository);

    }

    public LoanFactorSavingsAccountHelper(){}

    public LoanFactorSavingsAccountHelper(LoanProductRepository loanProductRepository, LOAN_FACTOR_SOURCE_ACCOUNT_TYPE loanFactorSourceAccountType){
        this.loanFactorSourceAccount = loanFactorSourceAccountType;
        this.loanProductRepository = loanProductRepository;

        crossLinking(loanProductRepository);
    }

    public boolean transact(SavingsAccountReadPlatformService savingsAccountReadPlatformService ,LoanReadPlatformService loanReadPlatformService ,LoanProduct loanProduct ,Client client ,Long loanFactorAccountId ,BigDecimal principal ,List excludeLoansList){
        return savingsAccountBasedLoanFactoring(savingsAccountReadPlatformService ,loanReadPlatformService ,loanProductRepository,loanProduct ,client ,loanFactorAccountId ,principal ,excludeLoansList);
    }


    public boolean savingsAccountBasedLoanFactoring(SavingsAccountReadPlatformService savingsAccountReadPlatformService, LoanReadPlatformService loanReadPlatformService ,LoanProductRepository loanProductRepository , LoanProduct loanProduct , Client client ,Long loanFactorAccountId , BigDecimal principal ,List excludeLoansList){

        if(loanFactorAccountId==null){
            String message = "Non attached savings account to verify loan factoring process .Please select one ";
            throw new NonAttachedLoanFactorAccountException(message,message ,"Standard Error");
        }

        //System.err.println("-------------------We are dealing with client -------------"+client.getDisplayName());

        //System.err.println("------------------------is number present here "+Optional.ofNullable(loanProduct.loanFactor()).isPresent());

        this.loanFactor = Optional.ofNullable(loanProduct.loanFactor()).orElse(0);

        //System.err.println("-----------loan factor is ---------------"+this.loanFactor);

        Long loanProductId = loanProduct.getId();

        boolean isCrossLink = loanProduct.isCrossLink();

        //System.err.println("-------------------loan factor to use for all this nonsense is "+this.loanFactor);

        if(this.loanFactor <= 0){

            /// check if other products have their own loan factoring that is cross link
            if(hasCrossLink()){
          //      System.err.println("-----------product has no loan factor hence using others------------- ");
                this.loanFactor = crossLinkLoanProduct.loanFactor();
                isCrossLink = hasCrossLink();
                if(loanFactor <=0){
                    return true ;
                }
            }
            else{
            //    System.err.println("-----------no cross link configured in this system");
                return true;
            }
        }

        SavingsAccountData savingsAccountData = savingsAccountReadPlatformService.retrieveOne(loanFactorAccountId);
        BigDecimal savingsAccountBalance = savingsAccountData.getAccountBalance();

        // assemble all clients loan accounts and get their total balance
        Long clientId = client.getId();
        List<LoanAccountData> loanAccountDataList = new ArrayList<>();

        // if cross link get all loans and their balances etc
        loanAccountDataList = loanReadPlatformService.retrieveAllForClient(clientId);

        // If its not cross link then only specific loans belonging to specific product should be used
        if(!isCrossLink){
        
            Predicate<LoanAccountData> matchLoanProductFilter = (e)->{
                boolean isEqual = e.loanProductId().equals(loanProductId);
                return isEqual;
            };

            loanAccountDataList = loanAccountDataList.stream().filter(matchLoanProductFilter).collect(Collectors.toList());
        }


        //System.err.println("---------excludeLoansList size is "+excludeLoansList.size());

        Predicate<LoanAccountData> activeLoansFilter = (e)->{
            /**
             * Modified 29/08/2022
             */
            return e.isActive();
        };

        /**
         * Added 29/08/2022
         * For loans to be excluded from loan refactoring balance calculation
         */
        Predicate<LoanAccountData> isNotExluded = (e)->{
            return !isLoanExcluded(e.getId() ,excludeLoansList);
        };

        //System.err.println("-------loanAccountData size before filter isNotExcluded "+loanAccountDataList.size());

        loanAccountDataList = loanAccountDataList.stream().filter(isNotExluded).collect(Collectors.toList());

        //System.err.println("-------loanAccountData size after filter isNotExcluded "+loanAccountDataList.size());


        /// get loan balances instead

        BigDecimal accrualsBalance = BigDecimal.ZERO;

        Predicate<LoanTransactionData> accrualsPredicate = (e)->{
            LoanTransactionEnumData loanTransactionEnumData = e.getTransactionType();
            return loanTransactionEnumData.isAccrual();
        };

        // purpose is to consume loan accounts list and then get total of accrual balance
        Consumer<LoanAccountData> accrualsTransactionConsumer = (e)->{

            Long loanId = e.getId();
            Collection<LoanTransactionData> loanTransactionDataList = loanReadPlatformService.retrieveLoanTransactions(loanId);
            loanTransactionDataList.stream().filter(accrualsPredicate).map(LoanTransactionData::getAmount).reduce(accrualsBalance ,BigDecimal::add);
        };

        loanAccountDataList.stream().filter(activeLoansFilter).forEach(accrualsTransactionConsumer);

        //System.err.println("----------------------after calculating all accrual balances we get ------- "+accrualsBalance.doubleValue());

        BigDecimal totalDuePrincipalBalance = loanAccountDataList.stream().filter(activeLoansFilter).map(LoanAccountData::getPrincipalDue).reduce(BigDecimal.ZERO ,BigDecimal::add);

        //System.err.println("---------totalDuePrincipal is "+totalDuePrincipalBalance);

        BigDecimal totalInterestPaid = loanAccountDataList.stream().filter(activeLoansFilter).map(LoanAccountData::getInterestPaid).reduce(BigDecimal.ZERO ,BigDecimal::add);

        BigDecimal interestBalance = accrualsBalance.subtract(totalInterestPaid);

        //System.err.println("------------------interest balance is ------------"+interestBalance.doubleValue());

        BigDecimal totalDueBalance = totalDuePrincipalBalance.add(interestBalance);

        //System.err.println("---------------------------total due balance is -------------"+totalDueBalance.doubleValue());

        BigDecimal loanFactorTotalAllowable = savingsAccountBalance.multiply(new BigDecimal(loanFactor));

        //System.err.println("-------------------total allowable balance is "+loanFactorTotalAllowable+"--calculated with factor of "+loanFactor);

        int cmp = totalDueBalance.compareTo(loanFactorTotalAllowable);

        //System.err.println("--------------------------- total loan balances are "+totalDueBalance.doubleValue()+"--------cmp value is "+cmp);

        BigDecimal totalAllowable = BigDecimal.ZERO ;
        if(cmp >= 0){
            String message = String.format("Your requested loan amount is greater than the maximum possible for this Savings Account Linked Product .Maximum available is %.2f", totalAllowable.doubleValue());
            throw new LoanFactorException(message ,message ,"Standard Error");
        }

        /**
         * Spill Over is when the totolDueBalance for current active loans is then added to the new loan application principal
         * So to avoid clients who have to overpass their limit
         */

        BigDecimal spillOver = totalDueBalance.add(principal);

        // System.err.println("------------------------spillover amount here is "+spillOver.doubleValue());

        // if spillover is greater than total allowable then get balance

        //System.err.println("----------spill over is "+spillOver+" and loanfactor allowable is "+loanFactorTotalAllowable);

        cmp = spillOver.compareTo(loanFactorTotalAllowable);

        //System.err.println("-------------------------------if this equal then error was raised ? "+cmp);

        if(cmp > 0){

            totalAllowable = loanFactorTotalAllowable.subtract(totalDueBalance);
            String message = String.format("Your requested loan amount is greater than the maximum possible for this Savings Account Linked Product .Maximum available is %.2f", totalAllowable.doubleValue());
            throw new LoanFactorException(message ,message ,"Standard Error");
        }

        /// now check if added new principal do we get to max or
        /// if you have  4000 loan factor ,then apply for 3500 loan how do we get the balance
        /// here apply loan as usual

        totalDueBalance = totalDueBalance.add(principal);
        //System.err.println("-------------after adding princiapal we get "+totalDueBalance.doubleValue());
        ///savings account validity error here but its another job for another day now son
        return true ;

    }

    private boolean isLoanExcluded(Long loanId ,List excludeLoansList){

        Predicate isEqual = (e)->{
            //System.err.println("--------------------comparing "+loanId+"------------- and "+e+"--------------the result is "+e.equals(loanId));
            return e.equals(loanId);
        };

        boolean isPresent = excludeLoansList.stream().filter(isEqual).findFirst().isPresent();
        //System.err.println("-------------------------is excluded ? "+isPresent);
        return  isPresent;
    }

    public LoanProduct crossLinking(LoanProductRepository loanProductRepository){

        List<LoanProduct> loanProductDataCollection = loanProductRepository.findAll();

        //System.err.println("----------------we have load all load products now "+loanProductDataCollection.size());

        Predicate<LoanProduct> crossLinkPredicate = (e)->{
            return e.isCrossLink();
        };

        Comparator<LoanProduct> comparator = Comparator.comparing(LoanProduct::loanFactor);

        List<LoanProduct> loanProductDataList = loanProductDataCollection.stream().filter(crossLinkPredicate).sorted(comparator).collect(Collectors.toList());

        //System.err.println("--------------------we have loan factored this system "+loanProductDataList.size());

        if(!loanProductDataList.isEmpty()){
            /// we have selected by top one with highest loan factor
            this.crossLinkLoanProduct = loanProductDataList.get(0);
            return crossLinkLoanProduct;
        }

        return null ;

    }

    public Boolean hasCrossLink(){

        boolean isPresent = Optional.ofNullable(crossLinkLoanProduct).isPresent();
        return isPresent ;
    }


    public BigDecimal remainingAllowable(BigDecimal totalAllowable ,BigDecimal principal){
        int cmp = totalAllowable.compareTo(principal);
        return BigDecimal.ZERO;
    }

//
//    public SACCO_LOAN_STATUS verifyShareAccount(LoanProduct loanProduct){
//
//        this.loanFactor = loanProduct.loanFactor();
//
//        int shareAccountValidity = loanProduct.shareAccountValidity();
//
//        boolean isValidAccount = shareAccountValidity(shareAccountData ,shareAccountValidity);
//
//        if(!isValidAccount){
//            String message = "Your share account active duration doesnt meet the required minimum for this loan";
//            throw new ShareAccountValidityException(message,message,"Standard Error");
//        }
//
//        Double principalDouble = principal.doubleValue();
//
//        this.shareAccountSummaryData = shareAccountData.getShareAccountSummaryData();
//
//        Double currentSharePrice = shareAccountData.getCurrentMarketPrice().doubleValue();
//
//        this.totalShareValue = currentSharePrice * shareAccountSummaryData.getTotalApprovedShares() * loanFactor;
//
//        // now we have total share Value as an amount ,lets compare it to loan factor amount
//
//        Long clientId = shareAccountData.getClientId();
//
//        int availableShares = availableShares(loanReadPlatformService ,clientId);
//
//        System.err.println("----------------availableShares------------"+availableShares);
//
//        Double totalApprovedPrincipal = (availableShares * currentSharePrice) * loanFactor ;
//
//        System.err.println("------------------totalApprovedPrincipal-------------------"+totalApprovedPrincipal);
//
//        if(principalDouble > totalApprovedPrincipal){
//            String message = String.format("Your requested loan amount is greater than the maximum possible for this Share Account Linked Product .Maximum available is %.2f" ,totalApprovedPrincipal);
//            throw new LoanFactorException(message ,message ,"Standard Error");
//        }
//
//        ///savings account validity error here but its another job for another day now son
//        return SACCO_LOAN_STATUS.SUCCESS ;
//
//    }

//    public Integer availableShares(LoanReadPlatformService loanReadPlatformService ,Long clientId){
//
//        Collection<LoanAccountData> loanAccountDataList = loanReadPlatformService.retrieveAllForClient(clientId);
//
//        //however if not null then time to do some magic iterate over all loans and check which ones belong to a sacco loan product
//
//        for(LoanAccountData loanAccountData : loanAccountDataList){
//
//            if(loanAccountData.isActive()){
//
//                LoanProduct loanProductData =  loanProductRepository.findOne(loanAccountData.getLoanProductId());
//
//                if(loanProductData==null){
//                    continue ;
//                }
//
//                boolean isSaccoProduct = isSaccoProduct(loanProductData);
//                if(isSaccoProduct){
//
//                    SACCO_LOAN_LOCK saccoLoanLock = loanProductData.saccoLoanLock();
//
//                    lockedAmount(loanAccountData ,loanProductData,saccoLoanLock);
//
//                    System.err.println("-------------------lockedAmount----"+totalShareValue);
//
//                    if(totalShareValue < 0.0){
//                        System.err.println("------------------Break total loan share below 0 "+totalShareValue);
//                        break ;
//                    }
//                }
//            }
//        }
//
//        ///total sharevalue ;
//        return availableShares();
//    }
//
//    public boolean isSaccoProduct(LoanProduct loanproduct){
//        return loanproduct.isSaccoProduct();
//    }
//
//    public Integer availableShares(){
//
//        if(totalShareValue <= 0.0){
//            throw new InsufficientSharesForSaccoLoanException("" ,"" ,"");
//        }
//
//        Double value =  (totalShareValue /loanFactor);
//        return value.intValue();
//
//    }

//    public void lockedAmount(LoanAccountData loanAccountData ,LoanProduct loanProductData, SACCO_LOAN_LOCK saccoLoanLock){
//
//        Double currentShareValue = 0.0 ;
//        Double amountInShareValue = 0.0 ;
//        switch(saccoLoanLock){
//            case FULLLOCK :
//                amountInShareValue = loanAmount(loanAccountData ,true);
//                break;
//            case RELEASE_ON_REPAYMENT:
//                amountInShareValue = loanAmount(loanAccountData ,false);
//                break ;
//        }
//
//        System.err.println("----------------------amountInShareValue--------------------"+amountInShareValue);
//
//        currentShareValue = totalShareValue - amountInShareValue ;
//        totalShareValue = currentShareValue ;
//
//        System.err.println("------------------Total Share Value is-------"+totalShareValue);
//    }

//    public Double loanAmount(LoanAccountData loanAccountData ,boolean isFulllock){
//
//        LoanSummaryData loanSummaryData = loanAccountData.getLoanSummary();
//        ///share money locked lets say you have 600 in loans ,how many shares are that in todays money
//        if(isFulllock){
//            return loanSummaryData.getPrincipal().doubleValue() ;
//        }
//        return loanSummaryData.getPrincipalOutstanding().doubleValue() ;
//
//    }
//
//    public boolean shareAccountValidity(ShareAccountData shareAccountData ,int duration){
//
//        LocalDate submittedDate = shareAccountData.getSubmittedDate();
//        LocalDate dateNow = new LocalDate();
//        LocalDate cutOffDate = dateNow.minusMonths(duration);
//
//        if(submittedDate.toDate().getTime() > cutOffDate.toDate().getTime()){
//            ////account is not valid
//            return false ;
//        }
//
//        return true ;
//    }

}
