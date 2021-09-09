/*

    Created by Sinatra Gunda
    At 5:16 AM on 9/1/2021

*/
package org.apache.fineract.wese.enumerations;

public enum DURATION_TYPE{
    MINUTES,
    HOURLY,
    DAILY;

    public static DURATION_TYPE fromInt(int arg){
        for(DURATION_TYPE v : values()){
            if(arg==v.ordinal()){
                return v ;
            }
        }
        return null ;
    }
}
