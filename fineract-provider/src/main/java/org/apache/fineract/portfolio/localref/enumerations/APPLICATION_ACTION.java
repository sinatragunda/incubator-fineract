/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 02 February 2023 at 12:55
 */
package org.apache.fineract.portfolio.localref.enumerations;

import org.apache.fineract.utility.service.IEnum;

public enum APPLICATION_ACTION implements IEnum {

    NEW("New"),
    VIEW("View"),
    LIST("List"),
    EDIT("Edit");

    APPLICATION_ACTION(String code){
        this.code = code;
    }
    private String code ;

    @Override
    public String getCode() {
        return code;
    }
}
