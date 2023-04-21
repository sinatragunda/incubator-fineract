/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 April 2023 at 07:26
 */
package org.apache.fineract.portfolio.paymentrules.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.exceptions.PaymentRuleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRuleRepositoryWrapper {

    private PaymentRuleRepository writeRepository ;

    @Autowired
    public PaymentRuleRepositoryWrapper(PaymentRuleRepository writeRepository) {
        this.writeRepository = writeRepository;
    }

    public PaymentRule findWithNotFoundDetection(Long id){

        PaymentRule paymentRule = writeRepository.findOne(id);
        boolean has = OptionalHelper.isPresent(paymentRule);
        if(!has){
            throw new PaymentRuleNotFoundException(id);
        }
        return paymentRule;
    }

    public PaymentRule save(PaymentRule paymentRule){
        writeRepository.saveAndFlush(paymentRule);
        return paymentRule;
    }
}
