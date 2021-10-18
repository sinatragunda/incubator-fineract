/*

    Created by Sinatra Gunda
    At 3:14 PM on 10/16/2021

*/
package org.apache.fineract.wese.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement ;

public class JsonCommandHelper {

    public static JsonCommand jsonCommand(FromJsonHelper fromJsonHelper ,String payload){

        JsonElement jsonElement = fromJsonHelper.parse(payload);
        JsonCommand jsonCommand = JsonCommand.fromJsonElement(0L ,jsonElement ,fromJsonHelper);
        return jsonCommand;

    }
}
