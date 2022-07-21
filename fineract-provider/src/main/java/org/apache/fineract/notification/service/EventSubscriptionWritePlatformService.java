/*

    Created by Sinatra Gunda
    At 7:16 AM on 7/18/2022

*/
package org.apache.fineract.notification.service;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;


public interface EventSubscriptionWritePlatformService {

    CommandProcessingResult create(JsonCommand command);
}
