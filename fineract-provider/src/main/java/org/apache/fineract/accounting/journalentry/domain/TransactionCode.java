/*

    Created by Sinatra Gunda
    At 11:59 AM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.domain;

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="m_transaction_code")
public class TransactionCode extends AbstractPersistableCustom<Long>{

    @Column(name="code", nullable = false)
    private Long code ;

    @Column(name="name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "debit_account_id", nullable = false)
    private GLAccount debitAccount;

    @ManyToOne
    @JoinColumn(name = "credit_account_id", nullable = false)
    private GLAccount creditAccount;

    public TransactionCode(){}

    public TransactionCode(Long code, String name, GLAccount debitAccount, GLAccount creditAccount) {
        this.code = code;
        this.name = name;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
    }

    public Long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public GLAccount getDebitAccount() {
        return debitAccount;
    }

    public GLAccount getCreditAccount() {
        return creditAccount;
    }
}
