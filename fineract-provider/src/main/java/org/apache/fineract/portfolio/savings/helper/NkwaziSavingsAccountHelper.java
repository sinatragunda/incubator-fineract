
package org.apache.fineract.portfolio.savings.helper ;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;

import java.math.BigDecimal;

public class NkwaziSavingsAccountHelper{

	public static Long withdraw(SavingsAccountWritePlatformService savingsAccountWritePlatformService, SavingsAccountAssembler savingsAccountAssembler , FromJsonHelper fromJsonHelper , Long accountId , String payload ,Boolean isEmployed){

		JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

		BigDecimal amount = jsonCommand.bigDecimalValueOfParameterNamed("transactionAmount");

		SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(accountId);

		BigDecimal balance = savingsAccount.accountBalance();
		BigDecimal maxBalance = BigDecimal.ZERO ;


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

}