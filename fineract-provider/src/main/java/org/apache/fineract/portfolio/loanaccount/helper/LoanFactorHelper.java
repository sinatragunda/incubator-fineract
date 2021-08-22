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

import java.util.ArrayList;
import java.math.BigDecimal ;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanproduct.enumerations.LOAN_FACTOR_SOURCE_ACCOUNT_TYPE;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountSummary;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountData ;
import org.apache.fineract.portfolio.accountdetails.data.ShareAccountSummaryData;
import  org.apache.fineract.portfolio.loanaccount.data.LoanAccountData ;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;

////added  10/27/2020
import org.apache.fineract.portfolio.loanaccount.exception.LoanFactorException ;

/// added 30/10/2020
import java.util.List;


public class LoanFactorHelper{

    private ShareAccountSummaryData shareAccountSummaryData ;
    private ShareAccountData shareAccountData ;
    private SavingsAccountData savingsAccountData;
    private SavingsAccountSummary savingsAccountSummary;
    private LoanReadPlatformService loanReadPlatformService ;
    private BigDecimal principal ;
    private Double totalShareValue = null ;
    private LoanProductRepository loanProductRepository;
    private int loanFactor ;
    private LOAN_FACTOR_SOURCE_ACCOUNT_TYPE loanFactorSourceAccount = null ;


    public LoanFactorHelper(ShareAccountData shareAccountData ,LoanReadPlatformService loanReadPlatformService ,LoanProductRepository loanProductRepository, BigDecimal principal){
        this.shareAccountData = shareAccountData ;
        this.loanReadPlatformService = loanReadPlatformService ;
        this.principal = principal ;
        this.loanProductRepository = loanProductRepository;
    }

    public LoanFactorHelper(){}

    public LoanFactorHelper(LOAN_FACTOR_SOURCE_ACCOUNT_TYPE loanFactorSourceAccountType){
        this.loanFactorSourceAccount = loanFactorSourceAccountType;
    }

    public boolean transact(SavingsAccountReadPlatformService savingsAccountReadPlatformService ,LoanReadPlatformService loanReadPlatformService ,LoanProduct loanProduct ,Client client ,Long loanFactorAccountId ,BigDecimal principal){

        switch (loanFactorSourceAccount){
            case SAVINGS:
                return savingsAccountBasedLoanFactoring(savingsAccountReadPlatformService ,loanReadPlatformService ,loanProduct ,client ,loanFactorAccountId ,principal);

        }
        return false ;
    }


    public boolean savingsAccountBasedLoanFactoring(SavingsAccountReadPlatformService savingsAccountReadPlatformService, LoanReadPlatformService loanReadPlatformService , LoanProduct loanProduct , Client client ,Long loanFactorAccountId , BigDecimal principal){

        this.loanFactor = loanProduct.loanFactor();

        System.err.println("-------------------loan factor to use for all this nonsense is "+this.loanFactor);

        if(this.loanFactor <=0){
            return true ;
        }

        Long loanProductId = loanProduct.getId();

        SavingsAccountData savingsAccountData = savingsAccountReadPlatformService.retrieveOne(loanFactorAccountId);
        BigDecimal savingsAccountBalance = savingsAccountData.getAccountBalance();


        /// assemble all clients savings account and get their total balance
        Long clientId = client.getId();
        List<LoanAccountData> loanAccountDataList = new ArrayList<>();
        boolean isCrossLink = loanProduct.isCrossLink();

        //if cross link get all loans and their balances etc
        if(isCrossLink){
            loanAccountDataList = loanReadPlatformService.retrieveAllForClient(clientId);
        }
        else{
            // retrieve all where loan product id is equal to something
            loanAccountDataList = loanReadPlatformService.retrieveAllForLoanProduct(loanProductId);
        }

        System.err.println("--------------------------- all accounts for client up for execution ------"+loanAccountDataList.size());


        loanAccountDataList.stream().forEach((e)->{
            System.err.println("------loan id steam for "+e.getAccountNo()+"--------- and some total balance of "+e.getTotalOutstandingAmount().doubleValue());
        });

        BigDecimal totalDueBalance = loanAccountDataList.stream().map(LoanAccountData::getTotalOutstandingAmount).reduce(BigDecimal.ZERO ,BigDecimal::add);
        //BigDecimal totalPrincipal = loanAccountDataList.stream().map(LoanAccountData::getPrincipal).reduce(BigDecimal.ZERO ,BigDecimal::add);

        BigDecimal loanFactorTotalAllowable = savingsAccountBalance.multiply(new BigDecimal(loanFactor));

        System.err.println("-------------------total allowable balance is "+loanFactorTotalAllowable.doubleValue());

        int cmp = totalDueBalance.compareTo(loanFactorTotalAllowable);

        System.err.println("--------------------------- total loan balances are "+totalDueBalance.doubleValue()+"--------cmp value is "+cmp);

        if(cmp >= 0){

            System.err.println("--------------------------throw an error here son ---------------");
            BigDecimal totalAllowable = totalDueBalance.subtract(loanFactorTotalAllowable);
            String message = String.format("Your requested loan amount is greater than the maximum possible for this Savings Account Linked Product .Maximum available is %.2f", totalAllowable.doubleValue());
            throw new LoanFactorException(message ,message ,"Standard Error");
        }

        /// now check if added new principal do we get to max or
        /// if you have  4000 loan factor ,then apply for 3500 loan how do we get the balance

        totalDueBalance = totalDueBalance.add(principal);
        System.err.println("-------------after adding princiapal we get "+totalDueBalance.doubleValue());
        ///savings account validity error here but its another job for another day now son
        return true ;

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
