/*

    Created by Sinatra Gunda
    At 11:47 AM on 8/10/2022

*/
package org.apache.fineract.accounting.enumerations;

public enum ENTITY_TYPE {

    LOAN("LOANS"),
    SAVINGS("SAVINGS"),
    SHARES("SHARES"),
    JOURNAL("JOURNAL");


    private String prefix ;
    private String code ;

    ENTITY_TYPE(String code){
        this.code = code ;
    }

    public String getCode(){
        return code ;
    }

    public String prefix(){
        switch (this){
            case LOAN:
                prefix = "L";
                break;
            case SAVINGS:
                prefix = "S";
                break;
            case JOURNAL:
                prefix = "";
                break;
        }

        return prefix;
    }

    public static ENTITY_TYPE fromInt(int arg){
        for(ENTITY_TYPE a : values()){
            if(a.ordinal()==arg){
                return a ;
            }
        }
        return null;
    }


    public static ENTITY_TYPE fromString(String arg){
        for(ENTITY_TYPE a : values()){
            if(a.getCode().equalsIgnoreCase(arg)){
                return a ;
            }
        }
        return null;
    }
}
