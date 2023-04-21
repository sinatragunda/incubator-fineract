/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 April 2023 at 07:28
 */
package org.apache.fineract.portfolio.paymentrules.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;
import org.apache.fineract.utility.helper.StringConstants;

public class PaymentRuleNotFoundException extends AbstractPlatformResourceNotFoundException {


    public PaymentRuleNotFoundException(Long id) {
        super(StringConstants.resourceNotFound, String.format("Payment rule with id %d not found " ,id), id);
    }
}
