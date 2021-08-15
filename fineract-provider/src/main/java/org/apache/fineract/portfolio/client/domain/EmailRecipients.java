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

@Entity
@Table(name="m_mail_recipients")
public class EmailRecipients extends AbstractPersistableCustom<Long> {

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
    private EmailRecipientsKey emailRecipientsKey ;

    public EmailRecipients(){}

    public EmailRecipients(String displayName, String emailAddress, Boolean linkedToClient,Long clientId) {
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

    public EmailRecipientsKey getEmailRecipientsKey() {
        return emailRecipientsKey;
    }

    public void setEmailRecipientsKey(EmailRecipientsKey emailRecipientsKey) {
        this.emailRecipientsKey = emailRecipientsKey;
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
}
