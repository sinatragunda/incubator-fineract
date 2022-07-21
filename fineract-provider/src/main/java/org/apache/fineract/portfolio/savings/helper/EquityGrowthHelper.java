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
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountServiceWrapper;
import org.apache.fineract.wese.helper.ComparatorUtility;
import org.apache.fineract.wese.helper.TimeHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import static java.util.stream.Collectors.toList;


public class EquityGrowthHelper {

    private EquityGrowthDividends equityGrowthDividends ;
    private Boolean includeZeroBeneficiaries ;

    public EquityGrowthHelper(boolean arg){
        this.includeZeroBeneficiaries = arg;
    }

    private Predicate<EquityGrowthOnSavingsAccount> zeroBeneficiariesPredicate = (e)->{

        if(includeZeroBeneficiaries){
            return true ;
        }
        // if its true then will pass so invert the boolean
        return !ComparatorUtility.isBigDecimalZero(e.getAmount());

    };

    public void transferEarnings(SavingsAccountServiceWrapper savingsAccountServiceWrapper, List<EquityGrowthOnSavingsAccount> list , Long transferSourceAccountId){

        SavingsAccountAssembler savingsAccountAssembler = savingsAccountServiceWrapper.savingsAccountAssember();
        SavingsAccountReadPlatformService savingsAccountReadPlatformService = savingsAccountServiceWrapper.savingsAccountReadPlatformService();
        EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository = savingsAccountServiceWrapper.equityGrowthOnSavingsAccountRepository();
        SavingsAccountDomainService savingsAccountDomainService = savingsAccountServiceWrapper.savingsAccountDomainService();

        Consumer<EquityGrowthOnSavingsAccount> consumer = (e)->{
            Long accountId = e.getSavingsAccountId();
            BigDecimal amount = e.getAmount();

            SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(accountId);

            // new added functionality to enable to select destination account other than source

            if(savingsAccount.productId() != transferSourceAccountId){
                Long clientId = savingsAccount.clientId();
                SavingsAccountData tranferAccount = savingsAccountReadPlatformService.retrieveAllForClientUnderPortfolio(clientId,transferSourceAccountId).stream().findFirst().orElse(null);
                boolean isPresent = Optional.ofNullable(tranferAccount).isPresent();
                if(isPresent){
                    accountId = tranferAccount.getId();
                    savingsAccount = savingsAccountAssembler.assembleFrom(accountId);
                }
            }

            savingsAccountDomainService.handleDepositLiteEx1(savingsAccount.getId() ,new LocalDate(),amount,"Equity Dividends Sharing");

            // update equity growth source account here
            e.setTransferAccount(savingsAccount);
            equityGrowthOnSavingsAccountRepository.save(e);

        };
        // further stream it then use for each now ,great plan
        list.stream().filter(zeroBeneficiariesPredicate).collect(toList()).stream().forEach(consumer);

    }


    public void flushToDatabase(EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository , EquityGrowthDividends equityGrowthDividends , List<EquityGrowthOnSavingsAccount> equityGrowthOnSavingsAccountList){

        Consumer<EquityGrowthOnSavingsAccount> consumer = (e)->{
            e.setEquityGrowthDividends(equityGrowthDividends);
            equityGrowthOnSavingsAccountRepository.save(e);
        };

        equityGrowthOnSavingsAccountList.stream().filter(zeroBeneficiariesPredicate).forEach(consumer);
    }

    public List<EquityGrowthOnSavingsAccount> calculateEquity(SavingsAccountMonthlyDepositRepository monthlyDepositRepository, List<SavingsAccountData> savingsAccountDataList,SAVINGS_TOTAL_CALC_CRITERIA savingsTotalCalcCriteria , Long savingsProductId , Date startDate ,Date endDate ,BigDecimal totalSavings, BigDecimal profit){

        /// calculate equity ,filter by period 
        /// fix number of beneficiaries error here ,where number is all clients yet 0 beneficiaries not included 

        List<EquityGrowthOnSavingsAccount> equityGrowthOnSavingsAccountList = new ArrayList<>();

        for(SavingsAccountData savingsAccountData : savingsAccountDataList){

            Long savingsAccountId = savingsAccountData.getId();

            List<SavingsAccountMonthlyDeposit> savingsAccountMonthlyDepositList = monthlyDepositRepository.findBySavingsAccountId(savingsAccountId);

            BigDecimal averageSavings = calculateSavingsAccountAverage(savingsAccountMonthlyDepositList ,savingsTotalCalcCriteria , startDate ,endDate);
            BigDecimal profitPerClient =  profitPerClient(savingsAccountMonthlyDepositList ,startDate ,endDate ,totalSavings, profit ,averageSavings);
            Double percentage = percentage(profitPerClient.doubleValue() ,totalSavings.doubleValue());

            EquityGrowthOnSavingsAccount equityGrowthOnSavingsAccount = new EquityGrowthOnSavingsAccount(equityGrowthDividends ,savingsAccountId ,averageSavings, profitPerClient ,percentage ,"Growth Test",savingsAccountData.getClientName());
            equityGrowthOnSavingsAccountList.add(equityGrowthOnSavingsAccount);
        }

        Long beneficiaries = equityGrowthOnSavingsAccountList.stream().filter(zeroBeneficiariesPredicate).count();

        equityGrowthDividends = new EquityGrowthDividends(savingsProductId, startDate ,endDate ,profit ,beneficiaries.intValue(),null);

        return equityGrowthOnSavingsAccountList.stream().filter(zeroBeneficiariesPredicate).collect(toList());
    
    }

    public BigDecimal profitPerClient(List<SavingsAccountMonthlyDeposit> savingsAccountMonthlyDepositList ,Date startDate ,Date endDate ,BigDecimal totalSavings, BigDecimal profit ,BigDecimal averageSavings){
        
        BigDecimal clientProfit = BigDecimal.ZERO;
        
        try{

            BigDecimal multiplier = averageSavings.divide(totalSavings , MathContext.DECIMAL128);
            clientProfit = multiplier.multiply(profit);
            System.err.println("------system profits "+profit+"--===========multiplier "+multiplier+"=======at start total portfolio savings-----"+totalSavings+"-----------------total----client profit is --------------"+clientProfit+"---------------based on savings "+averageSavings);
            
        }
        catch(ArithmeticException e){
            System.err.println("-------------excpetion caught for value  "+e.getMessage());
        }

        return clientProfit.setScale(0 ,BigDecimal.ROUND_FLOOR);
    
    }

    public BigDecimal calculateSavingsAccountAverage(List<SavingsAccountMonthlyDeposit> savingsMonthlyDepositList , SAVINGS_TOTAL_CALC_CRITERIA savingsTotalCalcCriteria, Date startDate , Date endDate){

        int periodCount = TimeHelper.periodDuration(startDate ,endDate);
        BigDecimal total = BigDecimal.ZERO ;

        Function<SavingsAccountMonthlyDeposit ,BigDecimal> netDeposit = (e)->{
            BigDecimal net = e.getDeposit().subtract(e.getWithdraw());
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

    public static Double percentage(Double value ,Double of){

        /// (value / of ) * 100
        Double percentage = (value / of) * 100 ;
        Double floated = Double.valueOf(String.format("%.2f" ,percentage));
        return floated ;
    }

    public EquityGrowthDividends getEquityGrowthDividends(){
        return equityGrowthDividends ;
    }


}
