/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 15:39
 */
package org.apache.fineract.utility.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.List;

public interface IEnum {
    public int ordinal();
    public String getCode();

    default public String getValue(){
        return null ;
    };
}
