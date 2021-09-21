/*

    Created by Sinatra Gunda
    At 8:31 AM on 9/19/2021

*/

package org.apache.fineract.wese.portfolio.depreciation.enumerations;

public enum DEPRECIATION_METHOD {

    STRAIGHT_LINE("Straigh Line Method");

    DEPRECIATION_METHOD(String code){
        this.code = code ;
    }

    private String code ;

    public static DEPRECIATION_METHOD fromInt(int arg){

        for(DEPRECIATION_METHOD s : values()){
            if(s.ordinal()==arg){
                return s ;
            }
        }
        return null;
    }
}
