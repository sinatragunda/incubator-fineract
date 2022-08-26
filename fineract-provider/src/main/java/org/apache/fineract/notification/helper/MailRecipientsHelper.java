/*

    Created by Sinatra Gunda
    At 9:12 AM on 8/15/2021

*/
package org.apache.fineract.notification.helper;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.MailRecipients;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;
import org.apache.fineract.portfolio.client.repo.MailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.MailRecipientsRepository;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;

import java.util.*;
import java.util.function.Consumer;

public class MailRecipientsHelper {

    public static Long createMailRecipients(MailRecipientsKeyRepository mailRecipientsKeyRepository, MailRecipientsRepository mailRecipientsRepository, MailRecipientsKey mailRecipientsKey){

        int count = mailRecipientsKey.getMailRecipientsList().size();
        mailRecipientsKey.setCount(count);
        mailRecipientsKeyRepository.saveAndFlush(mailRecipientsKey);

        /// we need to do a whole lot of logics here
        List<MailRecipients> mailRecipientsList = mailRecipientsKey.getMailRecipientsList();

        for(MailRecipients mailRecipients : mailRecipientsList){
            mailRecipients.setMailRecipientsKey(mailRecipientsKey);
            mailRecipientsRepository.save(mailRecipients);
        }

        Long id = mailRecipientsKey.getId();
        return id ;
    }

    public static Queue<MailRecipients> emailRecipients(MailRecipientsKeyRepository mailRecipientsKeyRepository, MailRecipientsRepository mailRecipientsRepository, ClientReadPlatformService clientReadPlatformService, Long keyId){


        System.err.println("---------------------key id is --------------------"+keyId);

        MailRecipientsKey mailRecipientsKey = mailRecipientsKeyRepository.findOne(keyId);

        boolean isPresent = Optional.ofNullable(mailRecipientsKey).isPresent();

        Queue<MailRecipients> mailRecipientsQueue = new LinkedList<>();

        if(!isPresent){
            /**
             * If MailRecipientsKey record is not present then just return with empty queue .Since it will be streamed if its blank function will skio
             */
            return mailRecipientsQueue;
        }

        boolean selectAllMode = mailRecipientsKey.getSelectAllMode();

        if(selectAllMode){

            Long officeId = mailRecipientsKey.getOfficeId();
            
            // we need to implement new nullable stuff here

            // Some stupid glitch exists here ,what was the glitch now ? .Am forgetting now

            System.err.println("---------------------------office id is ----------------------"+officeId);
            
            boolean officeIdPresent = Optional.ofNullable(officeId).isPresent();

            Consumer<MailRecipients> addNewToQueue = (e)-> mailRecipientsQueue.add(e);
                    
            if(!officeIdPresent){

                // We taking all clients since no office has been specified
                Page<ClientData> clientDataList =  clientReadPlatformService.retrieveAll(null);
                Consumer<ClientData> mailRecipientsConsumer = (clientData) ->{
                    MailRecipients mailRecipients = createMailRecipientObject(clientData);
                    Optional.ofNullable(mailRecipients).ifPresent(addNewToQueue);
                };
                clientDataList.getPageItems().stream().forEach(mailRecipientsConsumer);
            }
            else{

                //String lookUpCriteria = String.format("c.office_id=%d",officeId);
                /**
                 * Initial function retrieveAllForLookup with office criteria been changed after failing some sql injection validation
                 * Probably malformed criteria string
                 * Dated 25/08/2022
                 */
                Collection<ClientData> clientDataList = clientReadPlatformService.retrieveAllForLookupByOfficeId(officeId);

                Consumer<ClientData> clientDataConsumer = (clientData)->{

                    MailRecipients mailRecipients = createMailRecipientObject(clientData);
                    Optional.ofNullable(mailRecipients).ifPresent(addNewToQueue);

                };
                clientDataList.stream().forEach(clientDataConsumer);
            }

            /// update email addressed here son

            Consumer<MailRecipients> updateEmails = (e)->{
                Long clientId = e.getClientId();
                ClientData clientData = clientReadPlatformService.retrieveOne(clientId);
                String emailCurrent = clientData.getEmailAddress();
                e.setEmailAddress(emailCurrent);
            };

            // is it necessary since we taking data from client readplarform on retrieving already ?
            // mailRecipientsQueue.stream().forEach(updateEmails);
            return mailRecipientsQueue ;
        }

        List<MailRecipients> mailRecipientsList = mailRecipientsRepository.findByMailRecipientsKeyId(keyId);
        mailRecipientsList.stream().forEach((e)->{
            mailRecipientsQueue.add(e);
        });
        
        return mailRecipientsQueue ;
    }

    public static MailRecipients createMailRecipientObject(ClientData clientData){

        String name = clientData.displayName();
        String email = clientData.getEmailAddress();
        Long clientId = clientData.getId();

        return new MailRecipients(name ,email ,true ,clientId);

    }
}
