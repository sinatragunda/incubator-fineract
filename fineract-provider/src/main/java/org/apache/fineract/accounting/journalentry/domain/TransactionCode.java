/*

    Created by Sinatra Gunda
    At 11:59 AM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.domain;

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.LinkedHashMap;
import java.util.Map;


@Entity
@Table(name="m_transaction_code")
public class TransactionCode extends AbstractPersistableCustom<Long>{

    @Column(name="code", nullable = false)
    private Long code = null;

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

    public Map<String, Object> update(final JsonCommand command , GLAccountRepositoryWrapper glAccountRepositoryWrapper) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        if(command.isChangeInStringParameterNamed("name", this.name)) {
            final String newValue = command.stringValueOfParameterNamed("name");
            actualChanges.put("name", newValue);
            this.name = newValue;
        }
        if(command.isChangeInLongParameterNamed("code", this.code)) {
            final Long newValue = command.longValueOfParameterNamed("code");
            actualChanges.put("code", newValue);
            this.code = newValue;
        }

        final String debitAccountIdParam = "debitAccountId";
        if (command.isChangeInLongParameterNamed(debitAccountIdParam,debitAccount.getId())){
            final Long id = command.longValueOfParameterNamed(debitAccountIdParam);
            final GLAccount glAccount = glAccountRepositoryWrapper.findOneWithNotFoundDetection(id);
            this.debitAccount = glAccount ;      
        }

        final String creditAccountIdParam = "creditAccountId";
        if (command.isChangeInLongParameterNamed(creditAccountIdParam,creditAccount.getId())){
            final Long id = command.longValueOfParameterNamed(creditAccountIdParam);
            final GLAccount glAccount = glAccountRepositoryWrapper.findOneWithNotFoundDetection(id);
            this.creditAccount = glAccount ;
        }
        return actualChanges;
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
