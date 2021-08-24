/*

    Created by Sinatra Gunda
    At 10:37 AM on 8/24/2021

*/
package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class NonAttachedLoanFactorAccountException extends AbstractPlatformDomainRuleException {

    public NonAttachedLoanFactorAccountException(final String errorCode, final String errorMessage, final Object... defaultUserMessageArgs) {
            super(errorCode, errorMessage, defaultUserMessageArgs);

    }

}
