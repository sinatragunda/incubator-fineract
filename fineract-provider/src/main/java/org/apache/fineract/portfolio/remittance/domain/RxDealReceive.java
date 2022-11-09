package org.apache.fineract.portfolio.remittance.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.remittance.enumerations.RX_DEAL_STATUS;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.joda.time.LocalDate ;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.*;

@Entity
@Table(name="m_rx_deal_receive")
public class RxDealReceive extends AbstractPersistableCustom<Long> {

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name="rx_deal_id")
    private RxDeal rxDeal ;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name="savings_account_transaction_id")
    private SavingsAccountTransaction savingsAccountTransaction;

    @Column(name="name")
    private String name ;

    @Column(name="phone_number")
    private String phoneNumber ;

    @Column(name="email_address")
    private String emailAddress ;

    @Column(name="transaction_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate ;
    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name="office_id")
    private Office office;

    @Column(name="status")
    @Enumerated(EnumType.ORDINAL)
    private RX_DEAL_STATUS status;

    @Column(name="amount")
    private BigDecimal amount;

    @Column(name="total_charges")
    private BigDecimal charges;


    public RxDealReceive(RxDeal rxDeal, SavingsAccountTransaction savingsAccountTransaction, String name, String phoneNumber, String emailAddress ,LocalDate transactionDate , Office office , RX_DEAL_STATUS rxDealStatus ,BigDecimal amount ,BigDecimal charges) {
        this.rxDeal = rxDeal;
        this.savingsAccountTransaction = savingsAccountTransaction;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.transactionDate = DateUtils.fromLocalDate(transactionDate);
        this.office = office ;
        this.status = status ;
        this.emailAddress = emailAddress;
        this.amount = amount ;
        this.charges = charges;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCharges() {
        return charges;
    }

    public void setCharges(BigDecimal charges) {
        this.charges = charges;
    }

    public void setStatus(RX_DEAL_STATUS status) {
        this.status = status;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setRxDeal(RxDeal rxDeal) {
        this.rxDeal = rxDeal;
    }

    public void setSavingsAccountTransaction(SavingsAccountTransaction savingsAccountTransaction) {
        this.savingsAccountTransaction = savingsAccountTransaction;
    }

    public Office getOffice() {
        return office;
    }

    public RX_DEAL_STATUS getStatus() {
        return status;
    }

    public RxDeal getRxDeal() {
        return rxDeal;
    }

    public SavingsAccountTransaction getSavingsAccountTransaction() {
        return savingsAccountTransaction;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }
}
