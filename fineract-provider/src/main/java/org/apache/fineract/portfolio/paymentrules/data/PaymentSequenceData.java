/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 March 2023 at 11:55
 */
package org.apache.fineract.portfolio.paymentrules.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;

import java.util.List;

public class PaymentSequenceData {

    private Long id ;
    private Integer sequenceNumber;
    private String value ;
    private REF_TABLE refTable ;
    private PAYMENT_CODE paymentCode;
    private String paymentCodeValue;

    private List<EnumOptionData> paramValues ;

    public PaymentSequenceData(Long id, Integer sequenceNumber, String value, REF_TABLE refTable, PAYMENT_CODE paymentCode) {
        this.id = id;
        this.sequenceNumber = sequenceNumber;
        this.value = value;
        this.refTable = refTable;
        this.paymentCode = paymentCode;
        this.paymentCodeValue = paymentCode.getCode();
    }

    public Long getId() {
        return id;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public String getValue() {
        return value;
    }

    public REF_TABLE getRefTable() {
        return refTable;
    }

    public PAYMENT_CODE getPaymentCode() {
        return paymentCode;
    }
}
