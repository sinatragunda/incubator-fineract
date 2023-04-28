/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 08:29
 */
package org.apache.fineract.infrastructure.wsplugin.enumerations;

import org.apache.fineract.utility.service.IEnum;

public enum SCRIPT_TYPE implements IEnum {

    JAVA("Java");

    SCRIPT_TYPE(String code){
        this.code = code;
    }

    private String code;

    @Override
    public String getCode() {
        return code;
    }
}
