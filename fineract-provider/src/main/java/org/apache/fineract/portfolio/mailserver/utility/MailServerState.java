/*

    Created by Sinatra Gunda
    At 2:06 AM on 9/1/2022

*/
package org.apache.fineract.portfolio.mailserver.utility;

import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.apache.fineract.portfolio.mailserver.helper.DurationHelper;
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
    private static MailServerState instance ;
    private Instant lastSentMailTime ;
    private volatile Boolean isThreadSleeping = false;

    private MailServerState(MailServerSettings mailServerSettings){
        this.mailServerSettings = mailServerSettings;
        initPermit();
    }

    public static MailServerState getInstance(MailServerSettings mailServerSettings) {

        instance = Optional.ofNullable(instance).orElse(new MailServerState(mailServerSettings));
        return instance;
    }

    private void initPermit(){

        int maxPermit = mailServerSettings.getLimit();
        semaphore = new Semaphore(maxPermit);

    }

    public synchronized void trySend(){

        boolean acquirePermit = semaphore.tryAcquire();
        if(acquirePermit){
            permitGranted();
            return;
        }

        permitNotGranted();
    }

    private void permitGranted(){

        // send message here
        int availablePermits = semaphore.availablePermits();

        // no permits available now take note of current time so as to aid in synchronization of durations
        if(availablePermits == 0){
            lastSentMailTime = Instant.now();
        }
    }

    private void permitNotGranted(){

        /**
         * if thread is not sleeping then lets do some checking to sleep it based on duration of last sent message
         * Else if its sleeping we should just wait for it to wake up
         */

        if(!isThreadSleeping){

            Duration duration = Duration.between(lastSentMailTime ,Instant.now());

            DURATION_TYPE durationType = DURATION_TYPE.fromInt(mailServerSettings.getTimerType());

            Long durationSoFar = DurationHelper.duration(duration ,durationType);

            Long qoutaDuration = Long.valueOf(mailServerSettings.getQuotaDuration());

            if(durationSoFar > qoutaDuration){
                synchronized (this){
                    semaphore.drainPermits();
                    isThreadSleeping = false ;
                }
                /**
                 * Then call recursive method of sending message again from mail sender metered
                 */
                return ;
            }

            int sleepTime = duration.getNano();

            System.err.println("---------------nano second now is "+sleepTime);

            sleepThread(sleepTime);

            System.err.println("-------------------------waking up thread lets send again ");

        }
    }

    private synchronized void sleepThread(int sleepTime){
        try{
            Thread.sleep(sleepTime);
            isThreadSleeping = false ;
        }
        catch (InterruptedException n){
            n.printStackTrace();
        }
    }




}
