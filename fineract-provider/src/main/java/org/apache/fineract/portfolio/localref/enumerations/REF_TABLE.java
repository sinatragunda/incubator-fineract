/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 15:24
 */
package org.apache.fineract.portfolio.localref.enumerations;

import com.wese.component.defaults.enumerations.CLASS_LOADER;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;

import java.util.List;

public enum REF_TABLE implements IEnum {
    
    HYBRID("Hybrid" ,null),
    CLIENT("Client" ,CLASS_LOADER.CLIENT),
    LOAN("Loan" ,CLASS_LOADER.LOAN),
    SHARE("Share",null),
    CHARGE("Charge" ,null),
    ACCOUNT("Account" ,CLASS_LOADER.ACCOUNT),
    MENU("Menu" ,null),
    MENU_ITEM("Menu Item" ,null),
    TRANSACTION_CODES("Transaction Code" ,null),
    TELLER("Teller" ,null),
    LOAN_TRANSACTION("Loan Transaction" ,CLASS_LOADER.LOAN_TRANSACTION),
    REPORT("Report" ,null),
    OFFICE("Office",null),
    SEARCH("Search" ,null),
    SCREEN("Screen",null),
    JOURNAL_ENTRY("Journal Entry" ,CLASS_LOADER.JOURNAL_ENTRY),
    GL_ACCOUNT("Ledger Account" ,CLASS_LOADER.GL_ACCOUNT),
    VERSION("Version",null),
    PAYMENT_RULE("Payment Rule" ,null),
    FUNCTION("Function" ,null);

    private String code;
    private CLASS_LOADER classLoader;

    REF_TABLE(String code , CLASS_LOADER classLoader){
        this.code = code ;
        this.classLoader = classLoader;
    }

    public CLASS_LOADER getClassLoader() {
        return classLoader;
    }

    public static List template(){
        return EnumTemplateHelper.template(REF_TABLE.values());
    }

    @Override
    public String getCode() {
        return code;
    }

    public static REF_TABLE fromInt(int arg){
        return (REF_TABLE) EnumTemplateHelper.fromInt(values() ,arg);
    }

    public EnumOptionData toEnumData(){
        return EnumTemplateHelper.template(this);
    }

}
