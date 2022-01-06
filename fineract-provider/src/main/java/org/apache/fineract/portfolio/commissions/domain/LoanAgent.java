/*

    Created by Sinatra Gunda
    At 12:03 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;


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
@Table(name="m_loan_agent")
public class LoanAgent extends AbstractPersistableCustom<Long>{

    @Column(name ="client_id" ,nullable = false)
    @OneToOne
    private Client client ;

    @Column(name ="savings_account_id" ,nullable = false)
    @ManyToOne
    private SavingsAccount savingsAccount;


    public LoanAgent(){}


    public LoanAgent(Client client, SavingsAccount savingsAccount) {
        this.client = client;
        this.savingsAccount = savingsAccount;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public SavingsAccount getSavingsAccount() {
        return savingsAccount;
    }

    public void setSavingsAccount(SavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }
}
