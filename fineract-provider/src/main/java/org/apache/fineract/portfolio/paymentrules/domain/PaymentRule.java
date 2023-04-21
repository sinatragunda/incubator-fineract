/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 March 2023 at 10:32
 */
package org.apache.fineract.portfolio.paymentrules.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_DIRECTION;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.*;


@Entity
@Table(name="m_payment_rule")
public class PaymentRule extends AbstractPersistableCustom<Long>{

    @Column(name="name")
    private String name ;


    @Column(name="active")
    private Boolean active ;


    @Enumerated(EnumType.ORDINAL)
    @Column(name="payment_direction")
    private PAYMENT_DIRECTION paymentDirection;


    @ManyToOne
    @JoinColumn(name="office_id")
    private Office office;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentRule", orphanRemoval = true)
    private Set<PaymentSequence> paymentSequenceSet;

    protected PaymentRule() {}

    public PaymentRule(Set<PaymentSequence> paymentSequenceSet) {
        this.paymentSequenceSet = paymentSequenceSet;
    }

    public PaymentRule(String name ,Office office ,Set<PaymentSequence> paymentSequenceSet ,Boolean active ,PAYMENT_DIRECTION paymentDirection){
        this.name = name ;
        this.office = office ;
        this.paymentSequenceSet = paymentSequenceSet;
        this.active = active;
        this.paymentDirection = paymentDirection;
    }


    public void setPaymentSequenceSet(Set<PaymentSequence> paymentSequenceSet) {
        this.paymentSequenceSet = paymentSequenceSet;
    }

    public Set<PaymentSequence> getPaymentRuleSequence(){

        Comparator<PaymentSequence> comparator = (e , e1)-> e.getSequenceNumber().compareTo(e1.getSequenceNumber()) ;
        paymentSequenceSet.stream().sorted(comparator);
        return paymentSequenceSet;
    }

    public PAYMENT_DIRECTION getPaymentDirection() {
        return paymentDirection;
    }
}
