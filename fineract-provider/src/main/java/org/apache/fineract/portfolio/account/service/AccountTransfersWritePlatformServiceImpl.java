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
package org.apache.fineract.portfolio.account.service;

import static org.apache.fineract.portfolio.account.AccountDetailConstants.fromAccountIdParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.fromAccountTypeParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.toAccountIdParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.toAccountTypeParamName;
import static org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants.transferAmountParamName;
import static org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants.transferDateParamName;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.account.data.AccountTransfersDataValidator;
import org.apache.fineract.portfolio.account.domain.AccountTransferAssembler;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetailRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.account.domain.AccountTransferRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferTransaction;
import org.apache.fineract.portfolio.account.domain.AccountTransferType;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidPaidInAdvanceAmountException;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.apache.fineract.portfolio.savings.domain.*;
import org.apache.fineract.portfolio.savings.enumerations.SAVINGS_TRANSACTION_TRIGGER_TYPE;
import org.apache.fineract.portfolio.savings.helper.SavingsTransactionsTriggerHelper;
import org.apache.fineract.portfolio.savings.repo.SavingsTransactionTriggerRepository;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccountAssembler;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccountDomainService;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccountTransaction;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountTransfersWritePlatformServiceImpl implements AccountTransfersWritePlatformService {

    private final AccountTransfersDataValidator accountTransfersDataValidator;
    private final AccountTransferAssembler accountTransferAssembler;
    private final AccountTransferRepository accountTransferRepository;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final LoanAssembler loanAccountAssembler;
    private final LoanAccountDomainService loanAccountDomainService;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final AccountTransferDetailRepository accountTransferDetailRepository;
    private final LoanReadPlatformService loanReadPlatformService;

    // added 31/01/2022
    private final ShareAccountAssembler shareAccountAssembler ;
    private final ShareAccountDomainService shareAccountDomainService ;

    // added 12/08/2022
    private final SavingsTransactionTriggerRepository savingsTransactionTriggerRepository ;

    /**
     * Added 29/01/2023 at 1049
     */
    public final AccountTransfersReadPlatformService accountTransfersReadPlatformService;

    @Autowired
    public AccountTransfersWritePlatformServiceImpl(final AccountTransfersDataValidator accountTransfersDataValidator,
            final AccountTransferAssembler accountTransferAssembler, final AccountTransferRepository accountTransferRepository,
            final SavingsAccountAssembler savingsAccountAssembler, final SavingsAccountDomainService savingsAccountDomainService,
            final LoanAssembler loanAssembler, final LoanAccountDomainService loanAccountDomainService,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final AccountTransferDetailRepository accountTransferDetailRepository,
            final LoanReadPlatformService loanReadPlatformService , final ShareAccountAssembler shareAccountAssembler ,final ShareAccountDomainService shareAccountDomainService ,final SavingsTransactionTriggerRepository savingsTransactionTriggerRepository ,final  AccountTransfersReadPlatformService accountTransfersReadPlatformService) {
        this.accountTransfersDataValidator = accountTransfersDataValidator;
        this.accountTransferAssembler = accountTransferAssembler;
        this.accountTransferRepository = accountTransferRepository;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.loanAccountAssembler = loanAssembler;
        this.loanAccountDomainService = loanAccountDomainService;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.accountTransferDetailRepository = accountTransferDetailRepository;
        this.loanReadPlatformService = loanReadPlatformService;
        this.shareAccountAssembler = shareAccountAssembler ;
        this.shareAccountDomainService = shareAccountDomainService ;

        // added 11/08/2022
        this.savingsTransactionTriggerRepository = savingsTransactionTriggerRepository ;
        this.accountTransfersReadPlatformService = accountTransfersReadPlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {

        boolean isRegularTransaction = true;

        this.accountTransfersDataValidator.validate(command);

        //System.err.println("---------------data validated now -------------- ,this for this validation thing ----------------");

        final LocalDate transactionDate = command.localDateValueOfParameterNamed(transferDateParamName);
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed(transferAmountParamName);

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        final Integer fromAccountTypeId = command.integerValueSansLocaleOfParameterNamed(fromAccountTypeParamName);
        final PortfolioAccountType fromAccountType = PortfolioAccountType.fromInt(fromAccountTypeId);

        final Integer toAccountTypeId = command.integerValueSansLocaleOfParameterNamed(toAccountTypeParamName);
        final PortfolioAccountType toAccountType = PortfolioAccountType.fromInt(toAccountTypeId);

        String fmtNew = transactionDate.toString("dd MMMM yyyy");

        //System.err.println("--------------------------new formatted date is ----------"+fmtNew);

        final String note = command.stringValueOfParameterNamed("transferDescription");

        final PaymentDetail paymentDetail = null;
        Long fromSavingsAccountId = null;
        Long transferDetailId = null;
        boolean isInterestTransfer = false;
        boolean isAccountTransfer = true;
        Long fromLoanAccountId = null;
        boolean isWithdrawBalance = false;
        Long transactionId = null ;
        Long subEntityId = null ;
        Long shareAccountTransactionId = null ;
        final TransactionCode transactionCode = null ;

        if (isSavingsToSavingsAccountTransfer(fromAccountType, toAccountType)) {

            //System.err.println("--------------savings to savings transfer --------------");

            fromSavingsAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
            final SavingsAccount fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(fromSavingsAccountId);

            //System.err.println("-----------------from savings account -------------"+fromSavingsAccount.getClient().getDisplayName());

            //System.err.println("---------------account balance is -------------"+fromSavingsAccount.getWithdrawableBalance());

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, transactionBooleanValues ,transactionCode);

            //System.err.println("-------------withdrawal id us  ------------------"+withdrawal.getId());


            final Long toSavingsId = command.longValueOfParameterNamed(toAccountIdParamName);
            final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsId);

            //System.err.println("--------------------handle deposit now ---------------------");

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction);

            //System.err.println("-------------------------error thrown there ? ---------------------");

            transactionId = withdrawal.getId();
            subEntityId = deposit.getId();

            final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSavingsTransfer(command,
                    fromSavingsAccount, toSavingsAccount, withdrawal, deposit);
            
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferDetailId = accountTransferDetails.getId();

        } else if (isSavingsToLoanAccountTransfer(fromAccountType, toAccountType)) {
            // usually when we paying charges right ?

            //System.err.println("------------------------is this a charge repayment transaction ? ---------------");

            fromSavingsAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
            final SavingsAccount fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(fromSavingsAccountId);

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);
            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, transactionBooleanValues,transactionCode);

            // how do we set it now

            //System.err.println("----------------------- savings transaction id is -----------------------"+withdrawal.getId());
            subEntityId = withdrawal.getId();

            final Long toLoanAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
            final Loan toLoanAccount = this.loanAccountAssembler.assembleFrom(toLoanAccountId);

            final Boolean isHolidayValidationDone = false;
            final HolidayDetailDTO holidayDetailDto = null;
            final boolean isRecoveryRepayment = false;
            final LoanTransaction loanRepaymentTransaction = this.loanAccountDomainService.makeRepayment(toLoanAccount,
                    new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null,
                    isRecoveryRepayment, isAccountTransfer, holidayDetailDto, isHolidayValidationDone);


            //System.err.println("------------"+toLoanAccountId+"------------loan transaction id here is -----------"+loanRepaymentTransaction.getId());

            transactionId = loanRepaymentTransaction.getId();

            final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleSavingsToLoanTransfer(command,
                    fromSavingsAccount, toLoanAccount, withdrawal, loanRepaymentTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);


            transferDetailId = accountTransferDetails.getId();

        } else if (isLoanToSavingsAccountTransfer(fromAccountType, toAccountType)) {
            // FIXME - kw - ADD overpaid loan to savings account transfer
            // support.

            fromLoanAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
            final Loan fromLoanAccount = this.loanAccountAssembler.assembleFrom(fromLoanAccountId);

            final LoanTransaction loanRefundTransaction = this.loanAccountDomainService.makeRefund(fromLoanAccountId,
                    new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null);

            final Long toSavingsAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
            final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsAccountId);

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction);

            final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(command,
                    fromLoanAccount, toSavingsAccount, deposit, loanRefundTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferDetailId = accountTransferDetails.getId();

        }
        // added 31/01/2022
        else if(isSavingsToShareAccountTransfer(fromAccountType, toAccountType)) {

            //System.err.println("----------------------------transfer ----------------");

            fromSavingsAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
            final SavingsAccount fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(fromSavingsAccountId);

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, transactionBooleanValues ,transactionCode);

            Note savingsNote = Note.savingsTransactionNote(fromSavingsAccount ,withdrawal ,note);

            final Long toShareAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
            final ShareAccount toShareAccount = this.shareAccountAssembler.assembleFrom(toShareAccountId);

            final ShareAccountTransaction shareAccountTransaction = this.shareAccountDomainService.purchaseShares(toShareAccount, new CommandProcessingResultBuilder(),
                    transactionDate, transactionAmount, paymentDetail,savingsNote ,null , isAccountTransfer);

            final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleSavingsToShareTransfer(command,
                    fromSavingsAccount, withdrawal , toShareAccount,shareAccountTransaction);

            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);

            shareAccountTransactionId = shareAccountTransaction.getId();

            //System.err.println("------------share account id"+toShareAccountId+" +--------share transaction id is ---------------"+shareAccountTransactionId);

            //System.err.println("----------------------we need something else as well to identify these --------------");

            // use this as savings transaction id 
            subEntityId = withdrawal.getId();

            //System.err.println("------------savings account id ----"+fromSavingsAccountId+"--savings transaction id is -------------"+subEntityId);
        }

        final CommandProcessingResultBuilder builder = new CommandProcessingResultBuilder().withEntityId(transferDetailId);

        if (fromAccountType.isSavingsAccount()) {
            //System.err.println("----------------------from account transaction set here as well ?---------------------");
            //builder.withCommandId(commandId); // savings transaction id here ,will use it to reverse transactions ..
            builder.withSavingsId(fromSavingsAccountId);
            builder.withTransactionId(String.valueOf(transactionId));

            // savings transaction id here instead of command id
            builder.withSubEntityId(subEntityId);
        }
        if(toAccountType.isSharesAccount()){
            builder.withTransactionId(shareAccountTransactionId.toString());
            //builder.withCommandId(commandId);
            //builder.withResourceIdAsString(commandId.toString());
            builder.withSubEntityId(subEntityId);
        }

        // added feature 14/04/2022
        if(toAccountType.isSavingsAccount()){
            //System.err.println("---------------------set another id here son -----------------"+subEntityId);
        }

        if(toAccountType.isLoanAccount()){
            ///
            ///System.err.println("-------------------------to loan account is savings account ---------------");
            builder.withTransactionId(String.valueOf(transactionId));

        }
        if (fromAccountType.isLoanAccount()) {
            builder.withLoanId(fromLoanAccountId);
        }

        return builder.build();
    }

    @Override
    @Transactional
    public void reverseTransfersWithFromAccountType(final Long accountNumber, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = null;
        if (accountTypeId.isLoanAccount()) {
            acccountTransfers = this.accountTransferRepository.findByFromLoanId(accountNumber);
        }
        if (acccountTransfers != null && acccountTransfers.size() > 0) {
            undoTransactions(acccountTransfers);
        }

    }
    
    @Override
    @Transactional
    public void reverseTransfersWithFromAccountTransactions(final Collection<Long> fromTransactionIds, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = null;
        if (accountTypeId.isLoanAccount()) {
            acccountTransfers = this.accountTransferRepository.findByFromLoanTransactions(fromTransactionIds);
        }
        if (acccountTransfers != null && acccountTransfers.size() > 0) {
            undoTransactions(acccountTransfers);
        }

    }

    @Override
    @Transactional
    public void reverseAllTransactions(final Long accountId, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = new ArrayList<>();
        if (accountTypeId.isLoanAccount()) {

            Collection<AccountTransferData> accountTransferDataList = this.accountTransfersReadPlatformService.findAllByLoanId(accountId);

            Consumer<AccountTransferData> findAccountTransferRecord = (e)->{
                Long id = e.getId();
                
                //System.err.println("-----------------------------------we have found id of record ----"+id);
                AccountTransferTransaction accountTransferTransaction = this.accountTransferRepository.findOne(id);

                //System.out.println("-----------------------we have record ? "+ Optional.of(accountTransferTransaction).isPresent());

                acccountTransfers.add(accountTransferTransaction);
            };


            //System.err.println("------------------------------------------- value here is "+accountTransferDataList.size());

            accountTransferDataList.stream().forEach(findAccountTransferRecord);
            //acccountTransfers = this.accountTransferRepository.findAllByLoanId(accountId);
        }

        if(!acccountTransfers.isEmpty()){
            //System.out.println("------------------------------undo all transactions -------------------");
            undoTransactions(acccountTransfers);
        }
    }

    /**
     * @param acccountTransfers
     */
    private void undoTransactions(final List<AccountTransferTransaction> acccountTransfers) {
        for (final AccountTransferTransaction accountTransfer : acccountTransfers) {

            //System.out.println("=--------------------reverse the transaction ---------"+accountTransfer);

            if (accountTransfer.getFromLoanTransaction() != null) {
                //System.out.println("---------------------from loan transaction not null--------------");
                this.loanAccountDomainService.reverseTransfer(accountTransfer.getFromLoanTransaction());
            }
            if (accountTransfer.getToLoanTransaction() != null) {
                //System.out.println("---------------------get to loan transaction not null--------------");
                this.loanAccountDomainService.reverseTransfer(accountTransfer.getToLoanTransaction());
            }
            if (accountTransfer.getFromTransaction() != null) {
                //System.out.println("---------------------get from transaction not null--------------");
                this.savingsAccountWritePlatformService.undoTransaction(accountTransfer.accountTransferDetails().fromSavingsAccount()
                        .getId(), accountTransfer.getFromTransaction().getId(), true);
            }
            if (accountTransfer.getToSavingsTransaction() != null) {
                //System.out.println("---------------------get to savings transaction not null--------------");
                this.savingsAccountWritePlatformService.undoTransaction(
                        accountTransfer.accountTransferDetails().toSavingsAccount().getId(), accountTransfer.getToSavingsTransaction()
                                .getId(), true);
            }
            accountTransfer.reverse();
            //System.out.println("-------------------done reversing ");
            this.accountTransferRepository.save(accountTransfer);
        }
    }

    @Override
    @Transactional
    public Long transferFunds(final AccountTransferDTO accountTransferDTO) {

        Long transferTransactionId = null;
        final boolean isAccountTransfer = true;
        final boolean isRegularTransaction = accountTransferDTO.isRegularTransaction();
        final TransactionCode transactionCode = null ;
        
        AccountTransferDetails accountTransferDetails = accountTransferDTO.getAccountTransferDetails();
        
        if (isSavingsToLoanAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {
            //System.err.println("---------------------savings to loan transfer ,usually where charges are-------");

            SavingsAccount fromSavingsAccount = null;
            Loan toLoanAccount = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getFromSavingsAccount() == null) {
                    fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                }
                if (accountTransferDTO.getLoan() == null) {
                    toLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
                } else {
                    toLoanAccount = accountTransferDTO.getLoan();
                    this.loanAccountAssembler.setHelpers(toLoanAccount);
                }

            } else {
                fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
                this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                toLoanAccount = accountTransferDetails.toLoanAccount();
                this.loanAccountAssembler.setHelpers(toLoanAccount);
            }

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), AccountTransferType.fromInt(
                            accountTransferDTO.getTransferType()).isInterestTransfer(), accountTransferDTO.isExceptionForBalanceCheck());

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), transactionBooleanValues ,transactionCode);

            LoanTransaction loanTransaction = null;

            if (AccountTransferType.fromInt(accountTransferDTO.getTransferType()).isChargePayment()) {
                loanTransaction = this.loanAccountDomainService.makeChargePayment(toLoanAccount, accountTransferDTO.getChargeId(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), null, null, accountTransferDTO.getToTransferType(),
                        accountTransferDTO.getLoanInstallmentNumber());

            } else {
                final boolean isRecoveryRepayment = false;
                final Boolean isHolidayValidationDone=false;
                final HolidayDetailDTO holidayDetailDto=null;
                loanTransaction = this.loanAccountDomainService.makeRepayment(toLoanAccount, new CommandProcessingResultBuilder(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), null, null, isRecoveryRepayment, isAccountTransfer,holidayDetailDto,isHolidayValidationDone);
            }
            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToLoanTransfer(accountTransferDTO, fromSavingsAccount,
                    toLoanAccount, withdrawal, loanTransaction);
            
            try {
                this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);

                // when done record entry 
                SavingsTransactionTrigger trigger = new SavingsTransactionTrigger(withdrawal, toLoanAccount.getId(), SAVINGS_TRANSACTION_TRIGGER_TYPE.LOAN);
                SavingsTransactionsTriggerHelper.trigger(savingsTransactionTriggerRepository, null, trigger);

            }catch(Exception e){
                e.printStackTrace();
            }
            transferTransactionId = accountTransferDetails.getId();
        }

        // added 31/01/2022
        else if (isSavingsToShareAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {
            //
            SavingsAccount fromSavingsAccount = null;
            ShareAccount toShareAccount = null;
            
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getFromSavingsAccount() == null) {
                    fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                }
                if (accountTransferDTO.toShareAccount() == null) {
                    toShareAccount = this.shareAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
                } else {
                    toShareAccount = accountTransferDTO.toShareAccount();
                    //this.shareAccountAssembler.setHelpers(toShareAccount);
                }

            } else {
                fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
                this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                //toShareAccount = accountTransferDetails.
                //this.loanAccountAssembler.setHelpers(toLoanAccount);
            }

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), AccountTransferType.fromInt(
                            accountTransferDTO.getTransferType()).isInterestTransfer(), accountTransferDTO.isExceptionForBalanceCheck());

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), transactionBooleanValues ,transactionCode);

            ShareAccountTransaction shareAccountTransaction = null;

            // share accounts are never charge payments so will just go ahead with buying new shares 

            Note savingsNote = Note.savingsTransactionNote(fromSavingsAccount ,withdrawal ,accountTransferDTO.getNoteText());
            
            shareAccountTransaction = this.shareAccountDomainService.purchaseShares(toShareAccount, new CommandProcessingResultBuilder(),
                    accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(),savingsNote ,accountTransferDTO.getTxnExternalId(), true);
        

            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSharesTransfer(accountTransferDTO, fromSavingsAccount,
                    toShareAccount, withdrawal, shareAccountTransaction);

            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();
        
        }else if (isSavingsToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {

            SavingsAccount fromSavingsAccount = null;
            SavingsAccount toSavingsAccount = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getFromSavingsAccount() == null) {
                    fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                }
                if (accountTransferDTO.getToSavingsAccount() == null) {
                    toSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
                } else {
                    toSavingsAccount = accountTransferDTO.getToSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(toSavingsAccount);
                }
            } else {
                fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
                this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                toSavingsAccount = accountTransferDetails.toSavingsAccount();
                this.savingsAccountAssembler.setHelpers(toSavingsAccount);
            }

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), AccountTransferType.fromInt(
                            accountTransferDTO.getTransferType()).isInterestTransfer(), accountTransferDTO.isExceptionForBalanceCheck());

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), transactionBooleanValues ,transactionCode);

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), isAccountTransfer, isRegularTransaction);

            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSavingsTransfer(accountTransferDTO, fromSavingsAccount,
                    toSavingsAccount, withdrawal, deposit);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();

        } else if (isLoanToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {

            Loan fromLoanAccount = null;
            SavingsAccount toSavingsAccount = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getLoan() == null) {
                    fromLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromLoanAccount = accountTransferDTO.getLoan();
                    this.loanAccountAssembler.setHelpers(fromLoanAccount);
                }
                toSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
            } else {
                fromLoanAccount = accountTransferDetails.fromLoanAccount();
                this.loanAccountAssembler.setHelpers(fromLoanAccount);
                toSavingsAccount = accountTransferDetails.toSavingsAccount();
                this.savingsAccountAssembler.setHelpers(toSavingsAccount);
            }
            LoanTransaction loanTransaction = null;
            if (LoanTransactionType.DISBURSEMENT.getValue().equals(accountTransferDTO.getFromTransferType())) {
                loanTransaction = this.loanAccountDomainService.makeDisburseTransaction(accountTransferDTO.getFromAccountId(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(), accountTransferDTO.getTxnExternalId());
            } else {
                loanTransaction = this.loanAccountDomainService.makeRefund(accountTransferDTO.getFromAccountId(),
                        new CommandProcessingResultBuilder(), accountTransferDTO.getTransactionDate(),
                        accountTransferDTO.getTransactionAmount(), accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(),
                        accountTransferDTO.getTxnExternalId());
            }

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), isAccountTransfer, isRegularTransaction);
            accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(accountTransferDTO, fromLoanAccount,
                    toSavingsAccount, deposit, loanTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();

            //create trigger for transfer transaction
            SavingsTransactionTrigger trigger = new SavingsTransactionTrigger(deposit ,fromLoanAccount.getId() , SAVINGS_TRANSACTION_TRIGGER_TYPE.LOAN);
            SavingsTransactionsTriggerHelper.trigger(savingsTransactionTriggerRepository ,null ,trigger);

        } else {
            throw new GeneralPlatformDomainRuleException("error.msg.accounttransfer.loan.to.loan.not.supported",
                    "Account transfer from loan to another loan is not supported");
        }

        return transferTransactionId;
    }

    @Override
    public AccountTransferDetails repayLoanWithTopup(AccountTransferDTO accountTransferDTO) {
        final boolean isAccountTransfer = true;
        Loan fromLoanAccount = null;
        if (accountTransferDTO.getFromLoan() == null) {
            fromLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
        } else {
            fromLoanAccount = accountTransferDTO.getFromLoan();
            this.loanAccountAssembler.setHelpers(fromLoanAccount);
        }
        Loan toLoanAccount = null;
        if (accountTransferDTO.getToLoan() == null) {
            //System.err.println("-------------------to loan account is null --------------");
            toLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
        } else {
            toLoanAccount = accountTransferDTO.getToLoan();
            this.loanAccountAssembler.setHelpers(toLoanAccount);
        }

        //System.err.println("----------------to loan acoutn details "+toLoanAccount.getLoanStatus());

        LoanTransaction disburseTransaction = this.loanAccountDomainService.makeDisburseTransaction(accountTransferDTO.getFromAccountId(),
                accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(), accountTransferDTO.getTxnExternalId(), true);

        LoanTransaction repayTransaction = this.loanAccountDomainService.makeRepayment(toLoanAccount, new CommandProcessingResultBuilder(),
                accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                accountTransferDTO.getPaymentDetail(), null, null, false, isAccountTransfer,null,false, true);

        AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleLoanToLoanTransfer(accountTransferDTO, fromLoanAccount,
                toLoanAccount, disburseTransaction, repayTransaction);
        
        this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);

        return accountTransferDetails;
    }

    @Override
    @Transactional
    public void updateLoanTransaction(final Long loanTransactionId, final LoanTransaction newLoanTransaction) {
        final AccountTransferTransaction transferTransaction = this.accountTransferRepository.findByToLoanTransactionId(loanTransactionId);
        if (transferTransaction != null) {
            transferTransaction.updateToLoanTransaction(newLoanTransaction);
            this.accountTransferRepository.save(transferTransaction);
        }
    }

    private boolean isLoanToSavingsAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isLoanAccount() && toAccountType.isSavingsAccount();
    }

    private boolean isSavingsToLoanAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isSavingsAccount() && toAccountType.isLoanAccount();
    }

    private boolean isSavingsToSavingsAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isSavingsAccount() && toAccountType.isSavingsAccount();
    }

    // added 31/01/2022
    private boolean isSavingsToShareAccountTransfer(final PortfolioAccountType fromAccountType ,final PortfolioAccountType toAccountType){
        return fromAccountType.isSavingsAccount() && toAccountType.isSharesAccount();
    }
    
    @Override
    @Transactional
    public CommandProcessingResult refundByTransfer(JsonCommand command) {
        // TODO Auto-generated method stub
        this.accountTransfersDataValidator.validate(command);

        final LocalDate transactionDate = command.localDateValueOfParameterNamed(transferDateParamName);
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed(transferAmountParamName);

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        final PaymentDetail paymentDetail = null; 
        Long transferTransactionId = null;

        final Long fromLoanAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
        final Loan fromLoanAccount = this.loanAccountAssembler.assembleFrom(fromLoanAccountId);

        BigDecimal overpaid = this.loanReadPlatformService.retrieveTotalPaidInAdvance(fromLoanAccountId).getPaidInAdvance();

        if (overpaid == null || overpaid.equals(BigDecimal.ZERO) || transactionAmount.floatValue() > overpaid.floatValue()) {
            if(overpaid == null)
                overpaid = BigDecimal.ZERO;
            throw new InvalidPaidInAdvanceAmountException(overpaid.toPlainString());
        }

        final LoanTransaction loanRefundTransaction = this.loanAccountDomainService.makeRefundForActiveLoan(fromLoanAccountId,
                new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null);

        final Long toSavingsAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
        final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsAccountId);

        final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt, transactionDate,
                transactionAmount, paymentDetail, true, true);

        final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(command,
                fromLoanAccount, toSavingsAccount, deposit, loanRefundTransaction);
        this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
        transferTransactionId = accountTransferDetails.getId();

        final CommandProcessingResultBuilder builder = new CommandProcessingResultBuilder().withEntityId(transferTransactionId);

        builder.withSavingsId(toSavingsAccountId);

        return builder.build();
    }
}