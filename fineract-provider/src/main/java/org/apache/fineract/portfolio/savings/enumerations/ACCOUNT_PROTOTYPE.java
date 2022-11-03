package org.apache.fineract.portfolio.savings.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.remittance.enumerations.RX_PROVIDER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum ACCOUNT_PROTOTYPE {

    CHECKING("Checking"),
    SETTLEMENT("Settlement"),
    FUND("Fund");

    private String code ;
    ACCOUNT_PROTOTYPE(String code){
        this.code = code;
    }

    public static ACCOUNT_PROTOTYPE fromInt(Integer arg){

        for(ACCOUNT_PROTOTYPE t : values()){
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
