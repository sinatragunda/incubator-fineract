/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 April 2023 at 01:51
 */
package org.apache.fineract.portfolio.charge.enumerations;

import org.apache.fineract.utility.service.IEnum;

public enum TIER_TYPE implements IEnum {

    BAND("Banded"),
    LEVEL("Level");


    TIER_TYPE(String code){
        this.code = code ;
    }

    private String code ;

    public String getCode() {
        return code;
    }
}
