package org.apache.fineract.portfolio.remittance.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum RX_PROVIDER {
    INTERNAL("Internal"),
    WU("Western Union"),
    WR("World Remit"),
    MG("Money Gram"),
    MK("Mukuru"),
    CH("City Hopper"),
    AC("Access Forex");

    private String code ;

    public String getCode(){
        return this.code;
    }

    RX_PROVIDER(String code){
        this.code = code ;
    }

    public static RX_PROVIDER fromString(String arg){

        for(RX_PROVIDER t : values()){
           if(t.getCode().equalsIgnoreCase(arg)){
               return t ;
           }
        }
        return INTERNAL ;
    }
    public static RX_PROVIDER fromInt(Integer arg){

        for(RX_PROVIDER t : values()){
            if(arg.equals(t.ordinal())  ){
                return t ;
            }
        }
        return INTERNAL ;
    }

    public static Collection<EnumOptionData> template(){
        List<EnumOptionData> enumOptionDataList = new ArrayList<>();
        Arrays.stream(values()).forEach(e->{
            EnumOptionData enumOptionData = new EnumOptionData(Long.valueOf(e.ordinal()) ,e.code , e.code);
            enumOptionDataList.add(enumOptionData);
        });
        return  enumOptionDataList;
    }
}
