/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 12:08
 */
package org.apache.fineract.infrastructure.wsplugin.domain;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.infrastructure.wsplugin.api.WsScriptConstants;
import org.apache.fineract.utility.helper.ListHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.fineract.wese.helper.Constants;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
@Service
public class WsScriptContainerAssembler{

    private FromJsonHelper fromJsonHelper;

    @Autowired
    public WsScriptContainerAssembler(FromJsonHelper fromJsonHelper) {
        this.fromJsonHelper = fromJsonHelper;
    }

    public WsScriptContainer assembleFromJson(JsonCommand command){

        String name =command.stringValueOfParameterNamed(GenericConstants.nameParam);
        //String description = command.stringValueOfParameterNamed(GenericConstants.descriptionParam);

        JsonElement element = command.jsonElementOfParamNamed(WsScriptConstants.annotatedFunctionsParam);

        List<WsScript> wsScriptList = WsScriptAssembler.assembleFromJsonElement(fromJsonHelper ,element);

        System.err.println("----------------wsscript size is "+wsScriptList.size());

        Set<WsScript> wsScriptSet = ListHelper.toSet(wsScriptList);

        System.err.println("-----------------wscriptset siz e"+wsScriptList.size());

        WsScriptContainer wsScriptContainer = new WsScriptContainer(name ,null , wsScriptSet);

        Consumer<WsScript> setWsContainerConsumer = (e)->{
            e.setWsScriptContainer(wsScriptContainer);
        };


        wsScriptSet.stream().forEach(setWsContainerConsumer);

        return wsScriptContainer;
    }

}
