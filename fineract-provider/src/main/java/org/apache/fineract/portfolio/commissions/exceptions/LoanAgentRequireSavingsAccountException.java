/*

    Created by Sinatra Gunda
    At 3:10 PM on 6/15/2022

*/
package org.apache.fineract.portfolio.commissions.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class LoanAgentRequireSavingsAccountException extends AbstractPlatformDomainRuleException {

    public LoanAgentRequireSavingsAccountException(final String defaultUserMessage) {
        super("Loan Agent client requires a settlement account on creation", "Loan Agent client requires a settlement account on creation");
    }
}
