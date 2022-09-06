/*

    Created by Sinatra Gunda
    At 8:21 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.utility;

import org.apache.fineract.portfolio.mailserver.service.MailService;

public interface MailServerSender {

    void send(MailService mailService);
}
