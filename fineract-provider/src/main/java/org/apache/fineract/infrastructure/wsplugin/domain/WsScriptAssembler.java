/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 12:11
 */
package org.apache.fineract.infrastructure.wsplugin.domain;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.wsplugin.api.WsScriptConstants;
import org.apache.fineract.infrastructure.wsplugin.enumerations.RETURN_TYPE;
import org.apache.fineract.infrastructure.wsplugin.enumerations.SCRIPT_TYPE;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.wese.helper.JsonCommandHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List ;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WsScriptAssembler {


    public static List<WsScript> assembleFromJsonElement(FromJsonHelper fromJsonHelper ,JsonElement jsonElement){

        List<WsScript> wsScriptList = new ArrayList<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<String> keyset = jsonObject.keySet();
        for (String key: keyset) {

            JsonElement subElement = jsonObject.get(key);
            JsonCommand command = JsonCommandHelper.fromJsonElement(fromJsonHelper ,subElement);
            WsScript wsScript = assembleFromJson(command);

            boolean has = OptionalHelper.isPresent(wsScript);
            if(has) {
                wsScriptList.add(wsScript);
            }
        }
        return wsScriptList;
    }

    public static WsScript assembleFromJson(JsonCommand command){

        String className = command.stringValueOfParameterNamed(WsScriptConstants.qualifiedClassNameParam);
        String methodName = command.stringValueOfParameterNamed(WsScriptConstants.methodNameParam);
        Boolean addToContainer = command.booleanObjectValueOfParameterNamed("add");
        String returnTypeStr = command.stringValueOfParameterNamed(WsScriptConstants.returnTypeParam);

        RETURN_TYPE returnType = (RETURN_TYPE) EnumTemplateHelper.fromStringEx(RETURN_TYPE.values() ,returnTypeStr);

        if(addToContainer){
            WsScript wsScript = new WsScript(className ,methodName ,returnType, SCRIPT_TYPE.EXTERNAL);
            return wsScript;
        }
        return null ;
    }

    public static List<WsScript> assembleFromJsonArray(FromJsonHelper fromJsonHelper ,JsonArray array){

        List list = new ArrayList<>();
        for(JsonElement element : array){
            JsonCommand command = JsonCommandHelper.fromJsonElement(fromJsonHelper , element);
            WsScript wsScript = assembleFromJson(command);
            Boolean has = OptionalHelper.isPresent(wsScript);
            if(has){
                list.add(wsScript);
            }
        }
        return list ;
    }
}
