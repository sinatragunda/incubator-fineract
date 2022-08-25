/*

    Created by Sinatra Gunda
    At 7:15 AM on 7/18/2022

*/
package org.apache.fineract.notification.service;

import org.apache.fineract.notification.data.EventMailListData;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

import java.util.List;

public interface EventMailListReadPlatformService {

    List<EventMailListData> retrieveWhereEvent(Long officeId ,BUSINESS_EVENTS businessEvent);
    List<EventMailListData> retrieveAll();


}
