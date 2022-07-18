/*

    Created by Sinatra Gunda
    At 1:36 AM on 7/18/2022

*/
package org.apache.fineract.notification.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="m_event_mail_list")
public class EventMailList extends AbstractPersistableCustom<Long>{

    @ManyToOne
    @JoinColumn(name = "event_subscription_id", referencedColumnName = "id", nullable = false)
    private EventSubscription eventSubscription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mail_recipient_id", referencedColumnName = "id", nullable = false)
    private List<MailRecipientsKey> mailRecipientsKey;


    public EventMailList(){}

    public EventMailList(EventSubscription eventSubscription, List<MailRecipientsKey> mailRecipientsKey) {
        this.eventSubscription = eventSubscription;
        this.mailRecipientsKey = mailRecipientsKey;
    }

    public EventSubscription getEventSubscription() {
        return eventSubscription;
    }

    public void setEventSubscription(EventSubscription eventSubscription) {
        this.eventSubscription = eventSubscription;
    }

    public List<MailRecipientsKey> getMailRecipientsKey() {
        return mailRecipientsKey;
    }

    public void setMailRecipientsKey(List<MailRecipientsKey> mailRecipientsKey) {
        this.mailRecipientsKey = mailRecipientsKey;
    }
}
