package org.apache.fineract.organisation.teller.domain;

import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;


import java.math.BigDecimal;
import java.util.Date;

/**
 * Modified 05/11/2022 at 1829
 * Class modified to push cash transactions to vault ,ie cashier
 */
public class CashTransaction {
    private Long entityId ;
    private PRODUCT_TYPE productType;
    private CashierTxnType cashierTxnType;
    private Date transactionDate ;
    private BigDecimal amount ;
    private String note;

    private String currencyCode ;

    public CashTransaction(Long entityId, PRODUCT_TYPE productType, CashierTxnType cashierTxnType, Date transactionDate, BigDecimal amount, String note, String currencyCode) {
        this.entityId = entityId;
        this.productType = productType;
        this.cashierTxnType = cashierTxnType;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.note = note;
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Long getEntityId() {
        return entityId;
    }

    public PRODUCT_TYPE getProductType() {
        return productType;
    }

    public CashierTxnType getCashierTxnType() {
        return cashierTxnType;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }
}
