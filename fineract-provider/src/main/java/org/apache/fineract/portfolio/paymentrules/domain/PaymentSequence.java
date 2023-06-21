/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 March 2023 at 12:12
 */
package org.apache.fineract.portfolio.paymentrules.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.*;
import javax.persistence.FetchType;


import org.apache.commons.lang.StringUtils;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;

@Entity
@Table(name ="m_payment_rule_sequence")
public class PaymentSequence extends AbstractPersistableCustom<Long> {

    @Column(name="value")
    private String value ;

    @Column(name="sequence_number" ,nullable=false)
    private Integer sequenceNumber ;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="ref_table")
    private REF_TABLE refTable;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="payment_code")
    private PAYMENT_CODE paymentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="payment_rule_id")
    private PaymentRule paymentRule;

    protected PaymentSequence(){}

    public PaymentSequence(REF_TABLE refTable, PAYMENT_CODE paymentCode, PaymentRule paymentRule ,String value ,Integer sequenceNumber) {
        this.value = value;
        this.refTable = paymentCode.getRefTable();
        this.paymentCode = paymentCode;
        this.paymentRule = paymentRule;
        this.sequenceNumber = sequenceNumber;
    }

    public String getValue() {
        return value;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public REF_TABLE getRefTable() {
        return refTable;
    }

    public PAYMENT_CODE getPaymentCode() {
        return paymentCode;
    }

    public PaymentRule getPaymentRule() {
        return paymentRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentSequence)) return false;
        PaymentSequence that = (PaymentSequence) o;
        return sequenceNumber.equals(that.sequenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceNumber);
    }
}