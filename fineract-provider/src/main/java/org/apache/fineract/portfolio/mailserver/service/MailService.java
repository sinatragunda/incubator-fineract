/*

    Created by Sinatra Gunda
    At 10:18 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.service;

import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;

public interface MailService {
    SEND_MAIL_MESSAGE_STATUS send(MailContent mailContent);
}
