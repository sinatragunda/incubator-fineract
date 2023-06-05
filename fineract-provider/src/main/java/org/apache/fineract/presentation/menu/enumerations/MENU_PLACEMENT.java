/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 13:31
 */
package org.apache.fineract.presentation.menu.enumerations;

import org.apache.fineract.utility.service.IEnum;

public enum MENU_PLACEMENT implements IEnum {

    TOP_BAR_CLIENT("Client Quick Link",false),
    SIDE_BAR("Side Bar",true),
    QUICK_LINK("Quick Links" ,true);

    /**
     * Modified 26/05/2023 at 1132 
     * IsMultiple is a boolean to show whether the menu placement value allows multiple entries or only a single instance 
     * There are places where we need only value instead
     * If design pattern changes will implement changes ,for now not so ...
     */ 

    MENU_PLACEMENT(String code ,Boolean allowMultiplicity){
        this.code = code;
        this.allowMultiplicity = allowMultiplicity;
    }
    private String code ;
    private Boolean allowMultiplicity;

    @Override
    public String getCode() {
        return code;
    }

    public Boolean isAllowMultiplicity(){
        return this.allowMultiplicity;
    }
}
