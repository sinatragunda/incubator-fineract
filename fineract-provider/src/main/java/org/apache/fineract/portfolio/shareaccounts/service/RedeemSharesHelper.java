/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 23 April 2023 at 13:56
 */
package org.apache.fineract.portfolio.shareaccounts.service;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.account.domain.AccountTransferAssembler;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetailRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccountTransaction;

import java.math.BigDecimal;
import java.util.*;

import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedeemSharesHelper {

    private SavingsAccountDomainService savingsAccountDomainService;
    private AccountTransferAssembler accountTransferAssembler;
    private AccountTransferDetailRepository accountTransferDetailRepository;
    private FromJsonHelper fromJsonHelper;
    private PaymentDetailRepository paymentDetailRepository;

    @Autowired
    public RedeemSharesHelper(SavingsAccountDomainService savingsAccountDomainService, AccountTransferAssembler accountTransferAssembler, AccountTransferDetailRepository accountTransferDetailRepository, FromJsonHelper fromJsonHelper) {
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.accountTransferAssembler = accountTransferAssembler;
        this.accountTransferDetailRepository = accountTransferDetailRepository;
        this.fromJsonHelper = fromJsonHelper;
    }

    public void transferToSavingsAccount(ShareAccount shareAccount , ShareAccountTransaction shareAccountTransaction ,JsonCommand command){

        SavingsAccount savingsAccount = shareAccount.getSavingsAccount();
        BigDecimal transactionAmount = shareAccountTransaction.amount();
        Date date = shareAccountTransaction.getPurchasedDate();
        LocalDate transactionDate =  DateUtils.toLocalDate(date);

        String transferDate = command.stringValueOfParameterNamed("closedDate");

        //String payload = command.json();
        String payload = JsonHelper.update(command.json() ,"transferDate" ,transferDate);
        payload = JsonHelper.update(payload ,"transferAmount" ,transactionAmount);

        command = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

        System.err.println("-----------update this payload "+command);

        System.err.println("------------------------amount is "+transactionAmount+"-----------is null ---"+ OptionalHelper.isNull(savingsAccount));

        final Locale locale = command.extractLocale();

        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        String description = String.format("Share account closure ,redeemed shares from account %s" ,shareAccount.getAccountNumber());

        final  SavingsAccountTransaction deposit = savingsAccountDomainService.handleDepositLite(savingsAccount ,transactionDate ,transactionAmount ,description);

        System.err.println("------------------------------deposit done -----------"+deposit.getId()+"===========for amoiunt "+transactionAmount);

        System.err.println("--------------------share account transaction data is "+shareAccountTransaction.getId());

        final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleShareToSavingsTransfer(command,
                savingsAccount, deposit , shareAccount,shareAccountTransaction);

        this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);

    }

}
