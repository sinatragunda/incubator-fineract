package org.apache.fineract.portfolio.products.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.*;

public enum ACCOUNT_TYPE {
    
    CHECKING("Checking"),
    SETTLEMENT("Settlement"),
    FUND("Fund"),
    EQUITY("Equity"),
    DRAWDOWN("Drawdown");

    private String code ;
    ACCOUNT_TYPE(String code){
        this.code = code;
    }

    public static ACCOUNT_TYPE fromInt(Integer arg){

        arg = Optional.ofNullable(arg).orElse(0);

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

    /**
     * Added 14/11/2022 at 0305
     */
    public EnumOptionData toEnumData(){
        return new EnumOptionData(ordinal() ,code);
    }  

}
