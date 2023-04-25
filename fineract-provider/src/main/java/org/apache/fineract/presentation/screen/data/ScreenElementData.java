/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 April 2023 at 19:48
 */
package org.apache.fineract.presentation.screen.data;

import com.wese.component.defaults.enumerations.COMPARISON_GROUP;
import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.OPERAND_GATES;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.Collection;
import java.util.Optional;

public class ScreenElementData implements EnumeratedData {

    private Long id ;
    private String name ;
    private String value ;
    private String modelName ;
    private String displayName ;
    private boolean show ;
    private boolean mandatory ;
    private Long parentScreenElementId ;
    private OPERAND_GATES operandGates;
    private COMPARISON_TYPE comparisonType;
    private COMPARISON_GROUP comparisonGroup;
    private Collection<ScreenElementData> childElements;

    private EnumOptionData operandGatesOption ;
    private EnumOptionData comparisonTypeOption ;
    private EnumOptionData comparisonGroupOption;

    public ScreenElementData(Long id , String name , String value, String modelName, String displayName, boolean show, boolean mandatory, OPERAND_GATES operandGates, COMPARISON_TYPE comparisonType, COMPARISON_GROUP comparisonGroup, Collection<ScreenElementData> childElements ,Long parentScreenElementId) {
        this.id = id ;
        this.name = name;
        this.value = value;
        this.modelName = modelName;
        this.displayName = displayName;
        this.show = show;
        this.mandatory = mandatory;
        this.operandGates = operandGates;
        this.comparisonGroup = comparisonGroup;
        this.comparisonType = comparisonType;
        this.childElements = childElements;
        this.operandGatesOption = EnumTemplateHelper.template(operandGates);
        this.comparisonTypeOption = EnumTemplateHelper.template(comparisonType);
        this.comparisonGroupOption = EnumTemplateHelper.template(comparisonGroup);
        this.parentScreenElementId = parentScreenElementId ;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setChildElements(Collection<ScreenElementData> childElements) {
        this.childElements = childElements;
    }
}

