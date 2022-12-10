/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 01:35
 */
package org.apache.fineract.portfolio.localref.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.domain.LocalRefValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import org.apache.fineract.portfolio.localref.repo.LocalRefRepositoryWrapper;
import org.apache.fineract.portfolio.localref.repo.LocalRefValueRepositoryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

@Component
public class LocalRefRecordHelper {

    private FromJsonHelper fromJsonHelper ;
    private LocalRefRepositoryWrapper localRefRepositoryWrapper;
    private LocalRefValueRepositoryWrapper localRefValueRepositoryWrapper;

    @Autowired
    public LocalRefRecordHelper(FromJsonHelper fromJsonHelper, LocalRefRepositoryWrapper localRefRepositoryWrapper, LocalRefValueRepositoryWrapper localRefValueRepositoryWrapper) {
        this.fromJsonHelper = fromJsonHelper;
        this.localRefRepositoryWrapper = localRefRepositoryWrapper;
        this.localRefValueRepositoryWrapper = localRefValueRepositoryWrapper;
    }

    public void create(JsonCommand command ,AbstractPersistableCustom abstractPersistableCustom){
        Long recordId = abstractPersistableCustom.getId();
        create(command ,recordId);
    }

    public void create(JsonCommand command, Long recordId){

        List<LocalRefValue> localRefValueList = new ArrayList<>();
        JsonElement jsonElement = command.parsedJson();
        JsonArray jsonArray = fromJsonHelper.extractJsonArrayNamed("localrefs" ,jsonElement);

        for(JsonElement element : jsonArray){

            String value = fromJsonHelper.extractStringNamed(LocalRefConstants.refValueParam ,element);
            Long localRefId = fromJsonHelper.extractLongNamed(LocalRefConstants.localRefIdParam ,element);

            System.err.println("---------------------value is --------------"+value);

            LocalRef localRef = localRefRepositoryWrapper.findOneWithoutNotFoundDetection(localRefId);

            LocalRefValue localRefValue = new LocalRefValue(localRef ,recordId ,value);
            localRefValueList.add(localRefValue);
        }

        Consumer<LocalRefValue> saveConsumer = (e)->localRefValueRepositoryWrapper.save(e);
        localRefValueList.forEach(saveConsumer);
    }
}
