/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 16:39
 */
package org.apache.fineract.portfolio.localref.data;

import com.wese.component.defaults.enumerations.ELEMENT_TYPE;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.enumerations.REF_VALUE_TYPE;

import java.util.Collection;
import java.util.Locale;

public class LocalRefData {

    private Long id ;
    private String name ;
    private String description ;
    private Boolean isMandatory ;

    private REF_VALUE_TYPE refValueType;
    private EnumOptionData refTable ;


    private EnumOptionData refValueTypeData;

    private String codeName ;
    private String officeName ;
    private Collection<EnumOptionData> refTableOptions ;
    private Collection<EnumOptionData> refValueTypeOptions ;
    private Collection<EnumOptionData> permissionOptions ;
    private CodeData codeData;
    private Collection<LocalRefData> existingLocalRefs;
    private Collection<CodeData> codeDataCollection;

    private Long codeId ;

    public static LocalRefData template(){
        return new LocalRefData(REF_TABLE.template() , REF_VALUE_TYPE.template() ,null);
    }
    public LocalRefData(Collection<EnumOptionData> refTableOptions, Collection<EnumOptionData> refValueTypeOptions, Collection<EnumOptionData> permissionOptions) {
        this.refTableOptions = refTableOptions;
        this.refValueTypeOptions = refValueTypeOptions;
        this.permissionOptions = permissionOptions;
    }

    public LocalRefData(Long id ,REF_VALUE_TYPE refValueType){
        this.id = id ;
        this.refValueType = refValueType ;
    }

    public LocalRefData(Long id, String name, String description, REF_TABLE refTable, REF_VALUE_TYPE refValueType, String codeName ,String officeName ,Long codeId ,Boolean isMandatory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.refTable = refTable.toEnumData();
        this.refValueType = refValueType;
        this.refValueTypeData = refValueType.toEnumData();
        this.codeName = codeName;
        this.officeName = officeName;
        this.codeId = codeId ;
        this.isMandatory = isMandatory;
    }

    public Long getId() {
        return id;
    }

    public Boolean getMandatory() {
        return isMandatory;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setCodeDataCollection(Collection<CodeData> codeDataCollection) {
        this.codeDataCollection = codeDataCollection;
    }

    public Collection<LocalRefData> getExistingLocalRefs() {
        return existingLocalRefs;
    }

    public Long getCodeId() {
        return codeId;
    }

    public REF_VALUE_TYPE getRefValueType() {
        return refValueType;
    }

    public EnumOptionData getRefValueTypeData(){
        return refValueTypeData ;
    }

    public CodeData getCodeData() {
        return codeData;
    }

    public void setCodeData(CodeData codeData) {
        this.codeData = codeData;
    }
    public void setExistingLocalRefs(Collection existingLocalRefs){
        this.existingLocalRefs = existingLocalRefs;
    }


}
