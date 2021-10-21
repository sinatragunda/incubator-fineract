/*

    Created by Sinatra Gunda
    At 8:52 AM on 10/8/2021

*/
package org.apache.fineract.infrastructure.bulkimport.importhandler.equitygrowth;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.domain.EquityGrowthDividends;
import org.apache.fineract.portfolio.savings.domain.EquityGrowthOnSavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.helper.EquityGrowthHelper;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthDividendsRepository;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthOnSavingsAccountRepository;
import org.apache.fineract.wese.helper.ComparatorUtility;
import org.apache.fineract.wese.helper.TimeHelper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class EquityGrowthImportHandler {

    public static void postOpeningBalance(EquityGrowthDividendsRepository equityGrowthDividendsRepository, EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository, List<SavingsAccountTransactionData> savingsAccountTransactionDataList ,SavingsAccountAssembler savingsAccountAssembler , Long savingsProductId){

        int beneficiaries = savingsAccountTransactionDataList.size();
        Date startDate = TimeHelper.dateNow();
        Date endDate = TimeHelper.dateNow() ;

        // Long savingsProductId = savingsAccountTransactionDataList.get(0).getS

        // how do we get product id here now ,seems some tricky logic to do but hold on son
        // hard coding this value for now savingsproductid

        BigDecimal totalEquity  = savingsAccountTransactionDataList.stream().map(SavingsAccountTransactionData::getEquityBalance).reduce(BigDecimal.ZERO,BigDecimal::add);

        EquityGrowthDividends equityGrowthDividends = new EquityGrowthDividends(savingsProductId ,startDate ,endDate ,totalEquity ,beneficiaries);

        EquityGrowthDividends equityGrowthDividendsFlushed = equityGrowthDividendsRepository.saveAndFlush(equityGrowthDividends);

        for(SavingsAccountTransactionData savingsAccountTransactionData : savingsAccountTransactionDataList){

            BigDecimal equityBalance = savingsAccountTransactionData.getEquityBalance();

            boolean isZero = ComparatorUtility.isBigDecimalZero(equityBalance);
            if(isZero){
                continue;
            }

            Long savingsAccountId = savingsAccountTransactionData.getSavingsAccountId();

            Optional.ofNullable(savingsAccountId).ifPresent(e->{

                SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(e);
                MonetaryCurrency monetaryCurrency = savingsAccount.getCurrency();
                Money money = Money.of(monetaryCurrency ,equityBalance);

                Money totalEquityMoney = Money.of(monetaryCurrency ,totalEquity);

                String clientName = savingsAccount.getClient().getDisplayName();
                Double percentage = EquityGrowthHelper.percentage(money.getAmount().doubleValue() ,totalEquityMoney.getAmount().doubleValue());

                EquityGrowthOnSavingsAccount equityGrowthOnSavingsAccount = new EquityGrowthOnSavingsAccount(equityGrowthDividendsFlushed ,savingsAccountId ,BigDecimal.ZERO ,money.getAmount() ,percentage ,"Equity Migration" ,clientName);
                equityGrowthOnSavingsAccount.setEquityGrowthDividends(equityGrowthDividends);
                equityGrowthOnSavingsAccountRepository.save(equityGrowthOnSavingsAccount);

            });
        }
    }
}
