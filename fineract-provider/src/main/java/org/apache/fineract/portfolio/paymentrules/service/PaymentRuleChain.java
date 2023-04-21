/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 April 2023 at 01:32
 */
package org.apache.fineract.portfolio.paymentrules.service;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.utility.service.ServiceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRuleChain{

    private final ServiceWrapper serviceWrapper;

    @Autowired
    public PaymentRuleChain(final ServiceWrapper serviceWrapper) {
        this.serviceWrapper = serviceWrapper;
    }

    public void pay(PaymentRule paymentRule , Client client){
        System.err.println("------------------------pay a payment rule ,question who will provide the payment options ? ");
    }

    private void constructChain(){

        System.err.println("-------------You will fucken construct payment rules here -----------------------");
    }
}
