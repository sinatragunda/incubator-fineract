/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 November 2022 at 18:44
 */
package org.apache.fineract.organisation.teller.helper;

import org.apache.fineract.organisation.teller.domain.CashTransaction;
import org.apache.fineract.organisation.teller.domain.CashierTxnType;
import org.apache.fineract.organisation.teller.service.TellerWritePlatformService;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.joda.time.LocalDate ;

public class CashTransactionLoggingHelper {

    public static void savingsAccountTransaction(final TellerWritePlatformService tellerWritePlatformService , SavingsAccountTransaction savingsAccountTransaction){

        System.err.println("-----does savings acccount exist ?   "+Optional.ofNullable(savingsAccountTransaction).isPresent());

        System.err.println("-----does this value exist ?  "+Optional.ofNullable(savingsAccountTransaction.getPaymentDetail().getPaymentType()).isPresent());

        PaymentType paymentType = savingsAccountTransaction.getPaymentDetail().getPaymentType();

        boolean isCashPayment = paymentType.isCashPayment();

        boolean isDeposit = savingsAccountTransaction.isDeposit();

        if(isCashPayment){

            Long entityId = savingsAccountTransaction.getId();
            Date transactionDate = savingsAccountTransaction.getTransactionDate();
            BigDecimal amount = savingsAccountTransaction.getAmount();
            CashierTxnType cashierTxnType = CashierTxnType.OUTWARD_CASH_TXN;
            if(isDeposit){
                cashierTxnType = CashierTxnType.INWARD_CASH_TXN;
            }

            String note = "Testing Rx Deal Demo";
            String currencyCode = savingsAccountTransaction.getSavingsAccount().getCurrency().getCode();

            CashTransaction cashTransaction = new CashTransaction(entityId , PRODUCT_TYPE.SAVINGS ,cashierTxnType,transactionDate ,amount ,note ,currencyCode);
            System.err.println("-----------where the f is staffId needed now son ? ");
            tellerWritePlatformService.cashierTransaction(null ,cashTransaction);
        }
    }
}
