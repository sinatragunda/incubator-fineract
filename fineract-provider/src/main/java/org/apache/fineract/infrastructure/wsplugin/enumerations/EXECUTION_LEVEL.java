/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 27 April 2023 at 10:18
 */
package org.apache.fineract.infrastructure.wsplugin.enumerations;

import org.apache.fineract.utility.service.IEnum;

public enum EXECUTION_LEVEL implements IEnum {

    FIELD("Field"),
    APPLICATION("Application");

    private String code;
    EXECUTION_LEVEL(String code){
        this.code =code ;
    }

    @Override
    public String getCode() {
        return code;
    }
}
