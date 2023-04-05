/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 13:31
 */
package org.apache.fineract.presentation.menu.enumerations;

import org.apache.fineract.utility.service.IEnum;

public enum MENU_PLACEMENT implements IEnum {
    SIDE_BAR("Side Bar");

    MENU_PLACEMENT(String code){
        this.code = code;
    }
    private String code ;

    @Override
    public String getCode() {
        return code;
    }
}
