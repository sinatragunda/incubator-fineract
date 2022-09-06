/*

    Created by Sinatra Gunda
    At 7:16 AM on 7/18/2022

*/
package org.apache.fineract.notification.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
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
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;
import org.apache.fineract.portfolio.client.repo.MailRecipientsKeyRepository;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;



@Service
public class EventMailListWritePlatformServiceImpl implements EventMailListWritePlatformService {


    private final PlatformSecurityContext context;
    private final EventSubscriptionRepository eventSubscriptionRepository ;
    private final FromJsonHelper fromJsonHelper;
    private final OfficeRepositoryWrapper officeRepositoryWrapper;
    private final EventMailListRepository eventMailListRepository;
    private final MailRecipientsKeyRepository mailRecipientsKeyRepository ;


    @Autowired
    public EventMailListWritePlatformServiceImpl(PlatformSecurityContext context, EventSubscriptionRepository eventSubscriptionRepository, FromJsonHelper fromJsonHelper, OfficeRepositoryWrapper officeRepositoryWrapper, EventMailListRepository eventMailListRepository ,final MailRecipientsKeyRepository mailRecipientsKeyRepository) {
        this.context = context;
        this.eventSubscriptionRepository = eventSubscriptionRepository;
        this.fromJsonHelper = fromJsonHelper;
        this.officeRepositoryWrapper = officeRepositoryWrapper;
        this.eventMailListRepository = eventMailListRepository;
        this.mailRecipientsKeyRepository = mailRecipientsKeyRepository;
    }

    public CommandProcessingResult create(JsonCommand jsonCommand){

        context.authenticatedUser();

        // add some validation class here

        final String name = jsonCommand.stringValueOfParameterNamed("name");
        final Integer businessEventsInts = jsonCommand.integerValueOfParameterNamed(EventSubscriptionConstants.businessEventsParam);
        final Integer notificationBroadcastTypeInt = jsonCommand.integerValueOfParameterNamed(EventSubscriptionConstants.notificationBroadcastTypeParam);
        
        final String message = jsonCommand.stringValueOfParameterNamed(EventSubscriptionConstants.messageParam);
        final Long officeId = jsonCommand.longValueOfParameterNamed(EventSubscriptionConstants.officeIdParam);

        final Office[] office = {null} ;

        Optional.ofNullable(officeId).ifPresent(e->{
            office[0] = officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);

        });

        JsonElement jsonElement = jsonCommand.parsedJson();
        JsonArray jsonArray = fromJsonHelper.extractJsonArrayNamed(EventSubscriptionConstants.eventMailListParam ,jsonElement);

        Iterator<JsonElement> jsonElementIterator = jsonArray.iterator();
        List<MailRecipientsKey> mailRecipientsKeyList = new ArrayList<>();

        while(jsonElementIterator.hasNext()){

            JsonElement json = jsonElementIterator.next();
            Long id = fromJsonHelper.extractLongNamed("id",json);
            // to change function here to wrraper
            MailRecipientsKey mailRecipientsKey = mailRecipientsKeyRepository.findOne(id);

            Optional.ofNullable(mailRecipientsKey).ifPresent(e->{
                mailRecipientsKeyList.add(e);
            });

        }

        // some confusion over what to do here maybe call each class and be responsible for writing its own data
        // fromJsonHelper.extractLongNamed()

        final BUSINESS_EVENTS businessEvents = BUSINESS_EVENTS.fromInt(businessEventsInts);

        final NOTIFICATION_BROADCAST_TYPE notificationBroadcastType = NOTIFICATION_BROADCAST_TYPE.fromInt(notificationBroadcastTypeInt);

        System.err.println("-------------------------------------notificationBroadcast type is -------------"+notificationBroadcastType);

        System.err.println("------------------is office there "+Optional.ofNullable(office[0]).isPresent());

        System.err.println("------------------is name there "+Optional.ofNullable(name).isPresent());

        System.err.println("------------------is businessEvent there "+Optional.ofNullable(businessEvents).isPresent());

        System.err.println("------------------is message there  "+Optional.ofNullable(message).isPresent());

        final EventSubscription eventSubscription = new EventSubscription(name, businessEvents, notificationBroadcastType, office[0], message);


        Optional.ofNullable(eventSubscription).ifPresent(e->{

            if(!mailRecipientsKeyList.isEmpty()) {

                eventSubscriptionRepository.save(e);

                System.err.println("------------------write first eleement  ,now has id ? --------"+e.getId());

                for(MailRecipientsKey mailRecipientsKey : mailRecipientsKeyList){

                    System.err.println("======================prepare list============"+mailRecipientsKey.getId());
                    EventMailList eventMailList = new EventMailList(e, mailRecipientsKey);
                    eventMailListRepository.save(eventMailList);

                    System.err.println("--------------second item");
                }
            }
        });

        /// at this point it should have an id
        CommandProcessingResult result = new CommandProcessingResultBuilder().withEntityId(eventSubscription.getId()).build();
        return result;
    }


}
