/*

    Created by Sinatra Gunda
    At 7:16 AM on 7/18/2022

*/
package org.apache.fineract.notification.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.notification.constants.EventSubscriptionConstants;
import org.apache.fineract.notification.domain.EventMailList;
import org.apache.fineract.notification.domain.EventSubscription;
import org.apache.fineract.notification.enumerations.NOTIFICATION_BROADCAST_TYPE;
import org.apache.fineract.notification.repo.EventMailListRepository;
import org.apache.fineract.notification.repo.EventSubscriptionRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventSubscriptionWritePlatformServiceImpl implements EventSubscriptionWritePlatformService {


    private PlatformSecurityContext context;
    private EventSubscriptionRepository eventSubscriptionRepository ;
    private FromJsonHelper fromJsonHelper;
    private OfficeRepositoryWrapper officeRepositoryWrapper;
    private EventMailListRepository eventMailListRepository;


    @Autowired
    public EventSubscriptionWritePlatformServiceImpl(PlatformSecurityContext context, EventSubscriptionRepository eventSubscriptionRepository, FromJsonHelper fromJsonHelper, OfficeRepositoryWrapper officeRepositoryWrapper, EventMailListRepository eventMailListRepository) {
        this.context = context;
        this.eventSubscriptionRepository = eventSubscriptionRepository;
        this.fromJsonHelper = fromJsonHelper;
        this.officeRepositoryWrapper = officeRepositoryWrapper;
        this.eventMailListRepository = eventMailListRepository;
    }

    public CommandProcessingResult create(JsonCommand jsonCommand){

        context.authenticatedUser();

        // add some validation class here
        final String name = jsonCommand.stringValueOfParameterNamed("name");
        final Integer businessEventsInts = jsonCommand.integerValueOfParameterNamed(EventSubscriptionConstants.businessEventsParam);
        final Integer notificationBroadcastTypeInt = jsonCommand.integerValueOfParameterNamed(EventSubscriptionConstants.notificationBroadcastTypeParam);
        final String message = jsonCommand.stringValueOfParameterNamed(EventSubscriptionConstants.messageParam);
        final Long officeId = jsonCommand.longValueOfParameterNamed(EventSubscriptionConstants.officeIdParam);

        final Office office = officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);


        //JsonObject eventMailListObject =  fromJsonHelper.extractJsonObjectNamed(EventSubscriptionConstants.eventMailListParam ,jsonCommand.parsedJson());

        //JsonElement eventMailListJsonElement = fromJsonHelper.parse(eventMailListObject.toString());

        // some confusion over what to do here maybe call each class and be responsible for writing its own data
        // fromJsonHelper.extractLongNamed()

        final BUSINESS_EVENTS businessEvents = BUSINESS_EVENTS.fromInt(businessEventsInts);
        final NOTIFICATION_BROADCAST_TYPE notificationBroadcastType = NOTIFICATION_BROADCAST_TYPE.fromInt(notificationBroadcastTypeInt);

        final EventSubscription eventSubscription = new EventSubscription(name ,businessEvents ,notificationBroadcastType ,office ,message ,null);

        Optional.ofNullable(eventSubscription).ifPresent(e->{

            eventSubscriptionRepository.save(eventSubscription);
            EventMailList eventMailList = new EventMailList(eventSubscription ,null);

            eventMailListRepository.save(eventMailList);
        });

        /// at this point it should have an id

    }


}
