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
package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

public interface SavingsAccountDomainService {

    SavingsAccountTransaction handleWithdrawal(SavingsAccount account, DateTimeFormatter fmt, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, SavingsTransactionBooleanValues transactionBooleanValues,TransactionCode transactionCode);

    SavingsAccountTransaction handleDeposit(SavingsAccount account, DateTimeFormatter fmt, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, boolean isAccountTransfer, boolean isRegularTransaction);

    
    // Added 22/07/2021
    SavingsAccountTransaction handleDepositLite(Long savingsAccountId ,LocalDate transactionDate , BigDecimal transactionAmount);
   
    void postJournalEntries(SavingsAccount savingsAccount, Set<Long> existingTransactionIds, Set<Long> existingReversedTransactionIds);

    SavingsAccountTransaction handleDividendPayout(SavingsAccount account, LocalDate transactionDate, BigDecimal transactionAmount);


    // Added 02/01/2021
    SavingsAccountTransaction handleDepositLiteEx(SavingsAccount savingsAccount ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText);

    // Added 19/07/2022
    SavingsAccountTransaction handleDepositLiteEx1(Long savingsAccountId ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText);

     // Added 02/08/2021
    SavingsAccountTransaction handleWithdrawalLite(SavingsAccount savingsAccount ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText);

    /**
     * Added 24/09/2022 at 0037
     */ 
    SavingsAccountTransaction handleDeposit(SavingsAccount account, DateTimeFormatter fmt, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, boolean isAccountTransfer, boolean isRegularTransaction ,TransactionCode transactionCode);

   
   
}