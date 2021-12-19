
package org.apache.fineract.wese.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;


// Added 19/12/2021
import com.fasterxml.jackson.databind.node.ObjectNode;


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

    public static <T> String serializeResponse(T object){

        ObjectNode objectNode[] = {ObjectNodeHelper.statusNode(false)};

        Optional.ofNullable(object).ifPresent(e->{
            String json = objectToJson(object);
            objectNode[0] = ObjectNodeHelper.objectNodeFromString(json);
            objectNode[0].put("status",true);
        });

        return objectNode[0].toString();

    }

}
