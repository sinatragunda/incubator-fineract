

package org.apache.fineract.wese.helper;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

import java.util.Set;

public class ObjectNodeHelper{


	public static ObjectNode objectNode(){

        ObjectMapper objectMapper = new ObjectMapper();
        return  objectMapper.createObjectNode();
    }

    public static ArrayNode arrayNode(){

        ObjectMapper objectMapper = new ObjectMapper();
        return  objectMapper.createArrayNode();
    }


    public static ObjectNode statusNode(boolean status){
	    return objectNode().put("status",status);
    }


    // Added 19/12/2021
    public static ObjectNode objectNodeFromString(String arg){

        ObjectMapper objectMapper = new ObjectMapper();
        try{
            return (ObjectNode) objectMapper.readTree(arg);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null ;
    }

    // Added 04/01/2022
    public static ObjectNode objectNodeFromMap(Map<String,Object> map){

        ObjectNode objectNode = objectNode();

        Set<String> keySet = map.keySet();

        for(String key : keySet){
            Object value = map.get(key);
            String valueString = String.valueOf(value);
            objectNode.put(key,valueString);
        }

        return objectNode ;
    }


}