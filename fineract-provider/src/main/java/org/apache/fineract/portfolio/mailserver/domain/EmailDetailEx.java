/*

    Created by Sinatra Gunda
    At 8:56 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.domain;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.portfolio.mailserver.enumerations.MAIL_CONTENT_TYPE;

import java.io.File;
import java.util.List;

public class EmailDetailEx extends EmailDetail implements MailContent  {

    private MAIL_CONTENT_TYPE mailContentType = MAIL_CONTENT_TYPE.PLAIN;

    public EmailDetailEx(EmailDetail emailDetail){
        super(emailDetail);
    }

    public EmailDetailEx(String subject, String body, String address, String contactName) {
        super(subject, body, address, contactName);
    }


    @Override
    public EmailDetail emailDetails() {
        return this;
    }

    @Override
    public MAIL_CONTENT_TYPE mailContentType() {
        return mailContentType;
    }

    @Override
    public List<File> attachments() {
        return null;
    }
}
