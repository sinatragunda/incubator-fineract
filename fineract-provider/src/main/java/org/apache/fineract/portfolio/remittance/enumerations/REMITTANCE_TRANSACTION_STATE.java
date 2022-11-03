
package org.apache.fineract.portfolio.remittance.enumerations;


import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum REMITTANCE_TRANSACTION_STATE{

	OPENED("Opened") ,
	CLOSED ("Closed"),
	HOLD("Hold") ,
	BOUNCED("Bounced") ;

	private String code ;

    REMITTANCE_TRANSACTION_STATE(String c){
        this.code =c ;
    }

    public String getCode() {
        return code;
    }

    public static REMITTANCE_TRANSACTION_STATE fromInt(int i){
        for(REMITTANCE_TRANSACTION_STATE c : REMITTANCE_TRANSACTION_STATE.values()){
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