/*

    Created by Sinatra Gunda
    At 5:51 AM on 8/15/2021

*/
package org.apache.fineract.portfolio.client.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import java.util.ArrayList;
import java.util.List;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient ;

@Entity
@Table(name="m_mail_recipients_key")
public class EmailRecipientsKey extends AbstractPersistableCustom<Long>{

    @Column(name="name")
    private String name ;

    @Column(name="count" ,nullable=true)
    private int count ;

    @Column(name="select_all_mode" ,nullable=true)
    private Boolean selectAllMode;

    @Column(name="office_id" ,nullable=true)
    private Long officeId;

    // added 15/06/2022
    @Column(name="is_client_event_notification" ,nullable=true)
    private Boolean isClientEventNotification;


    @Transient
    private List<EmailRecipients> emailRecipientsList = new ArrayList<>();

    public EmailRecipientsKey(){}

    public EmailRecipientsKey(Long id){
        setId(id);
    }

    public EmailRecipientsKey(String name, Boolean selectAllMode, Long officeId) {
        this.name = name;
        this.selectAllMode = selectAllMode;
        this.officeId = officeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Boolean getSelectAllMode() {
        return selectAllMode;
    }

    public void setSelectAllMode(Boolean selectAllMode) {
        this.selectAllMode = selectAllMode;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public List<EmailRecipients> getEmailRecipientsList() {
        return emailRecipientsList;
    }

    public void setEmailRecipientsList(List<EmailRecipients> emailRecipientsList) {
        this.emailRecipientsList = emailRecipientsList;
    }

}
