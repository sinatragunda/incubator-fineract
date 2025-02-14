/*

    Created by Sinatra Gunda
    At 5:10 AM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.helper;

import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountMonthlyDeposit;
import org.apache.fineract.portfolio.savings.enumerations.SAVINGS_TOTAL_CALC_CRITERIA;
import org.apache.fineract.portfolio.savings.repo.SavingsAccountMonthlyDepositRepository;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class SavingsProductPortfolioHelper {

    public static BigDecimal portfolioBalance(SavingsAccountReadPlatformService savingsAccountReadPlatformService ,Long productId){

        List<SavingsAccountData> savingsAccountDataList = savingsAccountReadPlatformService.retrieveAllForPortfolio(productId);

        BigDecimal totalBalance = BigDecimal.ZERO ;
        totalBalance = savingsAccountDataList.stream().map(SavingsAccountData::getAccountBalance).reduce(BigDecimal.ZERO ,BigDecimal::add);
        return totalBalance ;
    }


    public static BigDecimal portfolioBalanceDated(SavingsAccountReadPlatformService savingsAccountReadPlatformService,  SavingsAccountMonthlyDepositRepository savingsAccountMonthlyDepositRepository, Date startDate , Date endDate  , SAVINGS_TOTAL_CALC_CRITERIA calcCriteria , Long productId){

        List<SavingsAccountData> savingsAccountDataList = savingsAccountReadPlatformService.retrieveAllForPortfolio(productId);

        startDate.setDate(1);
        endDate.setDate(1);

        List<SavingsAccountMonthlyDeposit> savingsAccountMonthlyDepositList = savingsAccountMonthlyDepositRepository.findByDatesInBetween(startDate ,endDate);

        Predicate<SavingsAccountMonthlyDeposit> filterByProductId = (e)->{
            boolean sameProduct = accountBelongsToProductCatalog(savingsAccountDataList ,productId ,e.getSavingsAccountId());
            return sameProduct ;
        };

        BigDecimal totalBalance = BigDecimal.ZERO ;
        Function<SavingsAccountMonthlyDeposit ,BigDecimal> netDepositMapper = (e)->{
            BigDecimal netDeposit = e.getDeposit().subtract(e.getWithdraw());
            return netDeposit ;
        };

        switch (calcCriteria){
            case DEPOSITS:
                totalBalance = savingsAccountMonthlyDepositList.stream().filter(filterByProductId).map(SavingsAccountMonthlyDeposit::getDeposit).reduce(BigDecimal.ZERO ,BigDecimal::add);
                break;
            case NET_BALANCES:
                totalBalance = savingsAccountMonthlyDepositList.stream().filter(filterByProductId).map(netDepositMapper).reduce(BigDecimal.ZERO ,BigDecimal::add);
        }

        return totalBalance ;
    }

    public static boolean accountBelongsToProductCatalog(List<SavingsAccountData> savingsAccountDataList ,Long productId ,Long accountId){

        for(SavingsAccountData s : savingsAccountDataList){
            if(accountId.equals(s.getId())){
                return productId.equals(s.getSavingsProductId());
            }
        }
        return false;
    }

}
