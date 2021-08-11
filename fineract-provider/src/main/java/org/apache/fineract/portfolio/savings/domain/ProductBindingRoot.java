/*

    Created by Sinatra Gunda
    At 9:45 AM on 8/2/2021

*/
package org.apache.fineract.portfolio.savings.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name ="m_product_binding")
public class ProductBindingRoot extends AbstractPersistableCustom<Long> {

    @Column(name="loan_factor")
    private int loanFactor ;


    public ProductBindingRoot(){}

    public int getLoanFactor() {
        return loanFactor;
    }

    public void setLoanFactor(int loanFactor) {
        this.loanFactor = loanFactor;
    }
}
