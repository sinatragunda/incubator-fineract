/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 16:39
 */
package org.apache.fineract.portfolio.localref.data;

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
    private EnumOptionData refTable ;
    private EnumOptionData refValueType;
    private String codeName ;
    private String officeName ;
    private Collection<EnumOptionData> refTableOptions ;
    private Collection<EnumOptionData> refValueTypeOptions ;
    private Collection<EnumOptionData> permissionOptions ;
    private Collection<CodeData> codeData;
    private Collection<LocalRefData> existingLocalRefs;

    public static LocalRefData template(){
        return new LocalRefData(REF_TABLE.template() , REF_VALUE_TYPE.template() ,null);
    }
    public LocalRefData(Collection<EnumOptionData> refTableOptions, Collection<EnumOptionData> refValueTypeOptions, Collection<EnumOptionData> permissionOptions) {
        this.refTableOptions = refTableOptions;
        this.refValueTypeOptions = refValueTypeOptions;
        this.permissionOptions = permissionOptions;
    }

    public LocalRefData(Long id, String name, String description, REF_TABLE refTable, REF_VALUE_TYPE refValueType, String codeName ,String officeName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.refTable = refTable.toEnumData();
        this.refValueType = refValueType.toEnumData();
        this.codeName = codeName;
        this.officeName = officeName;
    }

    public void setCodeData(Collection<CodeData> codeData) {
        this.codeData = codeData;
    }
    public void setExistingLocalRefs(Collection existingLocalRefs){
        this.existingLocalRefs = existingLocalRefs;
    }
}
