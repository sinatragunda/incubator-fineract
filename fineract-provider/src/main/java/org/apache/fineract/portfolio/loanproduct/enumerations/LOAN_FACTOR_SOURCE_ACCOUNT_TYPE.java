/*

    Created by Sinatra Gunda
    At 12:00 AM on 8/21/2021

*/
package org.apache.fineract.portfolio.loanproduct.enumerations;

import java.util.Optional;

public enum  LOAN_FACTOR_SOURCE_ACCOUNT_TYPE {

    SAVINGS("Savings Account"),
    SHARE_ACCOUNT("Share Account"),
    NONE("None");

    private String code ;

    LOAN_FACTOR_SOURCE_ACCOUNT_TYPE(String code){}

    public static LOAN_FACTOR_SOURCE_ACCOUNT_TYPE fromInt(Integer arg){

        LOAN_FACTOR_SOURCE_ACCOUNT_TYPE loanFactor[] = {NONE};

        Optional.ofNullable(arg).ifPresent(e ->{
            for(LOAN_FACTOR_SOURCE_ACCOUNT_TYPE a : values()){
                if(a.ordinal()==arg){
                    loanFactor[0] = a ;
                }
            }  
        });
        return loanFactor[0] ;
    }
}
