/*

    Created by Sinatra Gunda
    At 2:20 PM on 7/17/2022

*/
package org.apache.fineract.notification.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.notification.enumerations.NOTIFICATION_BROADCAST_TYPE;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name ="m_event_subscription")
public class EventSubscription extends AbstractPersistableCustom<Long>{

    @Column(name="name")
    private String name ;

    @Column(name="business_event")
    private BUSINESS_EVENTS businessEvents;

    @Column(name="notification_broadcast_type")
    private NOTIFICATION_BROADCAST_TYPE notificationBroadcastType;

    @ManyToOne
    @JoinColumn(name="office_id" ,nullable=false)
    private Office office ;

    @Column(name="message")
    private String message ;

    @OneToMany(mappedBy="eventMailList")
    private EventMailList eventMailList ;

    public EventSubscription(){}

    public EventSubscription(String name,BUSINESS_EVENTS businessEvents, NOTIFICATION_BROADCAST_TYPE notificationBroadcastType ,Office office ,String message ,EventMailList eventMailList) {
        this.name = name;
        this.businessEvents = businessEvents;
        this.notificationBroadcastType = notificationBroadcastType;
        this.office = office;
        this.message = message ;
        this.eventMailList = eventMailList ;
    }

    public BUSINESS_EVENTS getBusinessEvents() {
        return businessEvents;
    }

    public void setBusinessEvents(BUSINESS_EVENTS businessEvents) {
        this.businessEvents = businessEvents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public NOTIFICATION_BROADCAST_TYPE getNotificationBroadcastType() {
        return notificationBroadcastType;
    }

    public void setNotificationBroadcastType(NOTIFICATION_BROADCAST_TYPE notificationBroadcastType) {
        this.notificationBroadcastType = notificationBroadcastType;
    }
}
