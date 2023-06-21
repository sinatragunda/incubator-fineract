/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 June 2023 at 10:48
 */
package org.apache.fineract.portfolio.savings.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class InsufficientAccountBalanceToPayOffLoan extends AbstractPlatformResourceNotFoundException {

    public InsufficientAccountBalanceToPayOffLoan(){
        super("Account has no sufficient balance to pay off loan fully" ,"Account has no sufficient balance to pay off loan fully");
    }
}
