/*

    Created by Sinatra Gunda
    At 10:41 AM on 8/24/2022

*/
package org.apache.fineract.notification.data;

import org.apache.fineract.notification.domain.EventSubscription;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;

import java.util.List;

public class EventMailListData {

    private Long id ;
    private EventSubscriptionData eventSubscriptionData ;
    private MailRecipientsKeyData mailRecipientsKeyData ;

    public EventMailListData(Long id, EventSubscriptionData eventSubscriptionData, MailRecipientsKeyData mailRecipientsKeyData) {
        this.id = id;
        this.eventSubscriptionData = eventSubscriptionData;
        this.mailRecipientsKeyData = mailRecipientsKeyData;
    }

    public Long getId() {
        return id;
    }

    public EventSubscriptionData getEventSubscriptionData() {
        return eventSubscriptionData;
    }

    public MailRecipientsKeyData getMailRecipientsKeyData() {
        return mailRecipientsKeyData;
    }
}
