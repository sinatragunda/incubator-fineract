/*

    Created by Sinatra Gunda
    At 1:36 AM on 7/18/2022

*/
package org.apache.fineract.notification.domain;


import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;

@Entity
@Table(name="m_event_mail_list")
public class EventMailList extends AbstractPersistableCustom<Long>{

    @ManyToOne
    @JoinColumn(name = "event_subscription_id",nullable = false)
    private EventSubscription eventSubscription;

    @ManyToOne
    @JoinColumn(name = "mail_recipient_key_id", nullable = false)
    private MailRecipientsKey mailRecipientsKey;

    @Transient
    private List<MailRecipientsKey> mailRecipientsKeyList ;


    public EventMailList(){}

    public EventMailList(EventSubscription eventSubscription, MailRecipientsKey mailRecipientsKey) {
        this.eventSubscription = eventSubscription;
        this.mailRecipientsKey = mailRecipientsKey;
    }

    public EventSubscription getEventSubscription() {
        return eventSubscription;
    }

    public void setEventSubscription(EventSubscription eventSubscription) {
        this.eventSubscription = eventSubscription;
    }

    public MailRecipientsKey getMailRecipientsKey() {
        return mailRecipientsKey;
    }

    public void setMailRecipientsKey(MailRecipientsKey mailRecipientsKey) {
        this.mailRecipientsKey = mailRecipientsKey;
    }
}
