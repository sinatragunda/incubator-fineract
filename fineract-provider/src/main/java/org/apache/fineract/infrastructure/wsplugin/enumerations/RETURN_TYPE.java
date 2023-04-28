/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 09:08
 */
package org.apache.fineract.infrastructure.wsplugin.enumerations;

import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;

import java.lang.reflect.Type;

public enum RETURN_TYPE implements IEnum {

    OBJECT("Object" ,"Object"),
    VOID("void" ,"Void"),
    STRING("java.lang.String" ,"String"),
    INTEGER("java.lang.Integer" ,"Integer");

    private String code ;
    private String value ;

    RETURN_TYPE(String code,String value){
        this.code = code;
        this.value = value;

    }

    @Override
    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static RETURN_TYPE fromString(String arg){
        return (RETURN_TYPE) EnumTemplateHelper.fromStringEx(RETURN_TYPE.values() ,arg);
    }
}
