/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 November 2022 at 06:23
 */
package org.apache.fineract.portfolio.remittance.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.*;

@Entity
@Table(name="m_rx_deal_transaction")
public class RxDealTransaction extends AbstractPersistableCustom<Long> {

    @ManyToOne(optional = true, fetch=FetchType.EAGER)
    @JoinColumn(name="rx_deal_id")
    private RxDeal rxDeal;

    public RxDealTransaction(RxDeal rxDeal) {
        this.rxDeal = rxDeal;
    }

    public RxDealTransaction(){}

    public void setRxDeal(RxDeal rxDeal) {
        this.rxDeal = rxDeal;
    }

    public RxDeal getRxDeal() {
        return rxDeal;
    }
}
