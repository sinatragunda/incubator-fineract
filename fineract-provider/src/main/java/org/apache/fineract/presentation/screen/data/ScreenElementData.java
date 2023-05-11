/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 April 2023 at 19:48
 */
package org.apache.fineract.presentation.screen.data;

import com.wese.component.defaults.enumerations.COMPARISON_GROUP;
import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.ELEMENT_TYPE;
import com.wese.component.defaults.enumerations.OPERAND_GATES;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.Collection;
import java.util.Optional;
import java.util.List ;

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

    private List<EnumOptionData> selectOptions;
    private REF_TABLE refTable;

    private ELEMENT_TYPE elementType;
    private EnumOptionData elementTypeData;
    private LocalRefData localRefData;

    public ScreenElementData(Long id , String name , String value, String modelName, String displayName, boolean show, boolean mandatory, OPERAND_GATES operandGates, COMPARISON_TYPE comparisonType, COMPARISON_GROUP comparisonGroup, Collection<ScreenElementData> childElements , Long parentScreenElementId , REF_TABLE refTable , ELEMENT_TYPE elementType , LocalRefData localRefData) {
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
        this.refTable = refTable;
        this.elementType = elementType ;
        this.elementTypeData = EnumTemplateHelper.template(elementType);
        this.localRefData = localRefData;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public ELEMENT_TYPE getElementType() {
        return elementType;
    }

    public LocalRefData getLocalRefData() {
        return localRefData;
    }

    public REF_TABLE getRefTable() {
        return refTable;
    }

    public COMPARISON_GROUP getComparisonGroup() {
        return comparisonGroup;
    }

    public void setChildElements(Collection<ScreenElementData> childElements) {
        this.childElements = childElements;
    }

    public void setSelectOptions(List<EnumOptionData> enumOptionDataList){
        this.selectOptions = enumOptionDataList;
    }
}

