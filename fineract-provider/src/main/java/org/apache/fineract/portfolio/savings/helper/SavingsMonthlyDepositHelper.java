package org.apache.fineract.portfolio.savings.helper; 

import java.util.Date ;
import java.util.List ;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.function.Predicate;
import org.apache.fineract.wese.helper.TimeHelper ;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.repo.SavingsAccountMonthlyDepositRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountMonthlyDeposit;


// Added 28/12/2021
import org.joda.time.LocalDate;


public class SavingsMonthlyDepositHelper{

	//// monthly duration thing here 
	//// if month is 2021-07-01

	public static BigDecimal currentMonthTransactions(List<SavingsAccountMonthlyDeposit> transactionsList ,boolean isDeposit){

		Date now = TimeHelper.dateNow();
		Predicate<SavingsAccountMonthlyDeposit> filterByMonth = (e)->{
			SavingsAccountMonthlyDeposit transaction = e ;
			Date startDate = e.getStartDate();
			boolean isSameMonth = TimeHelper.sameMonth(startDate ,now);
			return isSameMonth ;
		};

		BigDecimal amount = BigDecimal.ZERO ;
		List<SavingsAccountMonthlyDeposit> filteredList = transactionsList.stream().filter(filterByMonth).collect(Collectors.toList());

		if(isDeposit) {
			amount = filteredList.stream().map(e -> e.getDeposit()).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
		else {
			amount = filteredList.stream().map(e-> e.getWithdraw()).reduce(BigDecimal.ZERO ,BigDecimal::add);
		}
		return amount ;
	}

	public static void handleDepositOrWithdraw(SavingsAccountMonthlyDepositRepository repository ,SavingsAccount savingsAccount ,BigDecimal amount ,LocalDate transactionDate ,boolean isDeposit){

		Long id = savingsAccount.getId();

		// modified 28/12/2021
		Date startDate = TimeHelper.startDate(transactionDate.getTime());

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
				BigDecimal amountUpdate = BigDecimal.ZERO;
				if(!isDeposit){

					amountUpdate = amount.add(savingsAccountMonthlyDeposit.getWithdraw());
					savingsAccountMonthlyDeposit.setWithdraw(amountUpdate);
					repository.save(savingsAccountMonthlyDeposit);
					return ;
				}

				amountUpdate = amount.add(savingsAccountMonthlyDeposit.getDeposit());
				savingsAccountMonthlyDeposit.setDeposit(amountUpdate);
				repository.save(savingsAccountMonthlyDeposit);
				return ; 

			}
		}
		// create new value here
		savingsAccountMonthlyDeposit = new SavingsAccountMonthlyDeposit(id ,startDate ,amount ,isDeposit);
		repository.save(savingsAccountMonthlyDeposit);
	}	
}