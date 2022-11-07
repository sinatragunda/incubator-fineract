package org.apache.fineract.portfolio.products.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum ACCOUNT_TYPE {
    
    CHECKING("Checking"),
    SETTLEMENT("Settlement"),
    FUND("Fund");

    private String code ;
    ACCOUNT_TYPE(String code){
        this.code = code;
    }

    public static ACCOUNT_TYPE fromInt(Integer arg){

        for(ACCOUNT_TYPE t : values()){
            if(arg.equals(t.ordinal())  ){
                return t ;
            }
        }
        return CHECKING ;
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
