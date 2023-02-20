/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 01:35
 */
package org.apache.fineract.portfolio.localref.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.localref.data.LocalRefValueData;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.domain.LocalRefValue;

import java.util.*;
import java.util.function.Consumer;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.exception.LocalRefValueMandatoryException;
import org.apache.fineract.portfolio.localref.repo.LocalRefRepositoryWrapper;
import org.apache.fineract.portfolio.localref.repo.LocalRefValueRepositoryWrapper;

import org.apache.fineract.portfolio.localref.service.LocalRefReadPlatformService;
import org.apache.fineract.utility.domain.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

@Component
public class LocalRefRecordHelper {

    private FromJsonHelper fromJsonHelper ;
    private LocalRefRepositoryWrapper localRefRepositoryWrapper;
    private LocalRefValueRepositoryWrapper localRefValueRepositoryWrapper;
    private LocalRefReadPlatformService localRefReadPlatformService;

    @Autowired
    public LocalRefRecordHelper(FromJsonHelper fromJsonHelper, LocalRefRepositoryWrapper localRefRepositoryWrapper, LocalRefValueRepositoryWrapper localRefValueRepositoryWrapper ,LocalRefReadPlatformService localRefReadPlatformService) {
        this.fromJsonHelper = fromJsonHelper;
        this.localRefRepositoryWrapper = localRefRepositoryWrapper;
        this.localRefValueRepositoryWrapper = localRefValueRepositoryWrapper;
        this.localRefReadPlatformService = localRefReadPlatformService;
    }

    public void create(JsonCommand command ,AbstractPersistableCustom abstractPersistableCustom){
        Long recordId = abstractPersistableCustom.getId();
        create(command ,recordId);
    }

    public void create(JsonCommand command, Long recordId){

        List<LocalRefValue> localRefValueList = new ArrayList<>();
        JsonElement jsonElement = command.parsedJson();

        /**
         * Modified 02/01/2023 
         * NullPointerException thrown when creating new client in case when application has no localref
         */
        boolean hasParameter  = command.parameterExists("localrefs");

        if(!hasParameter){
            return ;
        } 

        JsonObject jsonObject = fromJsonHelper.extractJsonObjectNamed("localrefs" ,jsonElement);
        Set<Map.Entry<String ,JsonElement>> set = jsonObject.entrySet();
        for(Map.Entry<String ,JsonElement> entry : set){

            JsonElement element = entry.getValue();

            //System.err.println("----------------------------------data "+element);

            //System.err.println("---------------------entry key is "+entry.getKey());

            JsonObject data = fromJsonHelper.extractJsonObjectNamed("data" ,element);

            Set<Map.Entry<String,JsonElement>> dataSet = data.entrySet();

            //System.err.println("--------------data object is -----------"+data);

            for (Map.Entry<String,JsonElement> dataEntry : dataSet) {
                /**
                 * Some bug petaining to how keys are formed from json in angularjs so need to catch an error
                 * Cause provided key would be string instead of Lomg
                 */
                String idKey = dataEntry.getKey();
                Long localRefId = localRefId(idKey);

                String value = dataEntry.getValue().toString().replace("\"","");
                /**
                 * Consumer to populate localrefvalue
                 */
                Consumer consumer = (e)->{
                    LocalRef localRef = localRefRepositoryWrapper.findOneWithoutNotFoundDetection(localRefId);
                    validateEntry(localRef ,value);
                    LocalRefValue localRefValue = new LocalRefValue(localRef ,recordId ,value);
                    localRefValueList.add(localRefValue);
                };

                Optional.ofNullable(localRefId).ifPresent(consumer);

            }

        }

        Consumer<LocalRefValue> saveConsumer = (e)->localRefValueRepositoryWrapper.save(e);
        localRefValueList.forEach(saveConsumer);
    }

    public Long localRefId(String value){
        Long localRefId = null;
        try{
            localRefId = Long.valueOf(value);
        }
        catch (NumberFormatException n){
            System.err.println("------------eception caught "+n.getMessage());
        }
        return localRefId;
    }

    public void setRecordData(Record record, REF_TABLE refTable){
        Long recordId = record.getId();
        System.err.println("=========================record id "+recordId);
        Collection<LocalRefValueData> localRefValueDataCollection = localRefReadPlatformService.retrieveRecord(refTable ,recordId);

        System.err.println("======================values collected "+localRefValueDataCollection.size());
        record.setLocalRefValueData(localRefValueDataCollection);

        System.err.println("========================record value set ===========");

    }

    private void validateEntry(LocalRef localRef , String entry){

        //System.err.println("--------------is localref null now ? "+Optional.ofNullable(localRef).isPresent());
        boolean isPresent = Optional.ofNullable(entry).isPresent();
        boolean isMandatory = localRef.isMandatory();
        String key = localRef.getName();

        //System.err.println("-------------is mandatory "+isMandatory);
        if(isMandatory){
            if(isPresent){
                // check if string is not blank here ,need another efficient method but this is just temp
                int l = entry.length();
                //System.err.println("----------------------length is "+l);
                if(l <= 0){
                    //System.err.println("-------------throw this error son of length --");
                    throw new LocalRefValueMandatoryException(key);
                }
            }
            else{
                //System.err.println("===============errro thrown when not present =======");
                throw new LocalRefValueMandatoryException(key);
            }
        }
    }


}
