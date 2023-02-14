/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 February 2023 at 12:51
 */
package org.apache.fineract.infrastructure.dataqueries.enumerations;

import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;

public enum DATA_TABLE_CATEGORY implements IEnum{

    NORMAL(""),
    APPLICATION("m_application");
    private String code ;

    DATA_TABLE_CATEGORY(String code){
        this.code = code;
    }

    public static DATA_TABLE_CATEGORY fromString(String arg){
        return (DATA_TABLE_CATEGORY)EnumTemplateHelper.fromString(DATA_TABLE_CATEGORY.values() ,arg);
    }

    @Override
    public String getCode() {
        return code;
    }
}
