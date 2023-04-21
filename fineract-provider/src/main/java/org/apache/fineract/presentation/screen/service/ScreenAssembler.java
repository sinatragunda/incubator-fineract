/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:46
 */
package org.apache.fineract.presentation.screen.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wese.component.defaults.enumerations.*;
import com.wese.component.defaults.exceptions.BeanLoaderNotFoundException;
import com.wese.component.defaults.helper.AnnotationHelper;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.helper.ScreenApiConstant;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.From;
import java.util.HashSet;
import java.util.Set;

@Service
public class ScreenAssembler {

    private FromJsonHelper fromJsonHelper;

    @Autowired
    public ScreenAssembler(FromJsonHelper fromJsonHelper) {
        this.fromJsonHelper = fromJsonHelper;
    }



    public Screen fromJson(JsonCommand command){

        String name = command.stringValueOfParameterNamed(GenericConstants.nameParam);
        String shortName = command.stringValueOfParameterNamed(GenericConstants.shortNameParam);

        String refTableValue = command.stringValueOfParameterNamed(ScreenApiConstant.refTableParam);
        REF_TABLE refTable = refTable(refTableValue ,refTableValue);

        //System.err.println("----------------ref table value "+refTable);

        CLASS_LOADER classLoader = classLoader(refTableValue ,refTableValue);

        //System.err.println("--------------class loader is "+classLoader);

        Long officeId = command.longValueOfParameterNamed(ClientApiConstants.officeIdParamName);

        JsonElement dataElement =  command.jsonElementOfParamNamed(GenericConstants.dataParam);
        JsonObject jsonObject = dataElement.getAsJsonObject();
        Set<String> keySet = jsonObject.keySet();

        Set<ScreenElement> screenElementsSet = new HashSet<>();
        Screen screen = new Screen(name ,shortName,null ,null ,refTable,true ,screenElementsSet);

        for(String key : keySet){
            //System.err.println("----------keyset val is "+key);
            JsonObject keyObject = fromJsonHelper.extractJsonObjectNamed(key ,dataElement);
            ScreenElement screenElement = screenElementFromJson(keyObject ,screen ,null ,key ,classLoader);
            screenElementsSet.add(screenElement);
        }

        screen.setScreenElementSet(screenElementsSet);
        return screen ;

    }

    /**
     * Added 17/04/2023 at 0251
     * Get classloader from ref table value
     * Value can be string or int
     */
    private CLASS_LOADER classLoader(String intArg ,String value){

        CLASS_LOADER classLoader = CLASS_LOADER.fromString(value);
        boolean has = OptionalHelper.isPresent(classLoader);
        if(!has){
            try {
                Integer arg = Integer.valueOf(intArg);
                classLoader = (CLASS_LOADER) EnumTemplateHelper.fromInt(CLASS_LOADER.values(), arg);
                has = OptionalHelper.isPresent(classLoader);
                if (!has) {
                    throw new BeanLoaderNotFoundException(arg.longValue());
                }
            }
            catch (NumberFormatException n){}
        }
        return classLoader;
    }
    private REF_TABLE refTable(String intArg ,String value){

        REF_TABLE refTable = (REF_TABLE) EnumTemplateHelper.fromString(REF_TABLE.values(), value);
        boolean has = OptionalHelper.isPresent(refTable);
        if(!has){
            try {
                Integer arg = Integer.valueOf(intArg);
                refTable = (REF_TABLE) EnumTemplateHelper.fromInt(REF_TABLE.values(), arg);
                has = OptionalHelper.isPresent(refTable);
                if (!has) {
                    throw new BeanLoaderNotFoundException(arg.longValue());
                }
            }
            catch (NumberFormatException n){
                System.err.println("--------------exception for converting reftable "+n.getMessage());
            }
        }
        return refTable;
    }


    private static String getDisplayName(String key ,String displayName ,Class cl){

        //System.err.println("=====================display name for key ? "+key);
        boolean has = OptionalHelper.isPresent(displayName);
        if(!has){
            displayName = AnnotationHelper.getAttributeName(cl ,key);
            //System.err.println("-------------------------display name from annotation is "+displayName);
        }
        return displayName;
    }


    public ScreenElement screenElementFromJson(JsonObject jsonObject ,Screen screen ,ScreenElement parentScreenElement ,String key ,CLASS_LOADER classLoader){

        Class cl = classLoader.getCl();
        JsonElement element = JsonCommandHelper.toJsonElement(jsonObject.toString());

        Boolean showOnUi = fromJsonHelper.extractBooleanNamed(ScreenApiConstant.showOnUiParam ,element);
        String displayName = fromJsonHelper.extractStringNamed(ScreenApiConstant.displayNameParam ,element);

        /**
         * Modified 13/04/2023 at 1551
         * Not best setting but to be changed .
         * Get default value of attribute ref if user has not provided from ui
         */
        //System.err.println("-------------------set display name again  ---------"+displayName);
        displayName = getDisplayName(key ,displayName ,cl);

        String modelName = AnnotationHelper.getModelName(cl ,key);

        //System.err.println("--------------------model name from annotation helper is "+modelName);

        String value = fromJsonHelper.extractStringNamed(ScreenApiConstant.valueParam ,element);

        Integer comparisonTypeInt = fromJsonHelper.extractIntegerSansLocaleNamed(ScreenApiConstant.comparisonTypeParam ,element);

        COMPARISON_TYPE comparisonType = (COMPARISON_TYPE) EnumTemplateHelper.fromIntEx(COMPARISON_TYPE.values(),comparisonTypeInt);
        boolean mandatory = fromJsonHelper.extractBooleanNamed(ScreenApiConstant.mandatoryParam ,element);

        COMPARISON_GROUP comparisonGroup =  AnnotationHelper.getComparisonGroup(cl ,key);

        //System.err.println("----------------------group is "+comparisonGroup);

        Integer gateInt = fromJsonHelper.extractIntegerSansLocaleNamed(ScreenApiConstant.operandGateParam ,element);

        OPERAND_GATES gate = (OPERAND_GATES)EnumTemplateHelper.fromIntEx(OPERAND_GATES.values(), gateInt);

        Boolean multirow = fromJsonHelper.extractBooleanNamed(ScreenApiConstant.multiRowParam ,element);

        JsonObject subValueJsonObject = fromJsonHelper.extractJsonObjectNamed(ScreenApiConstant.subValueParam ,element);

        ScreenElement screenElement = new ScreenElement(key ,displayName , modelName ,comparisonType ,comparisonGroup , gate , ELEMENT_TYPE.SYSTEM ,showOnUi ,mandatory ,value ,screen ,parentScreenElement ,null);

        System.err.println("------------element to string  "+screenElement);

        Set<String> subValuesKeySet = new HashSet<>();
        Set<ScreenElement> childElementList = new HashSet<>();
        boolean hasSubValues = OptionalHelper.isPresent(subValueJsonObject);

        if(hasSubValues){
            //System.err.println("--------------has subvalues "+hasSubValues);
            for(String keyValue : subValuesKeySet){
                //System.err.println("===============key value is "+keyValue);
                JsonObject subValueDataObject = subValueJsonObject.getAsJsonObject(keyValue);
                ScreenElement childElement = screenElementFromJson(subValueDataObject,screen ,screenElement ,key ,classLoader);
                childElementList.add(childElement);
            }
        }

        screenElement.setChildElements(childElementList);
        return screenElement;
    }
}
