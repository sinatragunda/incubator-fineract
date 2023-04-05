/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 11 March 2023 at 03:09
 */
package org.apache.fineract.utility.helper;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnumeratedDataHelper {


    public static List<EnumOptionData> enumeratedData(List<? extends EnumeratedData> enumeratedData){
        
        List<EnumOptionData> enumOptionDataList = new ArrayList<>();
        
        for(EnumeratedData e : enumeratedData){
            EnumOptionData enumOptionData = new EnumOptionData(e.getId() ,e.getName(),e.getName());
            enumOptionDataList.add(enumOptionData);
        }

        return enumOptionDataList;
    }

    /**
     * Added 30/03/2023 at 2050
     */

    public static EnumOptionData enumOptionData(EnumeratedData e){
        
        EnumOptionData enumOptionData = new EnumOptionData(e.getId() ,e.getName(),e.getName());
        return enumOptionData;
    }  
}
