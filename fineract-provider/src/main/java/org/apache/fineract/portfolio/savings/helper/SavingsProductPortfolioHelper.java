/*

    Created by Sinatra Gunda
    At 5:10 AM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.helper;

import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;

import java.math.BigDecimal;
import java.util.List;

public class SavingsProductPortfolioHelper {

    public static BigDecimal portfolioBalance(SavingsAccountReadPlatformService savingsAccountReadPlatformService ,Long productId){

        List<SavingsAccountData> savingsAccountDataList = savingsAccountReadPlatformService.retrieveAllForPortfolio(productId);

        System.err.println("----------------total balance for these number of products ------------"+savingsAccountDataList.size());

        BigDecimal totalBalance = BigDecimal.ZERO ;

        totalBalance = savingsAccountDataList.stream().map(SavingsAccountData::getAccountBalance).reduce(BigDecimal.ZERO ,BigDecimal::add);
        System.err.println("-------------------------total account balances is -------------------------"+totalBalance);

        return totalBalance ;
    }

}
