/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 15:26
 */
package org.apache.fineract.portfolio.localref.enumerations;

import com.wese.component.defaults.enumerations.COMPARISON_GROUP;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum REF_VALUE_TYPE implements IEnum{

    BOOLEAN("Boolean"),
    STRING("String"),
    DECIMAL("Decimal"),
    CODE_VALUE("Template"),
    NUMERAL("Numeric"),
    DATE("Date");


    public COMPARISON_GROUP group(){
        COMPARISON_GROUP comparisonGroup = COMPARISON_GROUP.GENERIC;
        switch (this){
            case DATE:
                comparisonGroup = COMPARISON_GROUP.DATE;
                break;
            case STRING:
                comparisonGroup = COMPARISON_GROUP.STRING;
                break;
            case DECIMAL:
            case NUMERAL:
                comparisonGroup = COMPARISON_GROUP.NUMERIC;
                break;
            case CODE_VALUE:
                comparisonGroup = COMPARISON_GROUP.LIST;
                break;
            case BOOLEAN:
                comparisonGroup = COMPARISON_GROUP.BOOLEAN;
                break;
        }
        return comparisonGroup;
    }

    private String code ;
    REF_VALUE_TYPE(String code){
        this.code = code ;
    }

    public static List<EnumOptionData> template(){
        return EnumTemplateHelper.template(REF_VALUE_TYPE.values());
    }

    @Override
    public String getCode() {
        return code;
    }

    public static REF_VALUE_TYPE fromInt(int arg){
        return (REF_VALUE_TYPE) EnumTemplateHelper.fromInt(values() ,arg);
    }

    public EnumOptionData toEnumData(){
        return EnumTemplateHelper.template(this);
    }
}
