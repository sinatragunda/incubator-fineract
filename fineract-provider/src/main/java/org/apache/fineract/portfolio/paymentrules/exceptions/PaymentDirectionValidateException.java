/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 June 2023 at 07:26
 */
package org.apache.fineract.portfolio.paymentrules.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentDirectionValidateException extends AbstractPlatformDomainRuleException {

    public PaymentDirectionValidateException(){
        super("Transaction requires a payout rule ,payment rule with direction OUT" ,"Transaction requires a payout rule ,payment rule with direction OUT");
    }
}
