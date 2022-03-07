/*

    Created by Sinatra Gunda
    At 3:20 AM on 8/27/2021

*/
package org.apache.fineract.wese.enumerations;

public enum SEND_MAIL_MESSAGE_STATUS {

    SUCCESS("Success"),
    INVALID_ADDRESS("Invalid Email Address"),
    QOUTA_LIMIT("Quota Limit"),
    BOUNCE_BACK("Bounced Back form Mail Server"),
    ERROR("Error"),
    NETWORK_FAILURE("Network Failure");

    private String code ;

    SEND_MAIL_MESSAGE_STATUS(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code ;
    }
};
