/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 23 March 2023 at 10:02
 */
package org.apache.fineract.portfolio.savings.data;

import org.apache.fineract.portfolio.paymentrules.data.PaymentRuleData;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;

import java.util.List;

public class SavingsProductPropertiesData {

    private Long id ;
    private Long paymentRuleId;
    private String paymentRuleName ;
    private List<PaymentRuleData> paymentRuleDataList;
    private SavingsProductPropertiesData template ;

    public SavingsProductPropertiesData(List<PaymentRuleData> paymentRuleDataList) {
        this.paymentRuleDataList = paymentRuleDataList;
    }

    public SavingsProductPropertiesData(Long id, Long paymentRuleId ,String paymentRuleName) {
        this.id = id;
        this.paymentRuleId = paymentRuleId;
        this.paymentRuleName = paymentRuleName;
    }

    public void setTemplate(SavingsProductPropertiesData template) {
        this.template = template;
    }
}
