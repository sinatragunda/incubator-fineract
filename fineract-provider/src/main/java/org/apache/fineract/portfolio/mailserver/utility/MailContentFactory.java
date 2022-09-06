/*

    Created by Sinatra Gunda
    At 9:05 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.utility;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.portfolio.mailserver.domain.EmailDetailEx;
import org.apache.fineract.portfolio.mailserver.domain.EmailDetailWithAttachment;
import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.portfolio.mailserver.enumerations.MAIL_CONTENT_TYPE;

import java.util.List;

public class MailContentFactory {


    public static MailContent createMailContent(MAIL_CONTENT_TYPE mailContentType , List attachments , String emailAddress , String contactName , String subject , String message){

        MailContent mailContent = null ;
        switch (mailContentType){
            case MEDIA:
                mailContent = new EmailDetailWithAttachment(subject ,message ,emailAddress ,contactName ,attachments);
                break;
            case PLAIN:
                mailContent = new EmailDetailEx(subject ,message ,emailAddress ,contactName);

        }

        return mailContent;
    }

    public static MailContent createMailContent(MAIL_CONTENT_TYPE mailContentType , List attachments ,EmailDetail emailDetail){

        MailContent mailContent = null ;
        switch (mailContentType){
            case MEDIA:
                mailContent = new EmailDetailWithAttachment(emailDetail ,attachments);
                break;
            case PLAIN:
                mailContent = new EmailDetailEx(emailDetail);

        }

        return mailContent;
    }
}
