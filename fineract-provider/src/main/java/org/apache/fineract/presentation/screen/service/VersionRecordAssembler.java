/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 08:43
 */
package org.apache.fineract.presentation.screen.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.helper.ScreenApiConstant;
import org.apache.fineract.presentation.screen.repo.ScreenElementRepositoryWrapper;

import java.util.HashSet;
import java.util.Set;

import org.apache.fineract.presentation.screen.repo.ScreenRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Service
public class VersionRecordAssembler {

    private final FromJsonHelper fromJsonHelper ;
    private final ScreenElementRepositoryWrapper screenElementRepositoryWrapper;
    private final ScreenRepositoryWrapper screenRepositoryWrapper;

    @Autowired
    public VersionRecordAssembler(final FromJsonHelper fromJsonHelper, final ScreenElementRepositoryWrapper screenElementRepositoryWrapper , final ScreenRepositoryWrapper screenRepositoryWrapper) {
        this.fromJsonHelper = fromJsonHelper;
        this.screenElementRepositoryWrapper = screenElementRepositoryWrapper;
        this.screenRepositoryWrapper = screenRepositoryWrapper;
    }

    /**
     * Added 17/04/2023 at 0845
      * Our model name is the key which will use to find the attribute data
     */
    public Screen assembleFromJson(JsonCommand command){

        JsonObject jsonObject = command.parsedJson().getAsJsonObject();
        Set<String> keySet = jsonObject.keySet();
        Set<ScreenElement> screenElementsSet = new HashSet<>();

        Long screenId = command.longValueOfParameterNamed(ScreenApiConstant.screenIdParam);

        Screen screen = screenRepositoryWrapper.findOneWithNotFoundDetection(screenId);

        //System.err.println("--------------we now have screen repository");

        for(String model : keySet){
            JsonElement modelElement = command.jsonElementOfParamNamed(model);
            //System.err.println("----------keyset val is "+model);

            if(model.equalsIgnoreCase(ScreenApiConstant.screenIdParam)){
                //System.err.println("---------------id value for screen id skip it from contentisn");
                continue;
            }
            ScreenElement screenElement = screenElementFromJson(modelElement );
            screenElementsSet.add(screenElement);
        }
        System.err.println("-----------------------elements from supplied data are "+screenElementsSet.size());

        screen.setScreenElementSet(screenElementsSet);
        return screen;
    }

    private ScreenElement screenElementFromJson(JsonElement element){

        System.err.println("---------------screen element json  --------"+element);

        Long id = fromJsonHelper.extractLongNamed(GenericConstants.idParam,element);
        String parameter = fromJsonHelper.extractStringNamed(ScreenApiConstant.valueParam ,element);
        ScreenElement screenElement = screenElementRepositoryWrapper.findOneWithNotFoundDetection(id);
        screenElement.setParameter(parameter);

        System.err.println("--------------------------extracted value is "+parameter);


        System.err.println("---------------screen element object string   --------"+screenElement);

        return screenElement;
    }
}
