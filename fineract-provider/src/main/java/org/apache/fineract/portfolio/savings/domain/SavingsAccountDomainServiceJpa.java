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

import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.teller.helper.CashTransactionLoggingHelper;
import org.apache.fineract.organisation.teller.service.TellerWritePlatformService;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepository;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
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

    /**
     * Added 06/11/2022 at 2052
     */
    private final TellerWritePlatformService tellerWritePlatformService;
    private final PaymentTypeRepositoryWrapper paymentTypeRepositoryWrapper;


    @Autowired
    public SavingsAccountDomainServiceJpa(final SavingsAccountRepositoryWrapper savingsAccountRepository,
            final SavingsAccountTransactionRepository savingsAccountTransactionRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final ConfigurationDomainService configurationDomainService, final PlatformSecurityContext context,
            final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository, 
            final BusinessEventNotifierService businessEventNotifierService ,
            final SavingsAccountMonthlyDepositRepository savingsAccountMonthlyDepositRepository ,final SavingsAccountAssembler savingsAccountAssembler ,final NoteRepository noteRepository ,final TellerWritePlatformService tellerWritePlatformService ,final  PaymentTypeRepositoryWrapper paymentTypeRepositoryWrapper) {
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
        this.tellerWritePlatformService = tellerWritePlatformService;
        this.paymentTypeRepositoryWrapper = paymentTypeRepositoryWrapper;
    }

    @Transactional
    @Override
    public SavingsAccountTransaction handleWithdrawal(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final SavingsTransactionBooleanValues transactionBooleanValues ,final TransactionCode transactionCode) {


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
            try {
                account.calculateInterestUsing(mc, today, transactionBooleanValues.isInterestTransfer(),
                        isSavingsInterestPostingAtCurrentPeriodEnd, financialYearBeginningMonth, postInterestOnDate);
            }
            catch (NullPointerException n){
                n.printStackTrace();
            }

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

        /**
         * Added 06/11/2022 at 2053
         * Handle all withdrawals for cashing with cashier helper
         * Function post settlements to till
         */
        //CashTransactionLoggingHelper.savingsAccountTransaction(tellerWritePlatformService , withdrawal);


        /**
         * Added 26/07/2021
         * Here we add new value to save this transaction to monthly withdrawals
         * To do (06/11/2022 at 2055): Ideal solution is to rely on running balance ,which are accurate but functionality would have to be written on a free day
         *
         */
        SavingsMonthlyDepositHelper.handleDepositOrWithdraw(savingsAccountMonthlyDepositRepository ,account ,transactionAmount ,transactionDate ,false);

        /**
         * Added 27/09/2022 at 0502
         * To be implemented later after done testing functionality for deposits
         */

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, transactionBooleanValues.isAccountTransfer() ,transactionCode);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.SAVINGS_WITHDRAWAL,
                constructEntityMap(BUSINESS_ENTITY.SAVINGS_TRANSACTION, withdrawal));
        return withdrawal;
    }


    @Transactional
    @Override
    public SavingsAccountTransaction handleWithdrawalLite(final SavingsAccount savingsAccount ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText){

        AppUser user = getAppUserIfPresent();
        PaymentType paymentType = paymentTypeRepositoryWrapper.findOneWithNotFoundDetection(1L);
        PaymentDetail paymentDetail =  PaymentDetail.withPaymentType(paymentType);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");

        SavingsTransactionBooleanValues savingsTransactionBooleanValues = SavingsTransactionBooleanValues.liteInstance();

        TransactionCode transactionCode = null ;

        SavingsAccountTransaction savingsAccountTransaction =  handleWithdrawal(savingsAccount ,fmt ,transactionDate ,transactionAmount ,paymentDetail ,savingsTransactionBooleanValues ,transactionCode);

        Optional.ofNullable(noteText).ifPresent(e->{  
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
        
        System.err.println("----------------------------handleDeposit with regular transaction -----------------------");

        final TransactionCode transactionCode = null;
        final SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DEPOSIT;
        return handleDeposit(account, fmt, transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction,
                savingsAccountTransactionType,transactionCode);
    }


    @Transactional
    @Override
    public SavingsAccountTransaction handleDeposit(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final boolean isAccountTransfer, final boolean isRegularTransaction ,TransactionCode transactionCode) {
        final SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DEPOSIT;
        
        System.err.println("---------------where does this function go now -----handleDeposit--");
        return handleDeposit(account, fmt, transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction,
                savingsAccountTransactionType ,transactionCode);
    }

    /**
     * Modified 05/11/2022 at 0448 
     */ 
    @Transactional
    @Override
    public SavingsAccountTransaction handleDepositLite(final Long savingsAccountId ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText){

        AppUser user = getAppUserIfPresent();
        Integer accountType = null;
        PaymentDetail paymentDetail = null ;
        final TransactionCode transactionCode = null ;


        System.err.println("------------handle deposit lite -----------------");

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");

        SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(savingsAccountId);

        final SavingsAccountTransactionDTO transactionDTO = new SavingsAccountTransactionDTO(fmt, transactionDate, transactionAmount,
                paymentDetail, new Date(), user, accountType);

        SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DEPOSIT;
        SavingsAccountTransaction savingsAccountTransaction = handleDeposit(savingsAccount ,fmt ,transactionDate ,transactionAmount ,paymentDetail ,false ,true ,savingsAccountTransactionType ,transactionCode);


        Optional.ofNullable(noteText).ifPresent(e->{           
            final Note note = Note.savingsTransactionNote (savingsAccount, savingsAccountTransaction, noteText);
            this.noteRepository.save(note);
        });

        return savingsAccountTransaction;

    }



    // Added 02/01/2021 ,with notes .
    @Transactional
    @Override
    public SavingsAccountTransaction handleDepositLite(final SavingsAccount savingsAccount ,LocalDate transactionDate , BigDecimal transactionAmount ,String noteText){

        Long savingsAccountId = savingsAccount.getId();
        return handleDepositLite(savingsAccountId ,transactionDate ,transactionAmount ,noteText);
    
    }


    private SavingsAccountTransaction handleDeposit(final SavingsAccount account, final DateTimeFormatter fmt,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail,
            final boolean isAccountTransfer, final boolean isRegularTransaction,
            final SavingsAccountTransactionType savingsAccountTransactionType,final TransactionCode transactionCode) {
        
        System.err.println("---------private function to handle deposit----------------"+isAccountTransfer);

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

        //System.err.println("--------------payment detail values ? "+Optional.ofNullable(paymentDetail).isPresent());


        //System.err.println("--------------is cash payment now son  ? "+Optional.ofNullable(paymentDetail.getPaymentType()).isPresent());


        //System.err.println("--------------is cash payment ? "+paymentDetail.getPaymentType().isCashPayment());

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

        System.err.println("-----------------does this function always handle deposits all the time son ?");


        saveTransactionToGenerateTransactionId(deposit);

        this.savingsAccountRepository.saveAndFlush(account);
        /**
         * Added 06/11/2022 at 2020
         * We intend to login all cash transactions from here on
         * Assuming there arent handled anywhere else in the system
         */
        if(!isAccountTransfer) {
            //CashTransactionLoggingHelper.savingsAccountTransaction(tellerWritePlatformService, deposit);
        }

        /**
         * Added 20/07/2021
         * here we add new value to save this transaction to monthly deposit
         * Modified 28/12/2021 error with start dates 
         * Backdated transactions adding to current month transactions instead of creating own record added transaction date field
         * */
        SavingsMonthlyDepositHelper.handleDepositOrWithdraw(savingsAccountMonthlyDepositRepository ,account ,transactionAmount ,transactionDate ,true);

        //System.err.println("--------------transaction code is not inserted anywhere ---------"+Optional.ofNullable(transactionCode).isPresent());

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer ,transactionCode);
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
        final TransactionCode transactionCode= null ;
        final SavingsAccountTransactionType savingsAccountTransactionType = SavingsAccountTransactionType.DIVIDEND_PAYOUT;
        return handleDeposit(account, fmt, transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction,
                savingsAccountTransactionType,transactionCode);
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
            final Set<Long> existingReversedTransactionIds, boolean isAccountTransfer ,TransactionCode transactionCode) {

        System.err.println("-------------------------postJournalEntries ---------------");

        final MonetaryCurrency currency = savingsAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);

        final Map<String, Object> accountingBridgeData = savingsAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer,transactionCode
                );

        //System.err.println("---------------data with account bridge --------------------");
        this.journalEntryWritePlatformService.createJournalEntriesForSavings(accountingBridgeData);
    }

    @Transactional
    @Override
    public void postJournalEntries(final SavingsAccount account, final Set<Long> existingTransactionIds,
            final Set<Long> existingReversedTransactionIds) {

        final boolean isAccountTransfer = false;
        final TransactionCode transactionCode = null; 
        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds, isAccountTransfer,transactionCode);
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }
}