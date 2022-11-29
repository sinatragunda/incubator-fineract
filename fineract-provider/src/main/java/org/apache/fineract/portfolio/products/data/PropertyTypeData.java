/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 29 November 2022 at 00:24
 */
package org.apache.fineract.portfolio.products.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.products.enumerations.PROPERTY_TYPE;

import java.util.Collection;

public class PropertyTypeData {

    private Long id ;
    private EnumOptionData propertyType ;
    private Collection<EnumOptionData> propertyTypeOptions = PROPERTY_TYPE.template();

    public static PropertyTypeData template(){
        return new PropertyTypeData();
    }

    public PropertyTypeData(){}

    public static PropertyTypeData lookup(final Long id ,final PROPERTY_TYPE propertyType){
        return new PropertyTypeData(id ,propertyType);
    }

    public PropertyTypeData(Long id ,PROPERTY_TYPE propertyType){
        this.id = id ;
        this.propertyType = propertyType.toEnumData();
    }
    public PropertyTypeData(EnumOptionData propertyType) {
        this.propertyType = propertyType;
    }

    public EnumOptionData getPropertyType() {
        return propertyType;
    }
}
