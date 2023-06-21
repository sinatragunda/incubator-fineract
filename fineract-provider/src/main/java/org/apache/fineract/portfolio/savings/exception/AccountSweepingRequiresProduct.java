/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 18 June 2023 at 17:12
 */
package org.apache.fineract.portfolio.savings.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AccountSweepingRequiresProduct extends AbstractPlatformDomainRuleException {


    public AccountSweepingRequiresProduct(){
        super("Account sweeping requires a product to be mapped in the payment rule record configuraion" ,"Account sweeping requires a product to be mapped in the payment rule record configuraion");
    }
}
