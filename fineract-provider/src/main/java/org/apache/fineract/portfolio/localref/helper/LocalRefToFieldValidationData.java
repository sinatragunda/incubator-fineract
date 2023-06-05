/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 04 May 2023 at 01:45
 */
package org.apache.fineract.portfolio.localref.helper;

import com.wese.component.defaults.data.FieldValidationData;
import com.wese.component.defaults.enumerations.COMPARISON_GROUP;
import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.ELEMENT_TYPE;
import com.wese.component.defaults.enumerations.FIELD_TYPE;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.enumerations.REF_VALUE_TYPE;
import org.apache.fineract.utility.helper.EnumeratedDataHelper;

import java.util.*;
import java.util.function.Consumer;

public class LocalRefToFieldValidationData {


    public static List<FieldValidationData> convertForTemplate(LocalRefData localRefData){

        List<FieldValidationData> fieldValidationDataSet = new ArrayList<>();
        Boolean has = OptionalHelper.isPresent(localRefData);
        Integer sequenceNumber[] = {0}; 

        if(has) {

            Collection<LocalRefData> existingLocalRefs = localRefData.getExistingLocalRefs();

            Consumer<LocalRefData> convertConsumer = (e) -> {
                Long id = e.getId();
                String name = e.getName();
                REF_VALUE_TYPE refValueType = e.getRefValueType();
                ELEMENT_TYPE elementType = ELEMENT_TYPE.LOCAL_REF;
                COMPARISON_GROUP comparisonGroup = refValueType.group();

                List<EnumOptionData> dataListOptions = null;
                if (comparisonGroup == COMPARISON_GROUP.LIST) {
                    dataListOptions = getTemplateData(e);
                }

                FIELD_TYPE fieldType = FIELD_TYPE.OPTIONAL;
                Boolean isMandatory = e.getMandatory();

                if (isMandatory) {
                    fieldType = FIELD_TYPE.MANDATORY;
                }

                ++sequenceNumber[0];

                FieldValidationData fieldValidationData = new FieldValidationData(name, name, fieldType, null, comparisonGroup, dataListOptions, name, elementType ,sequenceNumber[0]);
                fieldValidationDataSet.add(fieldValidationData);
            };

            existingLocalRefs.stream().forEach(convertConsumer);
        }

        return  fieldValidationDataSet;
    }

    public static List<EnumOptionData> getTemplateData(LocalRefData e) {

        List<EnumOptionData> dataListOptions = new ArrayList<>();
        try {
            CodeData codeData = e.getCodeData();
            Collection codeValuesList = codeData.getCodeValueDataCollection();
            dataListOptions = EnumeratedDataHelper.enumeratedData(codeValuesList);
        }
        catch (Exception n){
            //n.printStackTrace();
        }
        return dataListOptions;
    }
}
