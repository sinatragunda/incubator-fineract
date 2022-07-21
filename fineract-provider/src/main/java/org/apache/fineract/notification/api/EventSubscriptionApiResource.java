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
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.notification.data.NotificationData;
import org.apache.fineract.notification.service.EventSubscriptionWritePlatformService;
import org.apache.fineract.notification.service.NotificationReadPlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.notification.data.NotificationData;
import org.apache.fineract.notification.service.NotificationReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.apache.fineract.notification.domain.EventSubscription;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("/eventsubscription")
@Component
@Scope("singleton")
public class EventSubscriptionApiResource {


    private final PlatformSecurityContext context;
    private final NotificationReadPlatformService notificationReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<EventSubscription> toApiJsonSerializer;
    private final EventSubscriptionWritePlatformService eventSubscriptionWritePlatformService;
    private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;

    @Autowired
    public EventSubscriptionApiResource(PlatformSecurityContext context,
                                   NotificationReadPlatformService notificationReadPlatformService,
                                   ApiRequestParameterHelper apiRequestParameterHelper,
                                   ToApiJsonSerializer<EventSubscription> toApiJsonSerializer ,EventSubscriptionWritePlatformService eventSubscriptionWritePlatformService ,PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService) {
        this.context = context;
        this.notificationReadPlatformService = notificationReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.eventSubscriptionWritePlatformService = eventSubscriptionWritePlatformService;
        this.commandSourceWritePlatformService = commandSourceWritePlatformService;
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
        return toApiJsonSerializer.serialize(result);
    }
}

