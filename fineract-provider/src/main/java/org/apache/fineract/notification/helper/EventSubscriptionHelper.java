/*

    Created by Sinatra Gunda
    At 9:20 AM on 8/25/2022

*/
package org.apache.fineract.notification.helper;

import org.apache.fineract.notification.data.EventMailListData;
import org.apache.fineract.notification.data.MailRecipientsKeyData;
import org.apache.fineract.notification.domain.EventMailList;
import org.apache.fineract.notification.service.EventMailListReadPlatformService;
import org.apache.fineract.portfolio.client.domain.MailRecipients;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;
import org.apache.fineract.portfolio.client.repo.MailRecipientsRepository;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

import java.util.List;
import java.util.function.Predicate;

public class EventSubscriptionHelper {





    public static List<EventMailListData> getSubscribersForEvent(EventMailListReadPlatformService eventMailListReadPlatformService , BUSINESS_EVENTS businessEvents ,Long officeId){

        List<EventMailListData> eventMailListList =  eventMailListReadPlatformService.retrieveWhereEvent(officeId ,businessEvents);

        System.err.println("-----------------------output size is "+eventMailListList.size());
        return eventMailListList ;

    }


    public static boolean isClientSubscribedForEvent(MailRecipientsRepository mailRecipientsRepository ,EventMailListData eventMailListData , Long clientId){

        MailRecipientsKeyData mailRecipientsKeyData = eventMailListData.getMailRecipientsKeyData();
        boolean isSelectAll = mailRecipientsKeyData.isSelectAllMode();

        if(isSelectAll){
          return true ;
        }

        Long keyId = mailRecipientsKeyData.getId();

        // if its not select all then hunt for client id
        List<MailRecipients> mailRecipientsList = mailRecipientsRepository.findByMailRecipientsKeyId(keyId);

        Predicate<MailRecipients> isClientPartOf = (e)->{
            return e.getClientId().equals(clientId);
        };

        // find if client is part of this group
        boolean isPresent = mailRecipientsList.stream().filter(isClientPartOf).findFirst().isPresent();

        return isPresent ;

    }


}
