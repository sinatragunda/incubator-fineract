/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 15:24
 */
package org.apache.fineract.portfolio.localref.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;

import java.util.List;

public enum REF_TABLE implements IEnum {
    UNSPECIFIED("Unspecified"),
    CLIENT("Client"),
    LOAN("Loan"),
    SHARE("Share"),
    CHARGE("Charges"),
    ACCOUNT("Account");

    private String code;

    REF_TABLE(String code){
        this.code = code ;
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