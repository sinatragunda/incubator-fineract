/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 15:42
 */
package org.apache.fineract.utility.helper;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
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
}
