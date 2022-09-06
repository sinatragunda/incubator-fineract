/*

    Created by Sinatra Gunda
    At 8:48 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.utility;

import org.apache.fineract.portfolio.mailserver.domain.MailContent;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MailSenderQueueManager {

    private Queue<MailContent> mailDataQueue ;
    private static MailSenderQueueManager instance ;


    private MailSenderQueueManager(){
        mailDataQueue = new ConcurrentLinkedQueue<>();

    }

    public static MailSenderQueueManager getInstance() {
        instance = Optional.ofNullable(instance).orElseGet(MailSenderQueueManager::new);
        return instance;
    }

    public void add(MailContent mailContent){
        mailDataQueue.add(mailContent);
    }

    public MailContent peekOrPoll(boolean peek){
        if(peek){
            return mailDataQueue.peek();
        }
        return mailDataQueue.poll();
    }

    public boolean isQueueEmpty(){
        return mailDataQueue.isEmpty();
    }
}
