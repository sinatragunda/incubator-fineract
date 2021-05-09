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

package org.apache.fineract.portfolio.account.helper;

import java.math.BigDecimal;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanaccount.domain.Loan ;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountSummary;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SettlementInstructionsHelper{

	private long loanId ;
	private long savingsAccountId ;
	private boolean adjustSettlementAmount = false ;
	private SavingsAccount savingsAccount ;
	private Loan loan ;
	private LoanAssembler loanAssemble;
	private SavingsAccountAssembler savingsAccountAssembler ;


	@Autowired
	public SettlementInstructionsHelper(final LoanAssembler loanAssemble ,final SavingsAccountAssembler savingsAccountAssembler){
		this.loanAssemble = loanAssemble ;
		this.savingsAccountAssembler = savingsAccountAssembler ;
	}


	public void init(long loanId ,long savingsAccountId){

		this.loan = loanAssemble.assembleFrom(loanId);
		this.savingsAccount = savingsAccountAssembler.assembleFrom(savingsAccountId);
		
		if(loan != null){
			///get loan product data 
			final LoanProduct loanProduct = loan.getLoanProduct();
			this.adjustSettlementAmount = loanProduct.isSettlementPartialPayment();

		}
	}

	public BigDecimal getTransactionAmount(BigDecimal transactionAmount){

		///get savings account balance here 

		if(savingsAccount==null){
			return BigDecimal.ZERO ;
		}

		SavingsAccountSummary savingsAccountSummary = savingsAccount.getSummary();

		if(savingsAccountSummary==null){
			return BigDecimal.ZERO ;
		}

		BigDecimal accountBalance = savingsAccountSummary.getAccountBalance();

		if(accountBalance.compareTo(BigDecimal.ZERO) < 1){
			/////block this payment and just raise insufficient balance
			return transactionAmount ; 
		
		}

		if(transactionAmount.compareTo(accountBalance) > 0){
			return accountBalance ;
		}

		return transactionAmount ;
	}

	public boolean isAdjustSettlementAmount(){
		return adjustSettlementAmount ;
	}


}