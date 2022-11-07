/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 November 2022 at 07:12
 */
package org.apache.fineract.portfolio.remittance.enumerations;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum RX_DEAL_STATUS{

    OPENED("Opened") ,
    CLOSED ("Closed"),
    HOLD("Hold") ,
    FALSE_VERIFICATION("False Verification"),
    BOUNCED("Bounced") ;

    private String code ;

    RX_DEAL_STATUS(String c){
        this.code =c ;
    }

    public String getCode() {
        return code;
    }

    public static RX_DEAL_STATUS fromInt(int i){
        for(RX_DEAL_STATUS c : values()){
            if(c.ordinal()==i){
                return c ;
            }
        }
        return null ;
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