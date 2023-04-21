/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 March 2023 at 12:26
 */
package org.apache.fineract.portfolio.paymentrules.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentSequence;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_DIRECTION;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountData;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;
import org.apache.fineract.portfolio.shareproducts.service.ShareProductReadPlatformServiceImpl;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.EnumeratedData;
import org.apache.fineract.utility.service.IEnum;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentRuleData {

    private Long id ;
    private String name ;
    private String officeName ;
    private Long officeId ;
    private Boolean active ;

    private PAYMENT_DIRECTION paymentDirection;
    private List<PaymentSequenceData> paymentSequenceDataList;
    private List<EnumOptionData> paymentCodesOptions ;
    private List<SavingsProductData> savingsProductData;
    private List<LoanProductData> loanProductData;
    private List<ShareAccountData> shareAccountData;
    private Map<String ,List<EnumOptionData>> paymentSequenceParams;
    private  List<EnumOptionData> paymentDirectionOptions ;
    private EnumOptionData paymentDirectionData ;

    public static PaymentRuleData template(){
        List paymentCodes = EnumTemplateHelper.template(PAYMENT_CODE.values());
        return new PaymentRuleData(paymentCodes);
    }

    public static PaymentRuleData template(LoanProductReadPlatformService loanProductReadPlatformService , SavingsProductReadPlatformService savingsProductReadPlatformService , ShareProductReadPlatformServiceImpl shareProductReadPlatformService){

        Map<String ,List<EnumOptionData>> params = new HashMap<>();
        Collection<? extends EnumeratedData> enumeratedDataCollection = null ;

        for (PAYMENT_CODE paymentCode: PAYMENT_CODE.values()) {

            switch (paymentCode.getRefTable()){
                case LOAN:
                    enumeratedDataCollection = (Collection<? extends EnumeratedData>) loanProductReadPlatformService.retrieveAllLoanProducts();
                    break;
                case ACCOUNT:
                    enumeratedDataCollection = (Collection<? extends EnumeratedData>) savingsProductReadPlatformService.retrieveAll();
                    break;
                case SHARE:
                    enumeratedDataCollection = (Collection<? extends EnumeratedData>) shareProductReadPlatformService.retrieveAllShareProducts();
                    break ;

            }
            List<EnumeratedData> enumeratedDataList = enumeratedDataCollection.stream().collect(Collectors.toList());
            List<EnumOptionData> enumOptionDataList = EnumTemplateHelper.fromEnumeratedList(enumeratedDataList);
            params.put(paymentCode.getCode() ,enumOptionDataList);
        }
        List paymentCodesOptions = EnumTemplateHelper.template(PAYMENT_CODE.values());
        List paymentDirectionOptions = EnumTemplateHelper.template(PAYMENT_DIRECTION.values());

        return new PaymentRuleData(params ,paymentDirectionOptions ,paymentCodesOptions);
    }

    public PaymentRuleData(Map<String, List<EnumOptionData>> paymentSequenceParams ,List<EnumOptionData> paymentDirectionOptions ,List paymentCodesOptions) {
        this.paymentSequenceParams = paymentSequenceParams;
        this.paymentDirectionOptions  = paymentDirectionOptions;
        this.paymentCodesOptions = paymentCodesOptions;
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

    public PaymentRuleData(Long id, String name, String officeName, Long officeId ,boolean active ,PAYMENT_DIRECTION paymentDirection) {
        this.id = id;
        this.name = name;
        this.officeName = officeName;
        this.officeId = officeId;
        this.paymentDirection = paymentDirection;
        this.active = active;
        this.paymentDirectionData = EnumTemplateHelper.template(paymentDirection);
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
