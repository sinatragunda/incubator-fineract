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

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionDTO;
import org.apache.fineract.portfolio.savings.exception.DepositAccountTransactionNotAllowedException;
import org.apache.fineract.portfolio.savings.repo.SavingsAccountMonthlyDepositRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;


// Added 20/07/2021
import org.apache.fineract.portfolio.savings.helper.SavingsMonthlyDepositHelper;
//import org.apache.fineract.portfolio.savings.repo.SavingsAccountMonthlyDepositRepository;
import org.joda.time.format.DateTimeFormat;

@Service
public class SavingsAccountDomainServiceJpa implements SavingsAccountDomainService {

    private final PlatformSecurityContext context;
    private final SavingsAccountRepositoryWrapper savingsAccountRepository;
    private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final ConfigurationDomainService configurationDomainService;
    private final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final SavingsAccountMonthlyDepositRepository savingsAccountMonthlyDepositRepository;
    private final SavingsAccountAssembler savingsAccountAssembler ;
    private final NoteRepository noteRepository ;


    @Autowired
    public SavingsAccountDomainServiceJpa(final SavingsAccountRepositoryWrapper savingsAccountRepository,
            final SavingsAccountTransactionRepository savingsAccountTransactionRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final ConfigurationDomainService configurationDomainService, final PlatformSecurityContext context,
            final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository, 
            final BusinessEventNotifierService businessEventNotifierService ,
            final SavingsAccountMonthlyDepositRepository savingsAccountMonthlyDepositRepository ,final SavingsAccountAssembler savingsAccountAssembler ,final NoteRepository noteRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
        this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.configurationDomainService = configurationDomainService;
        this.context = context;
        this.depositAccountOnHoldTransactionRepository = depositAccountOnHoldTransactionRepository;
        this.businessEventNotifierService = businessEventNotifierService;
        this.savingsAccountMonthlyDepositRepository = savingsAccountMonthlyDepositRepository;
        this.savingsAccountAssembler = savingsAccountAssembler ;
        this.noteRepository = noteRepository;
    }

    @Transactional
    @Override
    public SavingsAccountTransaction handleWithdrawal(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final SavingsTransactionBooleanValues transactionBooleanValues) {

        AppUser user = getAppUserIfPresent();
        account.validateForAccountBlock();
        account.validateForDebitBlock();
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();
        if (transactionBooleanValues.isRegularTransaction() && !account.allowWithdrawal()) { throw new DepositAccountTransactionNotAllowedException(
                account.getId(), "withdraw", account.depositAccountType()); }
        final Set<Long> existingTransactionIds = new HashSet<>();
        final LocalDate postInterestOnDate = null;
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);
        
        Integer accountType = null;
        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, new Date(), user, accountType);

        final SavingsAccountTransaction withdrawal = account.withdraw(transactionDTO, transactionBooleanValues.isApplyWithdrawFee());

        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, transactionBooleanValues.isInterestTransfer(), isSavingsInterestPostingAtCurrentPeriodEnd,
                    financialYearBeginningMonth, postInterestOnDate);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, transactionBooleanValues.isInterestTransfer(),
                    isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth, postInterestOnDate);
        }
        List<DepositAccountOnHoldTransaction> depositAccountOnHoldTransactions = null;
        if (account.getOnHoldFunds().compareTo(BigDecimal.ZERO) == 1) {
            depositAccountOnHoldTransactions = this.depositAccountOnHoldTransactionRepository
                    .findBySavingsAccountAndReversedFalseOrderByCreatedDateAsc(account);
        }

        // added 28/03/2022
        // added so that products that allow overdrawing can actually transfer money out of this account
        SavingsProduct savingsProduct = account.savingsProduct();
        boolean isOverdraftAccount = savingsProduct.isAllowOverdraft();

        if(!isOverdraftAccount) {
            account.validateAccountBalanceDoesNotBecomeNegative(transactionAmount, transactionBooleanValues.isExceptionForBalanceCheck(),
                    depositAccountOnHoldTransactions);

        }

        saveTransactionToGenerateTransactionId(withdrawal);
        this.savingsAccountRepository.save(account);

        /// added 26/07/2021
        /// here we add new value to save this transaction to monthly withdrawals 
        SavingsMonthlyDepositHelper.handleDepositOrWithdraw(savingsAccountMonthlyDepositRepository ,account ,transactionAmount ,transactionDate ,false);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, transactionBooleanValues.isAccountTransfer());
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.SAVINGS_WITHDRAWAL,
                constructEntityMap(BUSINESS_ENTITY.SAVINGS_TRANSACTION, withdrawal));
        return withdrawal;
    }


    @Transactional
    @Override
    public SavingsAccountTransaction handleWithdrawalLite(final SavingsAccount savingsAccount ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText){

        AppUser user = getAppUserIfPresent();
        PaymentDetail paymentDetail = null ;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");

        SavingsTransactionBooleanValues savingsTransactionBooleanValues = SavingsTransactionBooleanValues.liteInstance();

        SavingsAccountTransaction savingsAccountTransaction =  handleWithdrawal(savingsAccount ,fmt ,transactionDate ,transactionAmount ,paymentDetail ,savingsTransactionBooleanValues);

        Optional.ofNullable(noteText).ifPresent(e->{  
            System.err.println("-------------note is---------------- "+noteText);
            final Note note = Note.savingsTransactionNote (savingsAccount, savingsAccountTransaction, noteText);
            this.noteRepository.save(note);
        });

        return savingsAccountTransaction ;
    
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    @Transactional
    @Override
    public SavingsAccountTransaction handleDeposit(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final boolean isAccountTransfer, final boolean isRegularTransaction) {
        final SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DEPOSIT;
        return handleDeposit(account, fmt, transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction,
                savingsAccountTransactionType);
    }


    @Transactional
    @Override
    public SavingsAccountTransaction handleDepositLite(final Long savingsAccountId ,LocalDate transactionDate , BigDecimal transactionAmount){

        AppUser user = getAppUserIfPresent();
        Integer accountType = null;
        PaymentDetail paymentDetail = null ;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");

        SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(savingsAccountId);

        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, new Date(), user, accountType);

        SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DEPOSIT;
        return handleDeposit(savingsAccount ,fmt ,transactionDate ,transactionAmount ,paymentDetail ,false ,true ,savingsAccountTransactionType);
    }



    // Added 02/01/2021 ,with notes .
    @Transactional
    @Override
    public SavingsAccountTransaction handleDepositLiteEx(final SavingsAccount savingsAccount ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText){

        AppUser user = getAppUserIfPresent();
        Integer accountType = null;
        PaymentDetail paymentDetail = null ;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");

        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, new Date(), user, accountType);


        SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DEPOSIT;
        SavingsAccountTransaction savingsAccountTransaction = handleDeposit(savingsAccount ,fmt ,transactionDate ,transactionAmount ,paymentDetail ,false ,true ,savingsAccountTransactionType);

        Optional.ofNullable(noteText).ifPresent(e->{
            
            System.err.println("-------------note is---------------- "+noteText);

            final Note note = Note.savingsTransactionNote (savingsAccount, savingsAccountTransaction, noteText);
            this.noteRepository.save(note);
        });

        return savingsAccountTransaction;
    }


    // Added 19/07/2022 ,extended version of the Ex.
    @Transactional
    @Override
    public SavingsAccountTransaction handleDepositLiteEx1(final Long savingsAccountId ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText){

        SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(savingsAccountId);
        return handleDepositLiteEx(savingsAccount ,transactionDate ,transactionAmount,noteText);

    }

    private SavingsAccountTransaction handleDeposit(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final boolean isAccountTransfer, final boolean isRegularTransaction,
            final SavingsAccountTransactionType savingsAccountTransactionType) {
        

        AppUser user = getAppUserIfPresent();
        account.validateForAccountBlock();
        account.validateForCreditBlock();
        final boolean isSavingsInterestPostingAtCurrentPeriodEnd = this.configurationDomainService
                .isSavingsInterestPostingAtCurrentPeriodEnd();
        final Integer financialYearBeginningMonth = this.configurationDomainService.retrieveFinancialYearBeginningMonth();

        if (isRegularTransaction && !account.allowDeposit()) { throw new DepositAccountTransactionNotAllowedException(account.getId(),
                "deposit", account.depositAccountType()); }
        boolean isInterestTransfer = false;
        final Set<Long> existingTransactionIds = new HashSet<>();
        final Set<Long> existingReversedTransactionIds = new HashSet<>();
        updateExistingTransactionsDetails(account, existingTransactionIds, existingReversedTransactionIds);
        Integer accountType = null;
        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, new Date(), user, accountType);
        final SavingsAccountTransaction deposit = account.deposit(transactionDTO, savingsAccountTransactionType);
        final LocalDate postInterestOnDate = null;
        final MathContext mc = MathContext.DECIMAL64;
        if (account.isBeforeLastPostingPeriod(transactionDate)) {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.postInterest(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth,
            		postInterestOnDate);
        } else {
            final LocalDate today = DateUtils.getLocalDateOfTenant();
            account.calculateInterestUsing(mc, today, isInterestTransfer, isSavingsInterestPostingAtCurrentPeriodEnd,
                    financialYearBeginningMonth, postInterestOnDate);
        }



        saveTransactionToGenerateTransactionId(deposit);

        this.savingsAccountRepository.saveAndFlush(account);


        /// added 20/07/2021
        /// here we add new value to save this transaction to monthly deposit
        /// modified 28/12/2021 error with start dates ,backdated transactions adding to current month transactions instead of creating own record
        /// added transaction date field

        SavingsMonthlyDepositHelper.handleDepositOrWithdraw(savingsAccountMonthlyDepositRepository ,account ,transactionAmount ,transactionDate ,true);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.SAVINGS_DEPOSIT,
                constructEntityMap(BUSINESS_ENTITY.SAVINGS_TRANSACTION, deposit));
        return deposit;
    }

    @Override
    public SavingsAccountTransaction handleDividendPayout(final SavingsAccount account, final LocalDate transactionDate,
            final BigDecimal transactionAmount) {
        final DateTimeFormatter fmt = null;
        final PaymentDetail paymentDetail = null;
        final boolean isAccountTransfer = false;
        final boolean isRegularTransaction = true;
        final SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DIVIDEND_PAYOUT;
        return handleDeposit(account, fmt, transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction,
                savingsAccountTransactionType);
    }

    private Long saveTransactionToGenerateTransactionId(final SavingsAccountTransaction transaction) {
        this.savingsAccountTransactionRepository.save(transaction);
        return transaction.getId();
    }

    private void updateExistingTransactionsDetails(SavingsAccount account, Set<Long> existingTransactionIds,
            Set<Long> existingReversedTransactionIds) {
        existingTransactionIds.addAll(account.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(account.findExistingReversedTransactionIds());
    }

    private void postJournalEntries(final SavingsAccount savingsAccount, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds, boolean isAccountTransfer) {

        final MonetaryCurrency currency = savingsAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);

        final Map<String, Object> accountingBridgeData = savingsAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForSavings(accountingBridgeData);
    }

    @Transactional
    @Override
    public void postJournalEntries(final SavingsAccount account, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {

        final boolean isAccountTransfer = false;
        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }
}