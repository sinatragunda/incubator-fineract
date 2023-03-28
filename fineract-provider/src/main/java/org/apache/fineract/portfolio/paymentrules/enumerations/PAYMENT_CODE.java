/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 March 2023 at 12:21
 */
package org.apache.fineract.portfolio.paymentrules.enumerations;

import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;

public enum PAYMENT_CODE implements IEnum {

    LOAN_EARLY_REPAYMENT("Loan Early Repayment" ,REF_TABLE.LOAN),
    LOAN_DUE_REPAYMENT("Loan Due Repayment" ,REF_TABLE.LOAN),
    LOAN_NPA_REPAYMENT("Loans NPA Repayment",REF_TABLE.LOAN),
    ACCOUNT_SWEEP("Account Sweep",REF_TABLE.ACCOUNT),
    PURCHASE_SHARES("Purchase Shares",REF_TABLE.SHARE);

    PAYMENT_CODE(String code , REF_TABLE refTable){
        this.code = code ;
        this.refTable = refTable;
    }

    private String code ;
    private REF_TABLE refTable;

    @Override
    public String getCode() {
        return code;
    }

    public REF_TABLE getRefTable() {
        return refTable;
    }

    public static PAYMENT_CODE fromInt(int  arg){
        return (PAYMENT_CODE) EnumTemplateHelper.fromInt(PAYMENT_CODE.values() ,arg);
    }
}
