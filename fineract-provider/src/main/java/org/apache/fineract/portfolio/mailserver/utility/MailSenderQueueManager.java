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

    public static MailSenderQueueManager getInstance(){
        //System.err.println("-------------is instance null or "+Optional.ofNullable(instance).isPresent());
        instance = Optional.ofNullable(instance).orElseGet(MailSenderQueueManager::new);
        return instance;
    }

    public void add(MailContent mailContent){
        //System.err.println("-------------------------add another thing to mail content");
        synchronized (mailDataQueue) {
            mailDataQueue.add(mailContent);
            //System.err.println("-------------whose calling this function ? ");
        }
    }

    public MailContent peekOrPoll(boolean peek){
        
        MailContent mailContent = null;
        
        synchronized(mailDataQueue){
            //System.err.println("----------------mail sender size is "+mailDataQueue.size()+"--------------"+peek);
            if(peek){
                mailContent = mailDataQueue.peek();
            }
            else{
                mailContent = mailDataQueue.poll();
                //System.err.println("----------------mail sender size after polling "+mailDataQueue.size());
            }
        }

        return mailContent;
    }

    public boolean isQueueEmpty(){
        return mailDataQueue.isEmpty();
    }
}
