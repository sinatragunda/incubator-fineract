/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 April 2023 at 19:48
 */
package org.apache.fineract.presentation.screen.data;

import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.OPERAND_GATES;

import java.util.Collection;

public class ScreenElementData {

    private Long id ;
    private String name ;
    private String value ;
    private String modelName ;
    private String displayName ;
    private boolean show ;
    private boolean mandatory ;
    private OPERAND_GATES operandGates;
    private COMPARISON_TYPE comparisonType;
    private Collection<ScreenElementData> childElements;


    public ScreenElementData(Long id ,String name ,String value, String modelName, String displayName, boolean show, boolean mandatory, OPERAND_GATES operandGates, COMPARISON_TYPE comparisonType, Collection<ScreenElementData> childElements) {
        this.id = id ;
        this.name = name;
        this.value = value;
        this.modelName = modelName;
        this.displayName = displayName;
        this.show = show;
        this.mandatory = mandatory;
        this.operandGates = operandGates;
        this.comparisonType = comparisonType;
        this.childElements = childElements;
    }
}

