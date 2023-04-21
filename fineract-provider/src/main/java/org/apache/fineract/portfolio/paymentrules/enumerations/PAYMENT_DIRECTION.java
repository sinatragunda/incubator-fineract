/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 April 2023 at 06:56
 */
package org.apache.fineract.portfolio.paymentrules.enumerations;

import org.apache.fineract.utility.service.IEnum;

public enum PAYMENT_DIRECTION implements IEnum {
    IN("Pay In"),
    OUT("Pay Out");

    private String code ;
    PAYMENT_DIRECTION(String code){
        this.code = code ;
    }

    @Override
    public String getCode() {
        return code;
    }
}
