/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 16 February 2023 at 02:10
 */
package org.apache.fineract.portfolio.charge.domain;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.tax.domain.TaxGroup;

import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="m_charge_properties")
public class ChargeProperties extends AbstractPersistableCustom<Long> {

    @Column(name ="commissioned_charge")
    private boolean commissionedCharge = false;

    protected ChargeProperties(){}

    public ChargeProperties(boolean commissionedCharge) {
        this.commissionedCharge = commissionedCharge;
    }

    public static ChargeProperties fromJson(final JsonCommand command) {

        final boolean isCommissionedCharge = command.booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.commissionedChargeParam);
        return new ChargeProperties(isCommissionedCharge);
    }

    public boolean isCommissionedCharge() {
        return commissionedCharge;
    }
}
