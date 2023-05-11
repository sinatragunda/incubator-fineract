/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 07 May 2023 at 16:30
 */
package org.apache.fineract.infrastructure.wsplugin.enumerations;

import org.apache.fineract.utility.helper.FunctionsHelper;
import org.apache.fineract.utility.service.IEnum;

import java.util.function.Function;

public enum WS_INTERNAL_SCRIPTS implements IEnum{

    TODAY("Today Date" , FunctionsHelper.todayDate),
    MULTIPLY("Multiplier" ,FunctionsHelper.convertToInteger);

    WS_INTERNAL_SCRIPTS(String code ,Function function){
        this.function = function ;
        this.code = code;
    }

    private String code ;
    private Function function;

    @Override
    public String getCode() {
        return code;
    }

    public Function getFunction() {
        return function;
    }
}
