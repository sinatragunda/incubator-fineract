/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 16 June 2023 at 12:11
 */
package org.apache.fineract.portfolio.savings.helper;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.paymentrules.helper.SavingsPaymentRuleHelper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;

import java.util.Map;

public class OnSavingsDepositEventListenerPaymentRules implements BusinessEventListner{

    private SavingsPaymentRuleHelper savingsPaymentRuleHelper;

    public OnSavingsDepositEventListenerPaymentRules(SavingsPaymentRuleHelper savingsPaymentRuleHelper) {
        this.savingsPaymentRuleHelper = savingsPaymentRuleHelper;
    }

    @Override
    public void businessEventToBeExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {

    }

    @Override
    public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
        //System.err.println("-----------event now up for execution --------");
        SavingsAccountTransaction savingsAccountTransaction = (SavingsAccountTransaction) businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.SAVINGS_TRANSACTION);
        
        Boolean has = OptionalHelper.has(savingsAccountTransaction);

        //System.err.println("--------------has savings account transaction ? "+has);
        if(has){
            savingsPaymentRuleHelper.handlePayment(savingsAccountTransaction);
        }
    }
}
