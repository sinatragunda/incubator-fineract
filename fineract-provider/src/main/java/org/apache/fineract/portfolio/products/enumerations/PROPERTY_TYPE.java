/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 29 November 2022 at 00:06
 */
package org.apache.fineract.portfolio.products.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum PROPERTY_TYPE {
    UNDEFINED("Undefined"),
    TRACKING("Tracking");

    private String code ;
    PROPERTY_TYPE(String code){
        this.code = code ;
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

    public static PROPERTY_TYPE fromInt(int arg){
        for(PROPERTY_TYPE propertyType : values()){
            if(arg == propertyType.ordinal()){
                return propertyType;
            }
        }
        return UNDEFINED ;
    }


    public EnumOptionData toEnumData(){
        return new EnumOptionData(ordinal() ,code);
    }

}
