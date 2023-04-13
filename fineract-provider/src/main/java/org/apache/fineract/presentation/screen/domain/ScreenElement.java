/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:13
 */
package org.apache.fineract.presentation.screen.domain;

import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.ELEMENT_TYPE;
import com.wese.component.defaults.enumerations.OPERAND_GATES;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import javax.persistence.*;
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

    //@Transient Object userValue ;

    protected ScreenElement(){}

    public ScreenElement(String name, String displayName , String modelName, COMPARISON_TYPE comparisonType, OPERAND_GATES operandGates, ELEMENT_TYPE elementType, Boolean showOnUi, Boolean mandatory, String value, Screen screen, ScreenElement screenElement , Set<ScreenElement> childElements) {
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
                ", value='" + value + '\'' +
                '}';
    }
}
