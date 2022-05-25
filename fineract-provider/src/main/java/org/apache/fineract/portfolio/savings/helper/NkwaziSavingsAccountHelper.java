
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

	public static Long withdraw(SavingsAccountWritePlatformService savingsAccountWritePlatformService, SavingsAccountAssembler savingsAccountAssembler , FromJsonHelper fromJsonHelper , Long accountId , String payload){

		JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

		BigDecimal amount = jsonCommand.bigDecimalValueOfParameterNamed("transactionAmount");
		Boolean isEmployed = jsonCommand.booleanPrimitiveValueOfParameterNamed("isEmployed");

		SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(accountId);

		BigDecimal balance = savingsAccount.accountBalance();

		// this isnt the best of programming standards but okay nothing to do now
		if(isEmployed){
			// do something withdraw 25% of that account balance ;
			balance = balance.multiply(new BigDecimal(0.25));
		}
		else{
			balance = balance.multiply(new BigDecimal(0.5));
		}

		payload = JsonHelper.update(payload ,"transactionAmount" ,balance);

		jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

		CommandProcessingResult commandProcessingResult = savingsAccountWritePlatformService.withdrawal(accountId ,jsonCommand);
		Long transactionId = commandProcessingResult.resourceId();
		return transactionId ;

	}

}