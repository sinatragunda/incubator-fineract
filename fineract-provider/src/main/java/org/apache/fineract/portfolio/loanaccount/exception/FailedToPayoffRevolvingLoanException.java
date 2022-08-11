/*

    Created by Sinatra Gunda
    At 7:41 AM on 8/2/2022

*/
package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class FailedToPayoffRevolvingLoanException extends AbstractPlatformDomainRuleException {

    public FailedToPayoffRevolvingLoanException(final Long loanId){
        super("Loan repayment transaction failed ,unable to pay off revolving loan", "Loan repayment transaction failed ,unable to pay off revolving loan",loanId);
    }

}
