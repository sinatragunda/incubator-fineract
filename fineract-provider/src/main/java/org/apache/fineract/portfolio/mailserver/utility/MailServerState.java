/*

    Created by Sinatra Gunda
    At 2:06 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.utility;

import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.portfolio.mailserver.helper.DurationHelper;
import org.apache.fineract.portfolio.mailserver.service.MailService;
import org.apache.fineract.wese.enumerations.DURATION_TYPE;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Semaphore;

/**
 * Class purpose to handle the server state and handle the sleeping and acquiring of semaphores etc
 */

public class MailServerState {

    private Semaphore semaphore ;
    private MailServerSettings mailServerSettings;
    private static MailServerState instance = null;
    private Instant lastSentMailTime ;
    private volatile Boolean isThreadSleeping = false;
    private MailSenderQueueManager mailSenderQueueManager ;
    private static int NANO_SECONDS_PER_SECOND = 1000;
    private int maxPermits = 0;

    private MailServerState(MailServerSettings mailServerSettings){
        this.mailServerSettings = mailServerSettings;
        initPermit();
    }

    public static MailServerState getInstance(MailServerSettings mailServerSettings) {
        instance = Optional.ofNullable(instance).orElse(new MailServerState(mailServerSettings));
        return instance;
    }

    private void initPermit(){
        this.maxPermits = mailServerSettings.getLimit();
        semaphore = new Semaphore(maxPermits);
    }

    public synchronized void trySend(MailService mailService){

        boolean acquirePermit = semaphore.tryAcquire();

        System.err.println("-------------------acquire permits after sleeping ? "+acquirePermit);

        if(acquirePermit){
            permitGranted(mailService);
            return;
        }
        System.err.println("----------------------permit not granted ------------");
        permitNotGranted(mailService);
    }

    private void permitGranted(MailService mailService){

        MailContent mailContent = MailSenderQueueManager.getInstance().peekOrPoll(false);

        Optional.ofNullable(mailContent).ifPresent(e->{

            mailService.send(mailContent);

            int availablePermits = semaphore.availablePermits();

            System.err.println("----------------available permits are --------"+availablePermits);
            /**
             *No permits available now take note of current time so as to aid in synchronization of durations
             */
             if(availablePermits == 0){
                lastSentMailTime = Instant.now();
            }
        });
    }

    private void permitNotGranted(MailService mailService){

        /**
         * if thread is not sleeping then lets do some checking to sleep it based on duration of last sent message
         * Else if its sleeping we should just wait for it to wake up
         */
        Duration duration = Duration.between(lastSentMailTime ,Instant.now());

        DURATION_TYPE durationType = DURATION_TYPE.fromInt(mailServerSettings.getTimerType());

        Long durationSoFar = DurationHelper.duration(duration ,durationType);

        // time to waking up now in durationSoFar

        Long qoutaDuration = Long.valueOf(mailServerSettings.getQuotaDuration());

        //System.err.println("-----------------duration so far ? "+durationSoFar+"------------and qoutaDuration------"+qoutaDuration);

        // if(durationSoFar > qoutaDuration){

        //     System.err.println("----------------SUPPOSE TO START THREADING MAILS NOW ,send mail ");

        //     synchronized (this){
        //         System.err.println("----------------------------DRAIN PERMITS NOW ");
        //         semaphore.release(maxPermits);

        //         System.err.println("---------------PERMITS AFTER DRAINING ? "+semaphore.availablePermits());
        //         isThreadSleeping = false ;
        //     }
        //     /**
        //      * Then call recursive method of sending message again from mail sender metered
        //      */
        //     trySend(mailService);
        //     System.err.println("---------------we have recursively called trySend -------------");
        //     return ;
        // }

        int sleepTime = duration.getNano() /NANO_SECONDS_PER_SECOND;
        sleepThread(sleepTime);
        trySend(mailService);
    
    }

    private synchronized void sleepThread(int sleepTime){
        try{
            isThreadSleeping = true ;
            Thread.sleep(sleepTime);
            isThreadSleeping = false ;
            System.err.println("-----------------thread has woken up ,is not sleeping anymore ");
            wakeUpThread();
        }
        catch (InterruptedException n){
            n.printStackTrace();
        }
    }


    private void wakeUpThread(){
        semaphore.release(maxPermits);
    }




}
