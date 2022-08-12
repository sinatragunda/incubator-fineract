/*

    Created by Sinatra Gunda
    At 12:08 AM on 8/12/2022

*/
package org.apache.fineract.portfolio.savings.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.savings.enumerations.SAVINGS_TRANSACTION_TRIGGER_TYPE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="m_savings_transactions_trigger")
public class SavingsTransactionTrigger extends AbstractPersistableCustom<Long>{

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = true)
    private SavingsAccountTransaction savingsAccountTransaction ;

    @Column(name="trigger_id" ,nullable=false)
    private Long triggerId ;

    @Column(name="trigger_type" ,nullable=false)
    private SAVINGS_TRANSACTION_TRIGGER_TYPE triggerType ;

    private SavingsTransactionTrigger(){}

    public SavingsTransactionTrigger(Long triggerId, SAVINGS_TRANSACTION_TRIGGER_TYPE triggerType) {
        this.triggerId = triggerId;
        this.triggerType = triggerType;
    }

    public SavingsTransactionTrigger(SavingsAccountTransaction savingsAccountTransaction , Long triggerId, SAVINGS_TRANSACTION_TRIGGER_TYPE triggerType) {
        this.triggerId = triggerId;
        this.triggerType = triggerType;
        this.savingsAccountTransaction = savingsAccountTransaction;
    }

    public Long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Long triggerId) {
        this.triggerId = triggerId;
    }

    public SAVINGS_TRANSACTION_TRIGGER_TYPE getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(SAVINGS_TRANSACTION_TRIGGER_TYPE triggerType) {
        this.triggerType = triggerType;
    }

    public SavingsAccountTransaction getSavingsAccountTransaction() {
        return savingsAccountTransaction;
    }

    public void setSavingsAccountTransaction(SavingsAccountTransaction savingsAccountTransaction) {
        this.savingsAccountTransaction = savingsAccountTransaction;
    }
}
