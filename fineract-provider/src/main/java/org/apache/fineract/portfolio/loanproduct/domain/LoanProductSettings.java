/*

    Created by Sinatra Gunda
    At 1:57 PM on 5/30/2022

*/
package org.apache.fineract.portfolio.loanproduct.domain;

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
@Table( name ="m_loan_product_settings")
public class LoanProductSettings extends AbstractPersistableCustom<Long>{

    /// gets settlement product id ,used for settling accounts
    @Column(name="settlement_account_id")
    private Long settlementAccountId ;

    @OneToOne
    @JoinColumn(name="loan_product_id" ,nullable=false)
    private LoanProduct loanProduct ;


    public LoanProductSettings(){}

    public LoanProductSettings(LoanProduct loanProduct, Long settlementAccountId){
        this.settlementAccountId = settlementAccountId;
        this.loanProduct = loanProduct ;
    }

    public Long getSettlementAccountId(){
        return this.settlementAccountId ;
    }


}
