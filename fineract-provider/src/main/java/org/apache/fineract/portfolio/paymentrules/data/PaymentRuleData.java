/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 March 2023 at 12:26
 */
package org.apache.fineract.portfolio.paymentrules.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentSequence;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountData;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;

import java.util.List;

public class PaymentRuleData {

    private Long id ;
    private String name ;
    private String officeName ;
    private Long officeId ;
    private List<PaymentSequenceData> paymentSequenceDataList;
    private List<EnumOptionData> paymentCodesOptions ;
    private List<SavingsProductData> savingsProductData;
    private List<LoanProductData> loanProductData;
    private List<ShareAccountData> shareAccountData;

    public static PaymentRuleData template(){
        List paymentCodes = EnumTemplateHelper.template(PAYMENT_CODE.values());
        return new PaymentRuleData(paymentCodes);
    }

    public PaymentRuleData(List<EnumOptionData> paymentCodesOptions) {
        this.paymentCodesOptions = paymentCodesOptions;
    }

    public PaymentRuleData(Long id, String name, String officeName, Long officeId, List<PaymentSequenceData> paymentSequenceDataList, List<EnumOptionData> paymentCodesOptions, List<SavingsProductData> savingsProductData, List<LoanProductData> loanProductData, List<ShareAccountData> shareAccountData) {
        this.id = id;
        this.name = name;
        this.officeName = officeName;
        this.officeId = officeId;
        this.paymentSequenceDataList = paymentSequenceDataList;
        this.paymentCodesOptions = paymentCodesOptions;
        this.savingsProductData = savingsProductData;
        this.loanProductData = loanProductData;
        this.shareAccountData = shareAccountData;
    }

    public PaymentRuleData(Long id, String name, String officeName, Long officeId) {
        this.id = id;
        this.name = name;
        this.officeName = officeName;
        this.officeId = officeId;
    }

    public void setPaymentSequenceDataList(List<PaymentSequenceData> paymentSequenceDataList) {
        this.paymentSequenceDataList = paymentSequenceDataList;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOfficeName() {
        return officeName;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public List<PaymentSequenceData> getPaymentSequenceDataList() {
        return paymentSequenceDataList;
    }

    public List<EnumOptionData> getPaymentCodesOptions() {
        return paymentCodesOptions;
    }

    public List<SavingsProductData> getSavingsProductData() {
        return savingsProductData;
    }

    public List<LoanProductData> getLoanProductData() {
        return loanProductData;
    }

    public List<ShareAccountData> getShareAccountData() {
        return shareAccountData;
    }
}
