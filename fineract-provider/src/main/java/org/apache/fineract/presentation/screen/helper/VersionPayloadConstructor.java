/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 May 2023 at 08:01
 */
package org.apache.fineract.presentation.screen.helper;

import com.wese.component.defaults.enumerations.ELEMENT_TYPE;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.JsonParserHelper;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;

import java.util.HashMap;
import java.util.List ;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VersionPayloadConstructor {

    public static String construct(List<ScreenElement> screenElementList){

        Map<String ,Object> objectMap = new HashMap<>();

        Consumer<ScreenElement> convertToJson = (e)->{
            String value = e.getParameter();
            String key  = e.getModelName();
            //System.err.println("---------value to insert and key --"+key+"------------"+value);
            objectMap.put(key ,value);
        };

        objectMap.put("locale" ,"en");
        objectMap.put("dateFormat" ,"dd MMMM yyyy");

        Predicate<ScreenElement> isLocalRef = (e)-> e.getElementType()== ELEMENT_TYPE.LOCAL_REF;

        screenElementList.stream().filter(isLocalRef.negate()).forEach(convertToJson);
        String payload = JsonHelper.serializeMapToJson(objectMap);

        objectMap.clear();

        screenElementList.stream().filter(isLocalRef).forEach(convertToJson);

        if(!objectMap.isEmpty()) {
            payload = JsonHelper.update(payload, "localrefs", objectMap);
        }

        //System.err.println("--------------payload is ------------"+payload);
        return payload;
    }
}
