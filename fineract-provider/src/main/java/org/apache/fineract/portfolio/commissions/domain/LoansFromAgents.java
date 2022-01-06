/*

    Created by Sinatra Gunda
    At 10:35 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


@Entity
@Table(name="m_loans_from_agents")
public class LoansFromAgents extends AbstractPersistableCustom<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = true)
    private Loan loan ;

    @ManyToOne
    @JoinColumn(name = "loan_agent_id", nullable = true)
    private LoanAgent loanAgent ;

    public LoansFromAgents(){}

    protected LoansFromAgents(Long id){
        setId(id);
    }

    public LoansFromAgents(Loan loan ,LoanAgent loanAgent){
        this.loanAgent = loanAgent ;
        this.loan = loan ;
    }

    public static LoansFromAgents fromId(Long id){
        return new LoansFromAgents(id);
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public LoanAgent getLoanAgent() {
        return loanAgent;
    }

    public void setLoanAgent(LoanAgent loanAgent) {
        this.loanAgent = loanAgent;
    }

}
