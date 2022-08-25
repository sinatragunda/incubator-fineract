/*

    Created by Sinatra Gunda
    At 9:53 AM on 8/25/2022

*/
package org.apache.fineract.notification.service;

import org.apache.fineract.notification.data.NotificationData;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

public interface NotificationEventServiceEx {

    public void trigger(BUSINESS_EVENTS businessEvents , NotificationData notificationData);
}
