/*

    Created by Sinatra Gunda
    At 8:40 AM on 7/18/2022

*/
package org.apache.fineract.notification.api;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.hooks.data.Event;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.notification.constants.EventSubscriptionConstants;
import org.apache.fineract.notification.data.EventMailListData;
import org.apache.fineract.notification.domain.EventMailList;
import org.apache.fineract.notification.service.EventMailListReadPlatformService;
import org.apache.fineract.notification.service.EventMailListWritePlatformService;
import org.apache.fineract.notification.service.NotificationReadPlatformService;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.apache.fineract.notification.domain.EventSubscription;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import javax.ws.rs.Path;


@Path("/eventsubscription")
@Component
@Scope("singleton")
public class EventSubscriptionApiResource {


    private final PlatformSecurityContext context;
    private final NotificationReadPlatformService notificationReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    //private final ToApiJsonSerializer<EventSubscription> toApiJsonSerializer;
    private final ToApiJsonSerializer<EventMailListData> eventMailListSerializer;
    private final ToApiJsonSerializer<EnumOptionData> templateSerializer;
    private final EventMailListWritePlatformService eventMailListWritePlatformService;
    private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
    private final EventMailListReadPlatformService eventMailListReadPlatformService;


    @Autowired
    public EventSubscriptionApiResource(PlatformSecurityContext context,
                                        NotificationReadPlatformService notificationReadPlatformService,
                                        ApiRequestParameterHelper apiRequestParameterHelper,
                                        ToApiJsonSerializer<EventMailListData> eventMailListSerializer , final EventMailListWritePlatformService eventMailListWritePlatformService,final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService ,final ToApiJsonSerializer templateSerializer ,final EventMailListReadPlatformService eventMailListReadPlatformService) {
        this.context = context;
        this.notificationReadPlatformService = notificationReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.eventMailListSerializer = eventMailListSerializer;
        this.eventMailListWritePlatformService = eventMailListWritePlatformService;
        this.commandSourceWritePlatformService = commandSourceWritePlatformService;
        this.templateSerializer = templateSerializer ;

        // added 25/08/2022
        this.eventMailListReadPlatformService = eventMailListReadPlatformService;
    }


    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String create(String apiRequestBody) {

        this.context.authenticatedUser();

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createEventSubscription() //
                .withJson(apiRequestBody) //
                .build(); //

        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
        return eventMailListSerializer.serialize(result);
    }


    @Path("/template")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(EventSubscriptionConstants.resourceNameForPermissions);

        final List<EnumOptionData> businessEvents = BusinessEventNotificationConstants.businessEventEnumOption();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.templateSerializer.serialize(settings, businessEvents, EventSubscriptionConstants.DATA_PARAMETERS);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(EventSubscriptionConstants.resourceNameForPermissions);

        final List<EventMailListData> eventMailListList = eventMailListReadPlatformService.retrieveAll();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.eventMailListSerializer.serialize(settings, eventMailListList, EventSubscriptionConstants.DATA_PARAMETERS);
    }



}

