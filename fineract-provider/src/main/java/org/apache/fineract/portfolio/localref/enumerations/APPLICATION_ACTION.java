/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 02 February 2023 at 12:55
 */
package org.apache.fineract.portfolio.localref.enumerations;

import org.apache.fineract.utility.service.IEnum;

public enum APPLICATION_ACTION implements IEnum {

    NEW("New","new"),
    VIEW("View" ,"view"),
    LIST("List" ,"list"),
    EDIT("Edit" ,"edit"),
    LANDING("Landing" ,"s");

    APPLICATION_ACTION(String code ,String value){
        this.code = code;
        this.value = value ;
    }

    private String value;
    private String code ;

    @Override
    public String getCode() {
        return code;
    }

    public String getValue(){
        return this.value;
    }
}
