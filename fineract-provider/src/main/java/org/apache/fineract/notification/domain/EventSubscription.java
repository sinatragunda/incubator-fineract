/*

    Created by Sinatra Gunda
    At 2:20 PM on 7/17/2022

*/
package org.apache.fineract.notification.domain;

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
import org.apache.fineract.notification.enumerations.NOTIFICATION_BROADCAST_TYPE;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

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

    public EventSubscription(){}

    public EventSubscription(String name,BUSINESS_EVENTS businessEvents, NOTIFICATION_BROADCAST_TYPE notificationBroadcastType ,Office office ,String message) {
        this.name = name;
        this.businessEvents = businessEvents;
        this.notificationBroadcastType = notificationBroadcastType;
        this.office = office;
        this.message = message ;
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

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
