
package org.apache.fineract.wese.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

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

}
