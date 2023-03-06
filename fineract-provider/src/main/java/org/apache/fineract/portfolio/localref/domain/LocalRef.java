/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 16:08
 */
package org.apache.fineract.portfolio.localref.domain;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.codes.domain.Code;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.enumerations.REF_VALUE_TYPE;
import org.apache.fineract.portfolio.localref.helper.LocalRefConstants;
import org.apache.fineract.useradministration.domain.Permission;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Table(name ="m_local_ref")
@Entity
public class LocalRef extends AbstractPersistableCustom<Long> {

    @Column(name="name")
    private String name ;

    @Column(name="description")
    private String description ;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="ref_table" ,nullable=false)
    private REF_TABLE refTable;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="ref_value_type" ,nullable=false)
    private REF_VALUE_TYPE refValueType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="code_id" ,nullable=true)
    private Code code ;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name ="permission_id" ,nullable=true)
    private Permission permission ;

    @Temporal(TemporalType.DATE)
    @Column(name="submitted_date" ,nullable=false)
    private Date submittedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="office_id" ,nullable=false)
    private Office office;

    @Column(name="is_mandatory")
    private Boolean isMandatory;

    public LocalRef(String name, String description, REF_TABLE refTable, REF_VALUE_TYPE refValueType, Code code, Permission permission, Date submittedDate ,Office office ,Boolean isMandatory) {
        this.name = name;
        this.description = description;
        this.refTable = refTable;
        this.refValueType = refValueType;
        this.code = code;
        this.permission = permission;
        this.submittedDate = submittedDate;
        this.office = office;
        this.isMandatory = isMandatory;
    }

    public Boolean isMandatory() {
        return (Boolean)OptionalHelper.optionalOf(this.isMandatory ,false);
    }

    public String getName(){
        return this.name;
    }

    public Code getCode(){
        return (Code)OptionalHelper.optionalOf(this.code ,null);
    }

    public Map<String ,Object> update(JsonCommand command){

        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        if (command.isChangeInStringParameterNamed(LocalRefConstants.descriptionParam, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(LocalRefConstants.descriptionParam);
            actualChanges.put(LocalRefConstants.descriptionParam, newValue);
            this.description = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(LocalRefConstants.isMandatoryParam, this.isMandatory)){
            final Boolean newValue = command.booleanPrimitiveValueOfParameterNamed(LocalRefConstants.isMandatoryParam);
            actualChanges.put(LocalRefConstants.isMandatoryParam, newValue);
            this.isMandatory = newValue ;
        }

        if (command.isChangeInIntegerParameterNamed(LocalRefConstants.refValueTypeParam, this.refValueType.ordinal())) {
            final Integer newValue = command.integerValueOfParameterNamed(LocalRefConstants.refValueTypeParam);
            actualChanges.put(LocalRefConstants.refValueTypeParam, newValue);
            this.refValueType = REF_VALUE_TYPE.fromInt(newValue);
        }

        if (command.isChangeInIntegerParameterNamed(LocalRefConstants.refTableParam, this.refTable.ordinal())) {
            final Integer newValue = command.integerValueOfParameterNamed(LocalRefConstants.refTableParam);
            actualChanges.put(LocalRefConstants.refTableParam, newValue);
            this.refTable = REF_TABLE.fromInt(newValue);
        }

        if (command.isChangeInLongParameterNamed(ClientApiConstants.officeIdParamName, this.office.getId())){
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.officeIdParamName);
            actualChanges.put(ClientApiConstants.officeIdParamName, newValue);
            this.office.setIdEx(newValue);
        }

        boolean hasCode = Optional.ofNullable(this.code).isPresent();
        if(hasCode) {
            if (command.isChangeInLongParameterNamed(LocalRefConstants.codeIdParam, code.getId())) {
                final Long newValue = command.longValueOfParameterNamed(LocalRefConstants.codeIdParam);
                actualChanges.put(LocalRefConstants.codeIdParam, newValue);
                this.code.setIdEx(newValue);
            }
        }

        if (command.isChangeInStringParameterNamed(LocalRefConstants.nameParam, this.name)){
            final String newValue = command.stringValueOfParameterNamed(LocalRefConstants.nameParam);
            actualChanges.put(LocalRefConstants.nameParam, newValue);
            this.name = newValue;
        }
        return actualChanges;
    }
}
