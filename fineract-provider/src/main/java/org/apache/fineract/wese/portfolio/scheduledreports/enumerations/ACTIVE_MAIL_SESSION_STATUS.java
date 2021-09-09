/*

    Created by Sinatra Gunda
    At 9:58 AM on 9/7/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.enumerations;

public enum ACTIVE_MAIL_SESSION_STATUS {

    ACTIVE("Active"),
    QUOTA_REACHED("Quota Reached"),
    CLOSED("Closed");

    private String code ;

    ACTIVE_MAIL_SESSION_STATUS(String code){
        this.code = code ;
    }

    public String getCode() {
        return code;
    }
};
