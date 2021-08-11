

package org.apache.fineract.wese.helper;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

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

}