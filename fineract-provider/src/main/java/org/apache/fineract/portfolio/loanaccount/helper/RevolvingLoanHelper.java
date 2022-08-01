/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/* Change log 
    25/05/2021
        added allow multiple instances helper class 
*/
package org.apache.fineract.portfolio.loanaccount.helper;

import org.apache.fineract.portfolio.loanaccount.exception.RevolvingAccountInsufficientPayoffException;
import com.google.gson.JsonElement;
import java.math.BigDecimal ;
import org.apache.fineract.wese.helper.ObjectNodeHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.fineract.portfolio.loanaccount.domain.Loan ;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;

/// Added 31/05/2021
import java.util.ArrayList ;
import java.util.List ;

public class RevolvingLoanHelper{

	/// basically all it does it close an existing and pay of loan balance by using some disbursed amount from the new loan account 
	
	public static void validateApplication(List<Loan> revolveLoanAccounts ,Loan newLoanApplication){

		BigDecimal balance = revolveLoanAccounts.stream().map(e -> e.getTotalOutstanding()).reduce(BigDecimal.ZERO ,BigDecimal::add);
		BigDecimal principal = newLoanApplication.getProposedPrincipal();

		int cmp = balance.compareTo(principal);
		//if greater than 1 it means throw exception
		if(cmp >= 0){
			BigDecimal loanBalance = balance.subtract(principal);
			throw new RevolvingAccountInsufficientPayoffException(loanBalance);
		} 
	}

	public static void revolvingLoansBalanceCheck(List<Loan> list ,SavingsAccount savingsAccount){

		BigDecimal sum = list.stream().map(e -> e.getTotalOutstanding()).reduce(BigDecimal.ZERO ,BigDecimal::add);
		
		System.err.println("------------balance sum is --------------"+sum);

		BigDecimal savingsAccountBalance = savingsAccount.accountBalance();
		int cmp = sum.compareTo(savingsAccountBalance);
		//if greater than 1 it means throw exception
		if(cmp >= 0){
			BigDecimal loanBalance = sum.subtract(savingsAccountBalance);
			throw new RevolvingAccountInsufficientPayoffException(loanBalance);
		}		
	}

	public static BigDecimal revolvingLoanDTOAmount(Loan revolveLoanAccount ,SavingsAccount fromSavingsAccount){
		
		BigDecimal revolveLoanBalance = revolveLoanAccount.getTotalOutstanding();
        BigDecimal savingsAccountBalance = fromSavingsAccount.accountBalance();

        LoanProduct loanProduct = revolveLoanAccount.loanProduct();

        int cmp = revolveLoanBalance.compareTo(savingsAccountBalance);

        // revolve amount greater than savings account balance
        if(cmp > 0){

        	if(fromSavingsAccount.allowOverdraft()){
        		return revolveLoanBalance;
        	}

        	///check if it allows partial payments as well 
        	
        	if(loanProduct.isSettlementPartialPayment()){
        		return savingsAccountBalance;
        	}

        	BigDecimal dueBalance = revolveLoanBalance.subtract(savingsAccountBalance);
        	throw new RevolvingAccountInsufficientPayoffException(dueBalance);

        }

        System.err.println("-------------------------revovleLoanBalancr ------------"+revolveLoanBalance);

        return revolveLoanBalance ;

	}	
	public static void closeLoan(Long loanId ,boolean payOffBalance){

		ObjectNode objectNode = ObjectNodeHelper.objectNode();
		//objectNode.put("locale","en") ;
		//objectNode.put("dateFormat","dd MMMM yyyy");
	
	}

	public static void payOffBalance(Long loanId ,BigDecimal availableFunds ,boolean checkException){

		if(checkException){
			/// throw error 
			throw new RevolvingAccountInsufficientPayoffException(availableFunds);
		
		}

	}   


}