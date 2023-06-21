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
import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.repo.WsScriptRepositoryWrapper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.repo.LocalRefRepositoryWrapper;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.helper.ScreenApiConstant;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;
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
    private WsScriptRepositoryWrapper wsScriptRepositoryWrapper;
    private LocalRefRepositoryWrapper localRefRepositoryWrapper;

    @Autowired
    public ScreenAssembler(FromJsonHelper fromJsonHelper ,final WsScriptRepositoryWrapper wsScriptRepositoryWrapper ,final LocalRefRepositoryWrapper localRefRepositoryWrapper) {
        this.fromJsonHelper = fromJsonHelper;
        this.wsScriptRepositoryWrapper = wsScriptRepositoryWrapper;
        this.localRefRepositoryWrapper = localRefRepositoryWrapper;
    }


    public Screen fromJson(JsonCommand command){

        String name = command.stringValueOfParameterNamed(GenericConstants.nameParam);
        String shortName = command.stringValueOfParameterNamed(GenericConstants.shortNameParam);

        String refTableValue = command.stringValueOfParameterNamed(ScreenApiConstant.refTableParam);
        REF_TABLE refTable = refTable(refTableValue ,refTableValue);

        Boolean multirow  = command.booleanObjectValueOfParameterNamed(ScreenApiConstant.multiRowParam);

        //System.err.println("----------------ref table value "+refTable);

        CLASS_LOADER classLoader = classLoader(refTableValue ,refTableValue);

        //System.err.println("--------------class loader is "+classLoader);

        Long officeId = command.longValueOfParameterNamed(ClientApiConstants.officeIdParamName);

        JsonElement dataElement =  command.jsonElementOfParamNamed(GenericConstants.dataParam);
        JsonObject jsonObject = dataElement.getAsJsonObject();
        Set<String> keySet = jsonObject.keySet();

        Set<ScreenElement> screenElementsSet = new HashSet<>();
        Screen screen = new Screen(name ,shortName,null ,null ,refTable,true ,screenElementsSet ,multirow);

        for(String key : keySet){
            //System.err.println("----------keyset val is "+key);
            JsonObject keyObject = fromJsonHelper.extractJsonObjectNamed(key ,dataElement);
            ScreenElement screenElement = screenElementFromJson(keyObject ,screen ,null ,key ,classLoader);
            screenElementsSet.add(screenElement);
        }
        System.err.println("-----------------------------screen elements are ?"+screenElementsSet.size());
        //screen.setScreenElementSet(screenElementsSet);
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
                beanLoaderValidation(classLoader ,arg.longValue());
            }
            catch (NumberFormatException n){}
        }
        return classLoader;
    }


    private void beanLoaderValidation(Object object ,Long arg){
        boolean has = OptionalHelper.isPresent(object);
        if (!has){
            throw new BeanLoaderNotFoundException(arg);
        }
    }

    private REF_TABLE refTable(String intArg ,String value){

        REF_TABLE refTable = (REF_TABLE) EnumTemplateHelper.fromString(REF_TABLE.values(), value);
        boolean has = OptionalHelper.isPresent(refTable);
        if(!has){
            try {
                Integer arg = Integer.valueOf(intArg);
                refTable = (REF_TABLE) EnumTemplateHelper.fromInt(REF_TABLE.values(), arg);
                beanLoaderValidation(refTable ,arg.longValue());
            }
            catch (NumberFormatException n){
                System.err.println("--------------exception for converting reftable "+n.getMessage());
            }
        }
        return refTable;
    }

    private static String getDisplayName(String key ,String displayName ,Class cl,Boolean isSystemField){

        boolean has = OptionalHelper.isPresent(displayName);
        if(!has){
            
            if(isSystemField){
                displayName = AnnotationHelper.getAttributeName(cl ,key);
            }
        }
        return displayName;
    }


    public ScreenElement screenElementFromJson(JsonObject jsonObject ,Screen screen ,ScreenElement parentScreenElement ,String key ,CLASS_LOADER classLoader){

        Class cl = classLoader.getCl();
        JsonElement element = JsonCommandHelper.toJsonElement(jsonObject.toString());

        Boolean showOnUi = fromJsonHelper.extractBooleanNamed(ScreenApiConstant.showOnUiParam ,element);
        
        String displayName = fromJsonHelper.extractStringNamed(ScreenApiConstant.displayNameParam ,element);


        String modelName = AnnotationHelper.getModelName(cl ,key);

        String value = fromJsonHelper.extractStringNamed(ScreenApiConstant.valueParam ,element);

        Integer comparisonTypeInt = fromJsonHelper.extractIntegerSansLocaleNamed(ScreenApiConstant.comparisonTypeParam ,element);

        Integer sequenceNumber = fromJsonHelper.extractIntegerSansLocaleNamed(ScreenApiConstant.sequenceNumberParam ,element);

        COMPARISON_TYPE comparisonType = (COMPARISON_TYPE) EnumTemplateHelper.fromIntEx(COMPARISON_TYPE.values(),comparisonTypeInt);
        boolean mandatory = fromJsonHelper.extractBooleanNamed(ScreenApiConstant.mandatoryParam ,element);

        COMPARISON_GROUP comparisonGroup =  AnnotationHelper.getComparisonGroup(cl ,key);

        //System.err.println("----------------------group is "+comparisonGroup);

        Integer gateInt = fromJsonHelper.extractIntegerSansLocaleNamed(ScreenApiConstant.operandGateParam ,element);

        OPERAND_GATES gate = (OPERAND_GATES)EnumTemplateHelper.fromIntEx(OPERAND_GATES.values(), gateInt);

        JsonObject subValueJsonObject = fromJsonHelper.extractJsonObjectNamed(ScreenApiConstant.subValueParam ,element);

        boolean hasWsScript = fromJsonHelper.parameterExists(ScreenApiConstant.wsScriptIdParam ,element);

        //final Integer elementTypeId = fromJsonHelper.extractIntegerSansLocaleNamed(ScreenApiConstant.elementTypeParam ,element);
        //final ELEMENT_TYPE elementType = (ELEMENT_TYPE)EnumTemplateHelper.fromIntEx(ELEMENT_TYPE.values() ,elementTypeId);

        ELEMENT_TYPE elementType = ELEMENT_TYPE.SYSTEM;
        LocalRef localRef = null ;

        Boolean hasModelName = OptionalHelper.isPresent(modelName);

        boolean isSystemField = true ;
        if (!hasModelName){
            /**
             * Added 04/05/2023 at 0953
             * The assumption is it has no model name since its posted from ui then its a local ref
             */
            elementType = ELEMENT_TYPE.LOCAL_REF;
            modelName = key;
            localRef = localRefRepositoryWrapper.findOneWithoutNotFoundDetection(modelName);
            comparisonGroup = localRef.getRefValueType().group();
            isSystemField = false;
        }


        /**
         * Modified 13/04/2023 at 1551 and at 08/06/2023 at 0421
         * Not best setting but to be changed .
         * Get default value of attribute ref if user has not provided from ui
         * Added isSystemField bool check to avoid exception on Annotation.getAttribute if value is LocalRef
         */
        displayName = getDisplayName(key ,displayName ,cl ,isSystemField);

        WsScript wsScript = null ;
        
        if (hasWsScript){
            Long wsScriptId = fromJsonHelper.extractLongNamed(ScreenApiConstant.wsScriptIdParam ,element);
            wsScript = wsScriptRepositoryWrapper.findOneWithNotFoundDetection(wsScriptId);
        }

        ScreenElement screenElement = new ScreenElement(key ,displayName , modelName ,comparisonType ,comparisonGroup , gate ,elementType ,showOnUi ,mandatory ,value ,screen ,parentScreenElement ,null ,wsScript ,localRef ,sequenceNumber);

        System.err.println("------------element to string  "+screenElement);

        Set<ScreenElement> childElementList = new HashSet<>();

        boolean hasSubValues = OptionalHelper.isPresent(subValueJsonObject);

        System.err.println("---------print subvalue object -----"+subValueJsonObject);

        if(hasSubValues){

            Set<String> subValuesKeySet = subValueJsonObject.keySet();

            System.err.println("-------------has sub values "+hasSubValues+"--------- why is it still empty  "+subValuesKeySet.size());

            for(String keyValue : subValuesKeySet){
                System.err.println("===============key value is "+keyValue);
                JsonObject subValueDataObject = subValueJsonObject.getAsJsonObject(keyValue);
                ScreenElement childElement = screenElementFromJson(subValueDataObject,screen ,screenElement ,key ,classLoader);
                childElement.setDisplayName(displayName);
                childElementList.add(childElement);
            }
        }

        System.err.println("------------how many child elements do we have here ?"+childElementList.size());
        screenElement.setChildElements(childElementList);
        return screenElement;
    }
}
