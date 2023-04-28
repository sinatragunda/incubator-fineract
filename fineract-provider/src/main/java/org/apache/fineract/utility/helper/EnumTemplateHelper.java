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

    public static EnumOptionData templateWithValue(IEnum iEnum){
        Long id = Long.valueOf(iEnum.ordinal());
        EnumOptionData enumOptionData = new EnumOptionData(id ,iEnum.getCode() ,iEnum.getValue());
        return enumOptionData;
    }


    /**
     * Added 31/03/2023 at 0049
     */
    public static EnumOptionData fromIntToTemplate(IEnum iEnum[], int arg){
        
        IEnum value = fromInt(iEnum ,arg);
        EnumOptionData enumOptionData = new EnumOptionData(value.ordinal() ,value.getCode());
        return enumOptionData;
    }
  


    public static IEnum fromInt(IEnum values[], Integer arg){

        boolean hasInt = OptionalHelper.isPresent(arg);

        if(hasInt){
            for(IEnum iEnum : values){
                if(iEnum.ordinal() == arg){
                    return iEnum;
                }
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
     * Extended version that returns the first value as the default to avoid null
     * 
     */
    public static IEnum fromIntEx(IEnum values[] ,Integer arg){

        IEnum iEnum = fromInt(values ,arg);
        boolean isPresent = OptionalHelper.isPresent(iEnum);
        if(isPresent){
            return iEnum;
        }
        return values[0];
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
