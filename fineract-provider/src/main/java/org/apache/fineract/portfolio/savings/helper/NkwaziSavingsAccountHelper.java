
package org.apache.fineract.portfolio.savings.helper ;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class NkwaziSavingsAccountHelper{


	private final SavingsAccountReadPlatformService savingsAccountReadPlatformService ;
	private final SavingsAccountAssembler savingsAccountAssembler ;
	private final SavingsAccountWritePlatformService savingsAccountWritePlatformService ;
	private final LoanReadPlatformService loanReadPlatformService ;
	private final FromJsonHelper fromJsonHelper ;


	@Autowired
	public NkwaziSavingsAccountHelper(SavingsAccountReadPlatformService savingsAccountReadPlatformService, SavingsAccountAssembler savingsAccountAssembler, SavingsAccountWritePlatformService savingsAccountWritePlatformService, LoanReadPlatformService loanReadPlatformService, FromJsonHelper fromJsonHelper) {
		this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
		this.savingsAccountAssembler = savingsAccountAssembler;
		this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
		this.loanReadPlatformService = loanReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
	}

	public Long withdraw(Long accountId , String payload , Boolean isEmployed){

		JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

		BigDecimal amount = jsonCommand.bigDecimalValueOfParameterNamed("transactionAmount");

		SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(accountId);

		// get client id from savings account ;
		Long clientId = savingsAccount.clientId();

		BigDecimal balance = savingsAccount.accountBalance();
		BigDecimal maxBalance = maxAllowableWithdrawal(clientId);

		System.err.println("--------------------------------amount is --------------"+amount);

		// this isnt the best of programming standards but okay nothing to do now
		//int cmp = 0 ;
		if(isEmployed){
			// do something withdraw 25% of that account balance ;
			// maximum balance should be 25%
			maxBalance = balance.multiply(new BigDecimal(0.25));
		}
		else{
			maxBalance = balance.multiply(new BigDecimal(0.5));
		}

		System.err.println("--------------------------max balance is --------------"+maxBalance);

		int cmp = maxBalance.compareTo(amount);

		System.err.println("-------------cmp is -----------"+cmp);

		if(cmp < 0){
			// balance is greater 
			return null ;
		}

		payload = JsonHelper.update(payload ,"transactionAmount" ,amount);

		jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

		CommandProcessingResult commandProcessingResult = savingsAccountWritePlatformService.withdrawal(accountId ,jsonCommand);

		Long transactionId = commandProcessingResult.resourceId();

		return transactionId ;
	}

	public BigDecimal maxAllowableWithdrawal(Long clientId){

		Collection<SavingsAccountData> savingsAccountDataList = savingsAccountReadPlatformService.retrieveForLookup(clientId ,false);
		List<LoanAccountData> loanAccountDataList =  loanReadPlatformService.retrieveAllForClient(clientId);

		BigDecimal totalSavings = savingsAccountDataList.stream().map(SavingsAccountData::getAccountBalance).reduce(BigDecimal.ZERO ,BigDecimal::add);
		BigDecimal totalLoans = loanAccountDataList.stream().filter(LoanAccountData::isActive).map(LoanAccountData::getTotalOutstandingAmount).reduce(BigDecimal.ZERO ,BigDecimal::add);

		return totalSavings.add(totalLoans);

	}

}