/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 24 April 2023 at 03:49
 */
package org.apache.fineract.portfolio.paymentrules.helper;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentSequence;
import org.apache.fineract.utility.service.ServiceWrapper;

import java.util.ArrayList;
import java.util.Collection;

public class LoanRepaymentsPaymentRule {

    private ServiceWrapper serviceWrapper ;
    private PaymentRule paymentRule;
    private Client client;
    private PaymentSequence paymentSequence;

    public void handle(){

        String param = paymentSequence.getValue();
        boolean has = OptionalHelper.isPresent(param);
        LoanReadPlatformService loanReadPlatformService = serviceWrapper.getLoanReadPlatformService();
        Long clientId = client.getId();
        Collection clientLoans = new ArrayList();
        if(has){
            try {
                Long loanProductId = Long.valueOf(param);
                clientLoans = loanReadPlatformService.retrieveAllForClientAndProduct(clientId ,loanProductId);
            }
            catch (NumberFormatException n){

            }
        }
        else{
            clientLoans = loanReadPlatformService.retrieveAllForClient(clientId);
        }

        LoanWritePlatformService loanWritePlatformService;

        //loanWritePlatformService


    }
}
