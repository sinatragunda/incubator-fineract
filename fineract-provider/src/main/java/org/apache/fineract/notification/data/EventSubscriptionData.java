/*

    Created by Sinatra Gunda
    At 10:42 AM on 8/24/2022

*/
package org.apache.fineract.notification.data;

import org.apache.fineract.notification.enumerations.NOTIFICATION_BROADCAST_TYPE;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

public class EventSubscriptionData {

    private final Long id ;
    private final String name ;
    private final BUSINESS_EVENTS businessEvent ;
    private final NOTIFICATION_BROADCAST_TYPE notificationType ;
    private final Long officeId ;
    private final String message ;


    public EventSubscriptionData(Long id, String name, BUSINESS_EVENTS businessEvent, NOTIFICATION_BROADCAST_TYPE notificationType, Long officeId, String message) {
        this.id = id;
        this.name = name;
        this.businessEvent = businessEvent;
        this.notificationType = notificationType;
        this.officeId = officeId;
        this.message = message;
    }
}
