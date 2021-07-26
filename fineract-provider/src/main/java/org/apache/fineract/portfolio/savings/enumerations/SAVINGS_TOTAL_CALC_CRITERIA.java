/*

    Created by Sinatra Gunda
    At 5:11 AM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.enumerations;

import java.util.ArrayList;
import java.util.List;

public enum  SAVINGS_TOTAL_CALC_CRITERIA {

    NET_BALANCES("Net Balances"),
    DEPOSITS("Balance on Deposits");

    String code ;

    SAVINGS_TOTAL_CALC_CRITERIA(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static SAVINGS_TOTAL_CALC_CRITERIA fromString(String arg){
        for(SAVINGS_TOTAL_CALC_CRITERIA s : values()){
            if(arg.equalsIgnoreCase(s.getCode())){
                return s ;
            }
        }
        return null ;
    }

    public static List<String> getList(){
        List<String> value = new ArrayList<>();
        for(SAVINGS_TOTAL_CALC_CRITERIA s : values()){
            value.add(s.getCode());
        }
        return value;
    }

}
