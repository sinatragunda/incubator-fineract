/*

    Created by Sinatra Gunda
    At 3:14 PM on 10/16/2021

*/
package org.apache.fineract.wese.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement ;

import java.util.Map;

public class JsonCommandHelper {

    public static JsonCommand jsonCommand(FromJsonHelper fromJsonHelper ,String payload){

        JsonElement jsonElement = fromJsonHelper.parse(payload);
        JsonCommand jsonCommand = JsonCommand.fromJsonElement(0L ,jsonElement ,fromJsonHelper);
        return jsonCommand;

    }

    /**
     * Added 01/11/2022 at 0911
     */
    public static JsonCommand fromJsonElement(FromJsonHelper fromJsonHelper ,JsonElement jsonElement){
        JsonCommand jsonCommand = JsonCommand.fromJsonElement(0L ,jsonElement ,fromJsonHelper);
        return jsonCommand;
        
    }

    /**
     * Added 22/03/2023
     */
    public static JsonElement toJsonElement(String payload){
        JsonElement jsonElement = new Gson().fromJson(payload ,JsonElement.class);
        return jsonElement;
    }   

    /**
     * Added 01/11/2022 at 1810
     */
    public static JsonCommand jsonCommand(FromJsonHelper fromJsonHelper , Map map){
        
        String payload = JsonHelper.serializeMapToJson(map);
        JsonElement jsonElement = fromJsonHelper.parse(payload);
        JsonCommand jsonCommand = JsonCommand.fromJsonElement(0L ,jsonElement ,fromJsonHelper);
        return jsonCommand;

    }
  
}
