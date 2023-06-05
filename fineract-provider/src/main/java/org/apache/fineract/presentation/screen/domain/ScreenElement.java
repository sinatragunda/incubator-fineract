/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:13
 */
package org.apache.fineract.presentation.screen.domain;

import com.wese.component.defaults.domain.WsObject;
import com.wese.component.defaults.enumerations.COMPARISON_GROUP;
import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.ELEMENT_TYPE;
import com.wese.component.defaults.enumerations.OPERAND_GATES;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.portfolio.localref.domain.LocalRef;

import javax.persistence.*;
import java.util.HashSet;
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

    @ManyToOne
    @JoinColumn(name ="wsscript_id")
    private WsScript wsScript;


    @ManyToOne
    @JoinColumn(name ="local_ref_id")
    private LocalRef localRef ;


    @Column(name ="sequence_number")
    private Integer sequenceNumber ;


    protected ScreenElement(){}

    public ScreenElement(String name, String displayName , String modelName, COMPARISON_TYPE comparisonType, COMPARISON_GROUP comparisonGroup , OPERAND_GATES operandGates, ELEMENT_TYPE elementType, Boolean showOnUi, Boolean mandatory, String value, Screen screen, ScreenElement screenElement , Set<ScreenElement> childElements ,WsScript wsScript,LocalRef localRef ,Integer sequenceNumber) {
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
        this.wsScript = wsScript ;
        this.localRef = localRef;
        this.sequenceNumber = sequenceNumber;
    }

    public String getModelName() {
        return modelName;
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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public LocalRef getLocalRef() {
        return localRef;
    }

    public WsScript getWsScript(){
        return this.wsScript;
    }

    public Set getChildElements(){
        return OptionalHelper.optionalOf(this.childElements ,new HashSet<>());
    }

    public void setChildElements(Set childElements){
        this.childElements = childElements ;
    }

    public void setParentScreenElement(ScreenElement screenElement) {
        this.parentScreenElement = screenElement;
    }

    public ELEMENT_TYPE getElementType(){
        return this.elementType;
    }

    @Override
    public String toString() {
        return "ScreenElement{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", comparisonType=" + comparisonType +
                ", comparisonGroup= "+ comparisonGroup +
                ", operandGates=" + operandGates +
                ", elementType=" + elementType +
                ", showOnUi=" + showOnUi +
                ", mandatory=" + mandatory +
                ", parameter ="+parameter+
                ", value='" + value + '\'' +
                '}';
    }

    /**
     * Modified 22/04/2023 at 1532
     * Modified to find a way to add subelements into the set as well as previous installation made them equal to parent object
     * So comparisonType would be different and value as well
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScreenElement)) return false;
        ScreenElement that = (ScreenElement) o;
        return modelName.equals(that.modelName) && comparisonType == that.comparisonType && value.equals(that.value);
    }

    public void updateFromWsObject(WsObject wsObject){
        Object object = wsObject.get();
        String value  = object.toString();
        setParameter(value);
        System.err.println("----------------screen element "+this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelName);
    }


}
