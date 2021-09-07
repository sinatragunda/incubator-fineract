/*

    Created by Sinatra Gunda
    At 6:22 AM on 8/27/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.portfolio.excel.service.IExcelExportable;


// Added 06/09/2021

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient ;


@Entity
@Table(name="m_email_send_status")
public class EmailSendStatus extends AbstractPersistableCustom<Long> implements IExcelExportable{

    @Transient
    private SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus ;

    @Transient
    private EmailDetail emailDetail;

    @Column(name ="name")
    String name;

    @Column(name ="email_address")
    String emailAddress;

    @Column(name ="status")
    String status;

    @ManyToOne
    @JoinColumn(name="scheduled_mail_session_id")
    private ScheduledMailSession scheduledMailSession ;

    public EmailSendStatus(){

        System.err.println("-------init must be called here");
        //emailDetail = new EmailDetail();
    }

    public EmailSendStatus(EmailDetail emailDetail ,SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus) {
        this.sendMailMessageStatus = sendMailMessageStatus;
        this.emailDetail = emailDetail;
        this.name = emailDetail.getContactName();
        this.emailAddress = emailDetail.getAddress();
        this.status = sendMailMessageStatus.name();
    }

    public ScheduledMailSession getScheduledMailSession() {
        return scheduledMailSession;
    }

    public void setScheduledMailSession(ScheduledMailSession scheduledMailSession) {
        this.scheduledMailSession = scheduledMailSession;
    }

    public SEND_MAIL_MESSAGE_STATUS getSendMailMessageStatus() {
        return sendMailMessageStatus;
    }

    public void setSendMailMessageStatus(SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus) {
        this.sendMailMessageStatus = sendMailMessageStatus;
    }

    public EmailDetail getEmailDetail() {
        return emailDetail;
    }

    public void setEmailDetail(EmailDetail emailDetail) {
        this.emailDetail = emailDetail;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Object[] excelHeader() {

        return new Object[]{
                "#",
                "Contact Name",
                "Receipient",
                "Status"
        };
    }

    @Override
    public Object[] excelTemplate() {
        return new Object[]{
                "",
                name,
                emailAddress,
                status
        };
    }
}
