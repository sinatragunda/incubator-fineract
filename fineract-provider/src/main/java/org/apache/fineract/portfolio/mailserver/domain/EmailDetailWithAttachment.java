/*

    Created by Sinatra Gunda
    At 8:58 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.domain;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.portfolio.mailserver.enumerations.MAIL_CONTENT_TYPE;

import java.io.File;
import java.util.List;

public class EmailDetailWithAttachment extends EmailDetail implements MailContent {

    private MAIL_CONTENT_TYPE mailContent = MAIL_CONTENT_TYPE.MEDIA;
    private List<File> attachments ;

    public EmailDetailWithAttachment(EmailDetail emailDetail ,List attachments){
        super(emailDetail);
        this.attachments = attachments;
    }

    public EmailDetailWithAttachment(String subject, String body, String address, String contactName , List<File> attachments) {
        super(subject, body, address, contactName);
        this.attachments = attachments;
    }

    public MAIL_CONTENT_TYPE mailContentType() {
        return mailContent;
    }

    public List<File> attachments() {
        return attachments;
    }

    @Override
    public EmailDetail emailDetails() {
        return this;
    }


}


