/*

    Created by Sinatra Gunda
    At 6:44 AM on 7/22/2021

*/
package org.apache.fineract.portfolio.savings.helper;

import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.*;
import org.apache.fineract.portfolio.savings.enumerations.SAVINGS_TOTAL_CALC_CRITERIA;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthDividendsRepository;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthOnSavingsAccountRepository;
import org.apache.fineract.portfolio.savings.repo.SavingsAccountMonthlyDepositRepository;
import org.apache.fineract.wese.helper.TimeHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;


public class EquityGrowthHelper {

    private EquityGrowthDividends equityGrowthDividends ;

    public void transferEarnings(SavingsAccountDomainService savingsAccountDomainService ,List<EquityGrowthOnSavingsAccount> equityGrowthOnSavingsAccountList){
        for(EquityGrowthOnSavingsAccount equityGrowthOnSavingsAccount : equityGrowthOnSavingsAccountList){

            Long id = equityGrowthOnSavingsAccount.getSavingsAccountId();
            BigDecimal amount = equityGrowthOnSavingsAccount.getAmount();
            //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            savingsAccountDomainService.handleDepositLite(id ,new LocalDate(),amount);
        }
    }

    public void flushToDatabase(EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository , EquityGrowthDividends equityGrowthDividends , List<EquityGrowthOnSavingsAccount> equityGrowthOnSavingsAccountList){

        Consumer<EquityGrowthOnSavingsAccount> consumer = (e)->{
            e.setEquityGrowthDividends(equityGrowthDividends);
            equityGrowthOnSavingsAccountRepository.save(e);
        };
        equityGrowthOnSavingsAccountList.stream().forEach(consumer);
    }

    public List<EquityGrowthOnSavingsAccount> calculateEquity(SavingsAccountMonthlyDepositRepository monthlyDepositRepository, List<SavingsAccountData> savingsAccountDataList,SAVINGS_TOTAL_CALC_CRITERIA savingsTotalCalcCriteria , Long savingsProductId , Date startDate ,Date endDate ,BigDecimal totalSavings, BigDecimal profit){

        /// calculate equity ,filter by period
        int beneficiaries = savingsAccountDataList.size();
        equityGrowthDividends = new EquityGrowthDividends(savingsProductId, startDate ,endDate ,profit ,beneficiaries);

        List<EquityGrowthOnSavingsAccount> equityGrowthOnSavingsAccountList = new ArrayList<>();

        for(SavingsAccountData savingsAccountData : savingsAccountDataList){

            Long savingsAccountId = savingsAccountData.getId();

            List<SavingsAccountMonthlyDeposit> savingsAccountMonthlyDepositList = monthlyDepositRepository.findBySavingsAccountId(savingsAccountId);

            BigDecimal averageSavings = calculateSavingsAccountAverage(savingsAccountMonthlyDepositList ,savingsTotalCalcCriteria , startDate ,endDate);
            BigDecimal profitPerClient =  profitPerClient(savingsAccountMonthlyDepositList ,startDate ,endDate ,totalSavings, profit ,averageSavings);
            Double percentage = percentage(profitPerClient.doubleValue() ,totalSavings.doubleValue());

            System.err.println("--------------------profit for------"+savingsAccountData.getClientName()+"---------------------is "+profitPerClient.doubleValue());

            EquityGrowthOnSavingsAccount equityGrowthOnSavingsAccount = new EquityGrowthOnSavingsAccount(equityGrowthDividends ,savingsAccountId ,averageSavings, profitPerClient ,percentage ,"Growth Test",savingsAccountData.getClientName());

            equityGrowthOnSavingsAccountList.add(equityGrowthOnSavingsAccount);
        }

        return equityGrowthOnSavingsAccountList;

    }

    public BigDecimal profitPerClient(List<SavingsAccountMonthlyDeposit> savingsAccountMonthlyDepositList ,Date startDate ,Date endDate ,BigDecimal totalSavings, BigDecimal profit ,BigDecimal averageSavings){

        System.err.println("   average savings is         "+averageSavings.doubleValue());

        BigDecimal multiplier = averageSavings.divide(totalSavings ,3, BigDecimal.ROUND_HALF_UP);

        System.err.println("----------------multiplier is--------"+multiplier.doubleValue());

        BigDecimal clientProfit = multiplier.multiply(profit);

        System.err.println("----------------client profit is --------"+clientProfit.doubleValue());

        clientProfit.setScale(2);
        return clientProfit;
    }

    public BigDecimal calculateSavingsAccountAverage(List<SavingsAccountMonthlyDeposit> savingsMonthlyDepositList , SAVINGS_TOTAL_CALC_CRITERIA savingsTotalCalcCriteria, Date startDate , Date endDate){

        int periodCount = TimeHelper.periodDuration(startDate ,endDate);
        BigDecimal total = BigDecimal.ZERO ;

        Function<SavingsAccountMonthlyDeposit ,BigDecimal> netDeposit = (e)->{
            BigDecimal net = e.getDeposit().subtract(e.getWithdraw());
            System.err.println("----------net balance is=--------"+net.doubleValue());
            return net;
        };


        switch (savingsTotalCalcCriteria){
            case DEPOSITS:
                total = savingsMonthlyDepositList.stream().map(SavingsAccountMonthlyDeposit::getDeposit).reduce(BigDecimal.ZERO ,BigDecimal::add);
                break;
            case NET_BALANCES:
                total = savingsMonthlyDepositList.stream().map(netDeposit).reduce(BigDecimal.ZERO ,BigDecimal::add);

        }

        return total.divide(new BigDecimal(periodCount) ,2 ,BigDecimal.ROUND_HALF_UP);
    }

    public Double percentage(Double value ,Double of){

        /// (value / of ) * 100
        Double percentage = (value / of) * 100 ;
        Double floated = Double.valueOf(String.format("%.2f" ,percentage));
        return floated ;
    }

    public EquityGrowthDividends getEquityGrowthDividends(){
        return equityGrowthDividends ;
    }


}
