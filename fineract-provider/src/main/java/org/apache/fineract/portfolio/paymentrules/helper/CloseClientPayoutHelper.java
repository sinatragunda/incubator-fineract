/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 June 2023 at 07:32
 */
package org.apache.fineract.portfolio.paymentrules.helper;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentSequence;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;

import java.util.Set;
import java.util.function.Consumer;

import org.apache.fineract.portfolio.savings.helper.SavingsAccountHelper;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
@Service
public class CloseClientPayoutHelper {

    private SavingsPaymentRuleHelper savingsPaymentRuleHelper;
    private SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService;


    @Autowired
    public CloseClientPayoutHelper(SavingsPaymentRuleHelper savingsPaymentRuleHelper, SavingsAccountWritePlatformService savingsAccountWritePlatformService, SavingsAccountReadPlatformService savingsAccountReadPlatformService) {
        this.savingsPaymentRuleHelper = savingsPaymentRuleHelper;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
    }

    public void closeClient(Client client , PaymentRule paymentRule , LocalDate transactionDate){

        Set<PaymentSequence> paymentSequenceSet = paymentRule.getPaymentRuleSequence();

        Consumer<PaymentSequence> closeClientConsumer = (e)->{
            PAYMENT_CODE paymentCode = e.getPaymentCode();

            System.err.println("-----------sequence number is "+e.getSequenceNumber());

            switch (paymentCode){
                case SHARE_REDEEM:
                    savingsPaymentRuleHelper.redeemShares(client ,e ,transactionDate);
                    break;
                case LOAN_PAYOFF:
                    savingsPaymentRuleHelper.closeLoansWithPayoff(client ,e ,transactionDate);
                    break;
                case BALANCE_TRANSFER:
                    savingsPaymentRuleHelper.balanceTransfer(client ,e ,transactionDate);
                    break;
            }
        };

        paymentSequenceSet.stream().forEach(closeClientConsumer);

        SavingsAccountHelper.closeSavingsAccounts(client ,savingsAccountReadPlatformService ,savingsAccountWritePlatformService ,transactionDate);


    }
}
