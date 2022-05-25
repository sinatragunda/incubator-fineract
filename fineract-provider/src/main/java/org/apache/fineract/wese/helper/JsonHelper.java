
package org.apache.fineract.wese.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;


// Added 19/12/2021
import com.fasterxml.jackson.databind.node.ObjectNode;

// Added 04/01/2021
import java.util.Map ;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {

    public static String objectToJson(Object object){

        String json = null ;
        try{
            json = new ObjectMapper().writeValueAsString(object);
        }

        catch (JsonProcessingException j){
            j.printStackTrace();
            return null ;
        }

        return json ;
    }

    public static <T> T stringToObject(String value ,T t){
        T object = null ;
        try{
            object = (T) new ObjectMapper().readValue(value ,t.getClass());
        }
        catch (IOException i){
            return null ;
        }

        return object ;
    }


    public static <T> T serializeFromHttpResponse(T object ,String arg){

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        T t = null ;
        try{
            t = (T)objectMapper.readValue(arg ,object.getClass());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return t ;
    }

    public static <T> String serializeResponse(T object ,String message){

        ObjectNode objectNode[] = {ObjectNodeHelper.statusNode(false).put("message",new String(message))};

        Optional.ofNullable(object).ifPresent(e->{
            String json = objectToJson(object);
            objectNode[0] = ObjectNodeHelper.objectNodeFromString(json);
            objectNode[0].put("status",true);
        });

        return objectNode[0].toString();

    }

    // Added 04/01/2022 1:52am
    public static String serializeMapToJson(Map<String,Object> map){
        ObjectNode node = ObjectNodeHelper.objectNodeFromMap(map);
        return node.toString();
    }


    public static JSONObject put(String key ,Object value){
        return put(null ,key ,value);
    }


    public static JSONObject put(String payload ,String key ,Object data){

       boolean isPayloadPresent = Optional.ofNullable(payload).isPresent();
       
       JSONObject json = new JSONObject() ;

       if(isPayloadPresent){
            json = new JSONObject(payload);
       }
       
       try{
           json.put(key ,data);
       }

       catch (JSONException j){
           System.err.println(Constants.separator+" json exception here ");
           j.printStackTrace();
       }

       return json ;
   }

   // added 25/05/2022
   public static String update(String payload ,String key ,Object value){

        JSONObject json = put(payload ,key ,value);
        return json.toString();
   }


}
