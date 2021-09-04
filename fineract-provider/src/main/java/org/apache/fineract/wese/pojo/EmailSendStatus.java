/*

    Created by Sinatra Gunda
    At 6:22 AM on 8/27/2021

*/
package org.apache.fineract.wese.pojo;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;

public class EmailSendStatus {

    private SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus ;
    private EmailDetail emailDetail;

    public EmailSendStatus(){}

    public EmailSendStatus(SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus, EmailDetail emailDetail) {
        this.sendMailMessageStatus = sendMailMessageStatus;
        this.emailDetail = emailDetail;
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
}
