/*

    Created by Sinatra Gunda
    At 12:21 AM on 8/12/2022

*/
package org.apache.fineract.portfolio.savings.helper;


import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsTransactionTrigger;
import org.apache.fineract.portfolio.savings.repo.SavingsTransactionTriggerRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

// this intention to keep an id of an event that triggered other events etc
public class SavingsTransactionsTriggerHelper {

    public static void trigger(SavingsTransactionTriggerRepository transactionTriggerRepository , SavingsAccountTransaction savingsAccountTransaction , SavingsTransactionTrigger savingsTransactionTrigger){

        Optional.ofNullable(savingsAccountTransaction).ifPresent(e->{
            savingsTransactionTrigger.setSavingsAccountTransaction(e);
        });

        transactionTriggerRepository.save(savingsTransactionTrigger);

    }
}
