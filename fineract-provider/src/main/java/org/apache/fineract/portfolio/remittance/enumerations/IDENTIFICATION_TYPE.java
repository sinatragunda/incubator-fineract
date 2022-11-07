package org.apache.fineract.portfolio.remittance.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum IDENTIFICATION_TYPE {

    NONE("None"),
    ID("Id"),
    PASSPORT("Passport");

    private String code ;

    IDENTIFICATION_TYPE(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Collection<EnumOptionData> template(){
        List<EnumOptionData> enumOptionDataList = new ArrayList<>();
        Arrays.stream(values()).forEach(e->{
            EnumOptionData enumOptionData = new EnumOptionData(Long.valueOf(e.ordinal()) ,e.code , e.code);
            enumOptionDataList.add(enumOptionData);
        });
        return  enumOptionDataList;
    }

    public static IDENTIFICATION_TYPE fromInt(final Integer val){
        for(IDENTIFICATION_TYPE t : values()){
            if(t.ordinal()==val){
                return t ;
            }
        }
        return ID ;
    }
}
