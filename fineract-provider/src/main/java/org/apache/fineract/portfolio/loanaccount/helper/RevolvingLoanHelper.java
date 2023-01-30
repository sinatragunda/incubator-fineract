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

import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.account.domain.AccountTransferTransaction;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.exception.RevolvingAccountInsufficientPayoffException;
import com.google.gson.JsonElement;
import java.math.BigDecimal ;

import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.wese.helper.ObjectNodeHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.fineract.portfolio.loanaccount.domain.Loan ;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

/// Added 31/05/2021
import java.util.ArrayList ;
import java.util.List ;
import java.util.Locale;
import java.util.StringTokenizer;

public class RevolvingLoanHelper{

	/// basically all it does it close an existing and pay of loan balance by using some disbursed amount from the new loan account 
	
	public static void validateApplication(List<LoanTransactionData> revolveLoanAccounts ,Loan newLoanApplication){

		BigDecimal balance = revolveLoanAccounts.stream().map(e -> e.outstandingLoanBalance()).reduce(BigDecimal.ZERO ,BigDecimal::add);
		BigDecimal principal = newLoanApplication.getProposedPrincipal();

		int cmp = balance.compareTo(principal);
		//if greater than 1 it means throw exception
		if(cmp >= 0){
			BigDecimal loanBalance = balance.subtract(principal);
			throw new RevolvingAccountInsufficientPayoffException(loanBalance);
		} 
	}

	// what is this for now ? 
	public static void revolvingLoansBalanceCheck(List<LoanTransactionData> list ,SavingsAccount savingsAccount){

		BigDecimal sum = list.stream().map(e -> e.outstandingLoanBalance()).reduce(BigDecimal.ZERO ,BigDecimal::add);
		
		BigDecimal savingsAccountBalance = savingsAccount.accountBalance();
		int cmp = sum.compareTo(savingsAccountBalance);
		//if greater than 1 it means throw exception
		if(cmp >= 0){

			BigDecimal loanBalance = sum.subtract(savingsAccountBalance);
			throw new RevolvingAccountInsufficientPayoffException(loanBalance);
		}		
	}

	/**
	 *	Added 10/01/2023 at 0707
	 */
	public static BigDecimal loanBalance(Loan loan ,LoanReadPlatformService loanReadPlatformService, LocalDate transactionDate) {
		Long loanId = loan.getId();
		LoanTransactionData loanTransactionData = loanReadPlatformService.retrieveLoanForeclosureTemplate(loanId, transactionDate);
		BigDecimal loanBalance = loanTransactionData.outstandingLoanBalance();
		return loanBalance;
	}



		// what does this even do ?
	// gets the amount being revolved like outstanding balance from previous loan
	// added 02/08/2022 at 0505
	public static BigDecimal revolvingLoanDTOAmountEx(Loan loan , SavingsAccount fromSavingsAccount , LoanReadPlatformService loanReadPlatformService, LocalDate transactionDate){

 	  	Long loanId = loan.getId();

	    LoanTransactionData loanTransactionData = loanReadPlatformService.retrieveLoanForeclosureTemplate(loanId ,transactionDate);
		
		BigDecimal revolveLoanBalance =  loanTransactionData.outstandingLoanBalance();

		BigDecimal savingsAccountBalance = fromSavingsAccount.accountBalance();

        LoanProduct loanProduct = loan.loanProduct();

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
        return revolveLoanBalance ;
	}

	public static List payOffLoanWithLoan(AccountTransfersWritePlatformService accountTransfersWritePlatformService ,LoanAccountDomainService loanAccountDomainService, Loan fromLoan , Loan toLoan , LocalDate transactionDate , DateTimeFormatter dateTimeFormatter ,Locale locale, BigDecimal amount ,String description){

		//System.err.println("---------------------------payoffloan with loan --------"+toLoan.getLoanStatus());

		AccountTransferDTO accountTransferDTO = AccountTransferDTO.loanToLoan(fromLoan ,toLoan ,transactionDate ,locale ,dateTimeFormatter ,amount, description);

		
		AccountTransferDetails accountTransferDetails = accountTransfersWritePlatformService.repayLoanWithTopup(accountTransferDTO);

		//System.err.println("----------------after transfer the details status is "+toLoan.getLoanStatus());
		
		forecloseLoan(loanAccountDomainService ,toLoan ,transactionDate ,description);

		//System.err.println("-----------then foreclosre loan here ---------");

		List<AccountTransferTransaction> accountTransferTransactions = accountTransferDetails.accountTransferTransactions();
		
		return accountTransferTransactions;
	}

	public static void forecloseLoan(LoanAccountDomainService loanAccountDomainService ,Loan loan ,LocalDate transactionDate ,String description){
		//System.err.println("----------loan we closing is "+loan.getId()+"--------------has status "+loan.getLoanStatus());
		loanAccountDomainService.foreCloseLoan(loan ,transactionDate ,description);
	}

	/**
	 * Added 08/01/2023 at 1652
	 */
	public static List<Loan> revolvingLoans(LoanRepositoryWrapper loanRepositoryWrapper, Loan loan){

		String loanAccountIds = loan.revolvingAccountId();

		StringTokenizer stringTokenizer = new StringTokenizer(loanAccountIds ,",");
		List<Loan> loanList = new ArrayList<>();
		while (stringTokenizer.hasMoreTokens()){
			String idStr = stringTokenizer.nextToken();
			try{
				Long id = Long.valueOf(idStr);
				Loan loan1 = loanRepositoryWrapper.findOneWithNotFoundDetection(id);
				loanList.add(loan1);
			}
			catch (Exception n){

			}
		}
        return loanList ;
	} 

}