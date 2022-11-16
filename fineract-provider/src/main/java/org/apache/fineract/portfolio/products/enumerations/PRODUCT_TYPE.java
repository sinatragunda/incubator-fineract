/*

    Created by Sinatra Gunda
    At 9:44 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum PRODUCT_TYPE {

    SAVINGS("SAVINGS"),
    LOANS("LOANS"),
    SHARES("SHARES"),
    DEPOSITS("DEPOSITS");

    String code ;

    PRODUCT_TYPE(String code){
        this.code = code ;
    }

    public static PRODUCT_TYPE fromString(String code){
        for(PRODUCT_TYPE productType : values()){
            boolean match = productType.code.equalsIgnoreCase(code);
            if(match){
                return productType;
            }
        }
        return null ;
    }

    public String getCode() {
        return code;
    }

    /**
     * Added 14/11/2022 at 0258
     */
    
    public static Collection<EnumOptionData> template(){
        List<EnumOptionData> enumOptionDataList = new ArrayList<>();
        Arrays.stream(values()).forEach(e->{
            EnumOptionData enumOptionData = new EnumOptionData(Long.valueOf(e.ordinal()) ,e.code , e.code);
            enumOptionDataList.add(enumOptionData);
        });
        return  enumOptionDataList;
    }

    public EnumOptionData toEnumData(){
        return new EnumOptionData(ordinal() ,code);
    }

}
