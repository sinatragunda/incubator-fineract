/*

    Created by Sinatra Gunda
    At 5:23 AM on 8/14/2021

*/
package org.apache.fineract.portfolio.client.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// added 26/08/2021
import javax.persistence.Transient;

@Entity
@Table(name="m_mail_recipients")
public class MailRecipients extends AbstractPersistableCustom<Long> {

    @Column(name ="name")
    private String name ;

    @Column(name="email_address")
    private String emailAddress ;

    @Column(name="linked_to_client")
    private Boolean linkedToClient;

    @Column(name="client_id" ,nullable=true)
    private Long clientId ;

    @ManyToOne
    @JoinColumn(name="mail_recipients_key_id")
    private MailRecipientsKey mailRecipientsKey;


    // added 26/08/2021
    @Transient
    private boolean deliveryStatus ;

    public MailRecipients(){}

    public MailRecipients(String displayName, String emailAddress, Boolean linkedToClient, Long clientId) {
        this.name = displayName;
        this.emailAddress = emailAddress;
        this.linkedToClient = linkedToClient;
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MailRecipientsKey getMailRecipientsKey() {
        return mailRecipientsKey;
    }

    public void setMailRecipientsKey(MailRecipientsKey mailRecipientsKey) {
        this.mailRecipientsKey = mailRecipientsKey;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean getLinkedToClient() {
        return linkedToClient;
    }

    public void setLinkedToClient(Boolean linkedToClient) {
        this.linkedToClient = linkedToClient;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setDeliveryStatus(boolean status){
        this.deliveryStatus = status;
    }

    public boolean isDeliveryStatus(){
        return this.deliveryStatus;
    }
}
