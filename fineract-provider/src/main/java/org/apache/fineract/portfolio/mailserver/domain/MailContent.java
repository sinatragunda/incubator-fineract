/*

    Created by Sinatra Gunda
    At 8:48 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.domain;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.portfolio.mailserver.enumerations.MAIL_CONTENT_TYPE;

import java.io.File;
import java.util.List;

public interface MailContent {
    MAIL_CONTENT_TYPE mailContentType();
    EmailDetail emailDetails();
    List<File> attachments();
}
