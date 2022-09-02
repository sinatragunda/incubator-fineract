/*

    Created by Sinatra Gunda
    At 8:21 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.service;

public interface MailServerSender {

    default void send(){
        //MailSenderQueueManager.getInstance().add();
    }
}
