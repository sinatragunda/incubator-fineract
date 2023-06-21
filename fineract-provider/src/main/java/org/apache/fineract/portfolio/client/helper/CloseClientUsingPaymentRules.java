/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 June 2023 at 07:18
 */
package org.apache.fineract.portfolio.client.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.paymentrules.api.PaymentRulesConstants;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_DIRECTION;
import org.apache.fineract.portfolio.paymentrules.exceptions.PaymentDirectionValidateException;
import org.apache.fineract.portfolio.paymentrules.helper.CloseClientPayoutHelper;
import org.apache.fineract.portfolio.paymentrules.repo.PaymentRuleRepositoryWrapper;


import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
@Service
public class CloseClientUsingPaymentRules {

    private PaymentRuleRepositoryWrapper paymentRuleRepositoryWrapper;
    private CloseClientPayoutHelper closeClientPayoutHelper ;

    @Autowired
    public CloseClientUsingPaymentRules(PaymentRuleRepositoryWrapper paymentRuleRepositoryWrapper,CloseClientPayoutHelper closeClientPayoutHelper) {
        this.paymentRuleRepositoryWrapper = paymentRuleRepositoryWrapper;
        this.closeClientPayoutHelper = closeClientPayoutHelper;
    }

    public void closeClient(Client client , JsonCommand command ,LocalDate transactionDate){

        boolean has = command.hasParameter(PaymentRulesConstants.payoutRuleIdParam);

        if(has){
            Long id = command.longValueOfParameterNamed(PaymentRulesConstants.payoutRuleIdParam);

            System.err.println("--------------payout rule id is "+id);

            PaymentRule paymentRule = paymentRuleRepositoryWrapper.findWithNotFoundDetection(id);
            PAYMENT_DIRECTION paymentDirection = paymentRule.getPaymentDirection();
            valildatePaymentDirection(paymentDirection);
            closeClientPayoutHelper.closeClient(client ,paymentRule ,transactionDate);
        }
    }
    private static  void valildatePaymentDirection(PAYMENT_DIRECTION paymentDirection){
        if(paymentDirection==PAYMENT_DIRECTION.IN){
            throw new PaymentDirectionValidateException();
        }
    }
}
