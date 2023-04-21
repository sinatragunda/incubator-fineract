/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:13
 */
package org.apache.fineract.presentation.screen.domain;

import com.wese.component.defaults.enumerations.COMPARISON_GROUP;
import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.ELEMENT_TYPE;
import com.wese.component.defaults.enumerations.OPERAND_GATES;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name ="m_screen_element")
public class ScreenElement extends AbstractPersistableCustom<Long> {

    @Column(name ="name")
    private String name ;

    @Column(name ="display_name")
    private String displayName ;

    @Column(name ="model_name")
    private String modelName ;

    @Enumerated(EnumType.ORDINAL)
    @Column(name ="comparison_type")
    private COMPARISON_TYPE comparisonType;


    @Enumerated(EnumType.ORDINAL)
    @Column(name ="comparison_group")
    private COMPARISON_GROUP comparisonGroup;


    @Enumerated(EnumType.ORDINAL)
    @Column(name="gate")
    private OPERAND_GATES operandGates;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="element_type")
    private ELEMENT_TYPE elementType;

    @Column(name="show_on_ui")
    private Boolean showOnUi;

    @Column(name="mandatory")
    private Boolean mandatory;

    @Column(name="value")
    private String value ;

    @ManyToOne
    @JoinColumn(name ="screen_id")
    private Screen screen ;

    @ManyToOne
    @JoinColumn(name ="parent_screen_element_id")
    private ScreenElement parentScreenElement;

    @OneToMany(fetch = FetchType.EAGER ,cascade = CascadeType.ALL ,mappedBy = "parentScreenElement")
    private Set<ScreenElement> childElements ;

    @Transient String parameter ;

    protected ScreenElement(){}

    public ScreenElement(String name, String displayName , String modelName, COMPARISON_TYPE comparisonType, COMPARISON_GROUP comparisonGroup , OPERAND_GATES operandGates, ELEMENT_TYPE elementType, Boolean showOnUi, Boolean mandatory, String value, Screen screen, ScreenElement screenElement , Set<ScreenElement> childElements) {
        this.name = name;
        this.modelName = modelName;
        this.comparisonType = comparisonType;
        this.operandGates = operandGates;
        this.elementType = elementType;
        this.showOnUi = showOnUi;
        this.mandatory = mandatory;
        this.value = value;
        this.screen = screen;
        this.parentScreenElement = screenElement;
        this.childElements = childElements;
        this.displayName = displayName ;
        this.comparisonGroup = comparisonGroup ;
    }

    public COMPARISON_GROUP getComparisonGroup() {
        return this.comparisonGroup;
    }

    public COMPARISON_TYPE getComparisonType(){
        return this.comparisonType;
    }

    public String getDisplayName() {
        return displayName;
    }

    //public String getParameter() {return parameter;}

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter(){
        return this.parameter;
    }

    public String getValue() {
        return value;
    }

    public void setChildElements(Set childElements){
        this.childElements = childElements ;
    }

    public void setParentScreenElement(ScreenElement screenElement) {
        this.parentScreenElement = screenElement;
    }

    @Override
    public String toString() {
        return "ScreenElement{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", comparisonType=" + comparisonType +
                ", operandGates=" + operandGates +
                ", elementType=" + elementType +
                ", showOnUi=" + showOnUi +
                ", mandatory=" + mandatory +
                ", paramerer ="+parameter+
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScreenElement)) return false;
        ScreenElement that = (ScreenElement) o;
        return modelName.equals(that.modelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelName);
    }


}
