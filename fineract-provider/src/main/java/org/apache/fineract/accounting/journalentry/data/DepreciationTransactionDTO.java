/*

    Created by Sinatra Gunda
    At 5:14 AM on 9/18/2021

*/
package org.apache.fineract.accounting.journalentry.data;

import java.math.BigDecimal;

import java.util.Date;

public class DepreciationTransactionDTO {

    private final String transactionId;
    private final Date transactionDate;
    private final Long paymentTypeId;

    private final BigDecimal amount;

    /*** Boolean values determines if the transaction is reversed ***/
    private final boolean reversed;

    public DepreciationTransactionDTO(String transactionId, Date transactionDate, Long paymentTypeId, BigDecimal amount, boolean reversed) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.paymentTypeId = paymentTypeId;
        this.amount = amount;
        this.reversed = reversed;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public Long getPaymentTypeId() {
        return paymentTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isReversed() {
        return reversed;
    }
}
