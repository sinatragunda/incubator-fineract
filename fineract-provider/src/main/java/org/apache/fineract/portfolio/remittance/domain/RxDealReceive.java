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
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.*;

@Entity
@Table(name="m_rx_deal_receive")
public class RxDealReceive extends AbstractPersistableCustom<Long> {

    @JoinColumn(name="rx_deal_id")
    private RxDeal rxDeal ;

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

    @JoinColumn(name="office_id")
    private Office office;

    @Column(name="status")
    private RX_DEAL_STATUS status;

    public RxDealReceive(RxDeal rxDeal, SavingsAccountTransaction savingsAccountTransaction, String name, String phoneNumber, String emailAddress ,LocalDate transactionDate , Office office , RX_DEAL_STATUS rxDealStatus) {
        this.rxDeal = rxDeal;
        this.savingsAccountTransaction = savingsAccountTransaction;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.transactionDate = DateUtils.fromLocalDate(transactionDate);
        this.office = office ;
        this.status = status ;
        this.emailAddress = emailAddress;
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
