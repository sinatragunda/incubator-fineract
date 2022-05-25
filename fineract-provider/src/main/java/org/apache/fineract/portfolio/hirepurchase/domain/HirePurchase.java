/*

    Created by Sinatra Gunda
    At 11:54 AM on 5/25/2022

*/
package org.apache.fineract.portfolio.hirepurchase.domain;

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

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;

@Entity
@Table(name="m_hire_purchase")
public class HirePurchase extends AbstractPersistableCustom<Long>{


    @Column(name="name")
    private String itemName ;

    @OneToOne
    @JoinColumn(name="loan_id" ,nullable=false)
    private Loan loan ;

    public HirePurchase(){}

    public HirePurchase(Loan loan ,String itemName) {
        this.itemName = itemName;
        this.loan = loan;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}
