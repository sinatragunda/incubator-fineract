/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 March 2023 at 12:22
 */
package org.apache.fineract.portfolio.paymentrules.service;

import org.apache.fineract.portfolio.paymentrules.data.PaymentRuleData;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentSequence;

import java.util.List;

public interface PaymentRulesReadService {

    public PaymentRuleData template();
    public PaymentRuleData retrieveOne(Long id);
    public List<PaymentRuleData> retrieveAll(Long officeId ,boolean hasOffice);

}
