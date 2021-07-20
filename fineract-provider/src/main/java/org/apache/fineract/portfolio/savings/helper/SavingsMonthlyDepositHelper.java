package org.apache.fineract.portfolio.savings.helper; 

import java.util.Date ;
import java.time.LocalDate;
import java.util.List ;
import java.util.Collection;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.ZoneId;
import java.math.BigDecimal;
import java.util.function.Predicate;
import org.apache.fineract.wese.helper.TimeHelper ;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData ;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountMonthlyDepositRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountMonthlyDeposit;

public class SavingsMonthlyDepositHelper{

	//// monthly duration thing here 
	//// if month is 2021-07-01
	public static BigDecimal currentMonthDeposit(SavingsAccount savingsAccount){

		Date now = TimeHelper.dateNow();
		List<SavingsAccountTransaction> transactionsList = savingsAccount.getTransactions();
		
		Predicate<SavingsAccountTransaction> filterByMonth = (e)->{
			SavingsAccountTransaction transaction = e ;
			Date transactionDate = e.getTransactionDate();
			boolean isSameMonth = TimeHelper.sameMonth(transactionDate ,now);
			return isSameMonth ;
		};

		List<SavingsAccountTransaction> filteredList = transactionsList.stream().filter(filterByMonth).collect(Collectors.toList());
		BigDecimal monthlyDeposit = filteredList.stream().map(SavingsAccountTransaction::getAmount).reduce(BigDecimal.ZERO ,BigDecimal::add);
		return monthlyDeposit ;
	}

	public static BigDecimal currentMonthDeposit(Collection<SavingsAccountTransactionData> transactionsList){

		Date now = TimeHelper.dateNow();
		
		Predicate<SavingsAccountTransactionData> filterByMonth = (e)->{
			SavingsAccountTransactionData transaction = e ;
			Date transactionDate = e.getTransactionDate().toDate();
			boolean isSameMonth = TimeHelper.sameMonth(transactionDate ,now);
			return isSameMonth ;
		};

		List<SavingsAccountTransactionData> filteredList = transactionsList.stream().filter(filterByMonth).collect(Collectors.toList());
		BigDecimal monthlyDeposit = filteredList.stream().map(e -> e.getAmount()).reduce(BigDecimal.ZERO ,BigDecimal::add);
		return monthlyDeposit ;
	}


	public static BigDecimal currentMonthDeposit(List<SavingsAccountMonthlyDeposit> transactionsList){

		Date now = TimeHelper.dateNow();
		
		Predicate<SavingsAccountMonthlyDeposit> filterByMonth = (e)->{
			SavingsAccountMonthlyDeposit transaction = e ;
			Date startDate = e.getStartDate();
			boolean isSameMonth = TimeHelper.sameMonth(startDate ,now);
			return isSameMonth ;
		};

		List<SavingsAccountMonthlyDeposit> filteredList = transactionsList.stream().filter(filterByMonth).collect(Collectors.toList());
		BigDecimal monthlyDeposit = filteredList.stream().map(e -> e.getAmount()).reduce(BigDecimal.ZERO ,BigDecimal::add);
		return monthlyDeposit ;
	}

	public static void handleDeposit(SavingsAccountMonthlyDepositRepository repository ,SavingsAccount savingsAccount ,BigDecimal amount){

		Long id = savingsAccount.getId();
		Date startDate = TimeHelper.startDate();

		SavingsAccountMonthlyDeposit savingsAccountMonthlyDeposit = null;
		List<SavingsAccountMonthlyDeposit> savingsAccountMonthlyDepositList = repository.findBySavingsAccountId(id);

		if(!savingsAccountMonthlyDepositList.isEmpty()){

			Predicate<SavingsAccountMonthlyDeposit> filterByMonth = (e)->{
				SavingsAccountMonthlyDeposit transaction = e ;
				Date transactionDate = e.getStartDate();
				boolean isSameMonth = TimeHelper.sameMonth(startDate ,transactionDate);
				return isSameMonth ;
			};

			List<SavingsAccountMonthlyDeposit> newList = savingsAccountMonthlyDepositList.stream().filter(filterByMonth).collect(Collectors.toList());
			
			if(!newList.isEmpty()){
				savingsAccountMonthlyDeposit = newList.get(0);
				BigDecimal amountUpdate = amount.add(savingsAccountMonthlyDeposit.getAmount());
				savingsAccountMonthlyDeposit.setAmount(amountUpdate);
				repository.save(savingsAccountMonthlyDeposit);
				return ; 
			}
		}
		// create new value here
		savingsAccountMonthlyDeposit = new SavingsAccountMonthlyDeposit(id ,startDate ,amount);
		repository.save(savingsAccountMonthlyDeposit);
	}	
}