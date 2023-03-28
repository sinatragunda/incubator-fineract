/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 15:42
 */
package org.apache.fineract.utility.helper;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.service.EnumeratedData;
import org.apache.fineract.utility.service.IEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EnumTemplateHelper {

    public static List<EnumOptionData> template(IEnum iEnum[]){

        List<EnumOptionData> list = new ArrayList<>();
        Stream.of(iEnum).forEach(e->{
            EnumOptionData enumOptionData = new EnumOptionData(e.ordinal() ,e.getCode());
            list.add(enumOptionData);
        });
        return list ;
    }

    public static EnumOptionData template(IEnum iEnum){
        EnumOptionData enumOptionData = new EnumOptionData(iEnum.ordinal() ,iEnum.getCode());
        return enumOptionData;
    }

    public static IEnum fromInt(IEnum values[], int arg){
        for(IEnum iEnum : values){
            if(iEnum.ordinal() == arg){
                return iEnum;
            }
        }
        return null ;
    }

    public static IEnum fromString(IEnum values[], String arg){
        for(IEnum iEnum : values){
            if(iEnum.getCode().equalsIgnoreCase(arg)){
                return iEnum;
            }
        }
        return null ;
    }

    /**
     * Added 18/02/2023 at 0608
     * Provision for nulls ,reserves first ordinal for values that can have default values
     */  
    public static IEnum fromStringEx(IEnum values[], String arg){
        for(IEnum iEnum : values){
            if(iEnum.getCode().equalsIgnoreCase(arg)){
                return iEnum;
            }
        }
        return values[0] ;
    }

    /**
     * Added 13/03/2023 at 0912
     * 
     */
    public static IEnum fromIntEx(IEnum values[] ,int arg){
        IEnum iEnum = fromInt(values ,arg);
        boolean isPresent = OptionalHelper.isPresent(iEnum);
        if(!isPresent){
            return values[0];
        }
        return iEnum;
    }

    public static List<EnumOptionData> fromEnumeratedList(List<? extends EnumeratedData> genericList){

        List<EnumOptionData> enumOptionDataList = new ArrayList<>();

        for(EnumeratedData enumeratedData : genericList){
            EnumOptionData enumOptionData = new EnumOptionData(enumeratedData.getId(), enumeratedData.getName());
            enumOptionDataList.add(enumOptionData);
        }
        return enumOptionDataList;
    }

}
