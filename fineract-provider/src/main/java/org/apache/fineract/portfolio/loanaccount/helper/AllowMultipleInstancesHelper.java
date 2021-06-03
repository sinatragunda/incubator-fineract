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
    24/05/2021
        added allow multiple instances helper class 
*/
package org.apache.fineract.portfolio.loanaccount.helper;

import java.util.Collection;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.exception.AllowMultipleLoanInstancesException;

import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;

import org.apache.fineract.portfolio.accountdetails.data.AccountSummaryCollectionData;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;

public class AllowMultipleInstancesHelper{

	public static void status(LoanProduct loanProduct ,AccountDetailsReadPlatformService accountDetailsReadPlatformService ,Long productId, Long clientId){

		boolean allow = loanProduct.allowMultipleInstances();

		if(!allow){

        	AccountSummaryCollectionData clientAccount = accountDetailsReadPlatformService.retrieveClientAccountDetails(clientId);
			/// lets iterate through loans now get data we want son 
			Collection<LoanAccountSummaryData> loanAccounts = clientAccount.loanAccounts();

			try {
				for(LoanAccountSummaryData l : loanAccounts){
					if(productId==l.productId()){
						if(l.isActive()){
							throw new AllowMultipleLoanInstancesException(loanProduct.productName());
						}
					}
				}
			}
			catch(NullPointerException n){
			}
		}
	}

 }
