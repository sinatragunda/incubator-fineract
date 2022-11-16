/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 November 2022 at 07:02
 */
package org.apache.fineract.portfolio.remittance.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class RxDealStatusNotOpenedException extends AbstractPlatformDomainRuleException {


    public RxDealStatusNotOpenedException(){
        super("Only Rx Deals whose status is open can be remitted out " ,"Only Rx Deals whose status is open can be remitted out " ,1L);
    }

}
