package org.apache.fineract.portfolio.savings.helper; 

import java.util.Date ;
import java.util.List ;
import java.util.Instant;
import java.function.Predictate;
import org.apache.fineract.wese.helper.TimeHelper ;
import org.apache.fineract.savings.domain.SavingsAccount;
import org.apache.fineract.savings.domain.SavingsAccountTransaction;

public class SavingsMonthlyDepositHelper{

	//// monthly duration thing here 
	//// if month is 2021-07-01 - 
	public static BigDecimal currentMonthDeposit(SavingsAccount savingsAccount){

		Instant instant = Instant.now();
		Date now = Date.from(instant);
		List<SavingsAccountTransaction> transactionsList = savingsAccount.getTransactions();
		
		Predictate<SavingsAccountTransaction> filterByMonth = (e)->{
			SavingsAccountTransaction transaction = e ;
			Date transactionDate = e.getTransactionDate();
			boolean isSameMonth = TimeHelper.sameMonth(transactionDate ,now);
			return isSameMonth ;
		}

		List<SavingsAccountTransaction> filteredList = transactionsList.stream().filter(filterByMonth).collect(Collectors.toList());
		BigDecimal monthlyDeposit = filteredList.map(SavingsAccountTransaction::amount).reduce(BigDecimal.ZERO ,BigDecimal::add);
		return monthlyDeposit ;
	}


	
}