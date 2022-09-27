/*

    Created by Sinatra Gunda
    At 4:23 PM on 8/17/2021

*/
package org.apache.fineract.enumerations;

public enum MODULE {

    LOANS("Loans"),
    SAVINGS("Savings"),
    SHARES("Shares"),
    CLIENTS("Clients"),
    DISCARD("Discard");

    MODULE(String code){
        this.code = code ;
    }

    private String code ;

    public String getCode() {
        return code;
    }

    public static MODULE fromInt(int arg){
        for(MODULE val : values()){
            if(val.ordinal()==arg){
                return val ;
            }
        }
        return null ;
    }

    public static MODULE fromString(String arg){
        for(MODULE val : values()){
            if(val.getCode().equalsIgnoreCase(arg)){
                return val ;
            }
        }
        return null ;
    }
}
