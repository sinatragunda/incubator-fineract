/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 March 2023 at 09"33
 */
package org.apache.fineract.portfolio.savings.domain;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.repo.PaymentRuleRepository;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.domain.SavingsProduct;
import org.apache.fineract.portfolio.tax.domain.TaxGroup;

import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name="m_savings_product_properties")
public class SavingsProductProperties extends AbstractPersistableCustom<Long> {

    @ManyToOne
    @Column(name ="payment_rule_id")
    private PaymentRule paymentRule;

    @OneToOne
    @Column(name ="savings_product_id")
    private SavingsProduct savingsProduct;

    protected SavingsProductProperties(){}

    public SavingsProductProperties(SavingsProduct savingsProduct ,PaymentRule paymentRule) {
        this.paymentRule = paymentRule;
        this.savingsProduct = savingsProduct;
    }

    public static SavingsProductProperties fromJson(final JsonCommand command) {
        final Long paymentRuleId = command.longValueOfParameterNamed(SavingsApiConstants.paymentRuleIdParam);
        return null ;
    }

    public static SavingsProductProperties fromJson(final JsonCommand command , PaymentRuleRepository paymentRuleRepository) {

        final Long paymentRuleId = command.longValueOfParameterNamed(SavingsApiConstants.paymentRuleIdParam);
        final PaymentRule paymentRule = paymentRuleRepository.findOne(paymentRuleId);
        
        return new SavingsProductProperties(null,paymentRule);
    }

    public Map<String, Object> update(JsonCommand command){

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        boolean hasPaymentRule = OptionalHelper.isPresent(paymentRule);

        if(hasPaymentRule) {
            if (command.isChangeInLongParameterNamed(SavingsApiConstants.paymentRuleIdParam, paymentRule.getId())) {
                final Long newValue = command.longValueOfParameterNamed(SavingsApiConstants.paymentRuleIdParam);
                actualChanges.put(SavingsApiConstants.paymentRuleIdParam, newValue);
                //this.paymentRule = new PaymentRule(newValue);
            }
        }
        else{
            if(command.hasParameter(SavingsApiConstants.paymentRuleIdParam)){
                final Long newValue = command.longValueOfParameterNamed(SavingsApiConstants.paymentRuleIdParam);
                actualChanges.put(SavingsApiConstants.paymentRuleIdParam, newValue);
            }
        }

        return actualChanges;
    }

    public PaymentRule paymentRule() {
        return this.paymentRule;
    }

    public void setPaymentRule(PaymentRule paymentRule) {
        this.paymentRule = paymentRule;
    }
}
