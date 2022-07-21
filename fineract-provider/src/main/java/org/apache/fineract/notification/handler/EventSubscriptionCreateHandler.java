/*

    Created by Sinatra Gunda
    At 1:02 PM on 7/18/2022

*/
package org.apache.fineract.notification.handler;


import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.notification.service.EventSubscriptionWritePlatformService;
import org.apache.fineract.portfolio.group.service.GroupingTypesWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "EVENT_SUBSCRIPTION", action = "CREATE")
public class EventSubscriptionCreateHandler implements NewCommandSourceHandler {

    private EventSubscriptionWritePlatformService eventSubscriptionWritePlatformService;

    @Autowired
    public EventSubscriptionCreateHandler(final EventSubscriptionWritePlatformService eventSubscriptionWritePlatformService) {
        this.eventSubscriptionWritePlatformService = eventSubscriptionWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.eventSubscriptionWritePlatformService.create(command);
    }
}
