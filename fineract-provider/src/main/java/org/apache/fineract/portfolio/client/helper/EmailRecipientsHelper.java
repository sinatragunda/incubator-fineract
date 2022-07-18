/*

    Created by Sinatra Gunda
    At 9:12 AM on 8/15/2021

*/
package org.apache.fineract.portfolio.client.helper;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.MailRecipients;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;
import org.apache.fineract.portfolio.client.repo.EmailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.EmailRecipientsRepository;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;

import java.util.*;
import java.util.function.Consumer;

public class EmailRecipientsHelper {

    public static Long createMailRecipients(EmailRecipientsKeyRepository emailRecipientsKeyRepository , EmailRecipientsRepository emailRecipientsRepository , MailRecipientsKey mailRecipientsKey){

        int count = mailRecipientsKey.getMailRecipientsList().size();
        mailRecipientsKey.setCount(count);
        emailRecipientsKeyRepository.saveAndFlush(mailRecipientsKey);

        /// we need to do a whole lot of logics here
        List<MailRecipients> mailRecipientsList = mailRecipientsKey.getMailRecipientsList();

        for(MailRecipients mailRecipients : mailRecipientsList){
            mailRecipients.setMailRecipientsKey(mailRecipientsKey);
            emailRecipientsRepository.save(mailRecipients);
        }

        Long id = mailRecipientsKey.getId();
        return id ;
    }

    public static Queue<MailRecipients> emailRecipients(EmailRecipientsKeyRepository emailRecipientsKeyRepository , EmailRecipientsRepository emailRecipientsRepository , ClientReadPlatformService clientReadPlatformService, Long keyId){

        MailRecipientsKey mailRecipientsKey = emailRecipientsKeyRepository.findOne(keyId);

        // if true then get office id and fill lists with recipients of clients
        boolean selectAllMode = mailRecipientsKey.getSelectAllMode();
        Queue<MailRecipients> mailRecipientsQueue = new LinkedList<>();

        if(selectAllMode){

            Long officeId = mailRecipientsKey.getOfficeId();
            
            // we need to implement new nullable stuff here

            // some stupid glitch exists here
            
            
            boolean officeIdPresent = Optional.ofNullable(officeId).isPresent();

            Consumer<MailRecipients> addNewToQueue = (e)-> mailRecipientsQueue.add(e);
                    
            if(!officeIdPresent){
                //we taking all clients since no office has been specified

                Page<ClientData> clientDataList =  clientReadPlatformService.retrieveAll(null);
                Consumer<ClientData> mailRecipientsConsumer = (clientData) ->{
                    MailRecipients mailRecipients = createMailRecipientObject(clientData);
                    //Consumer<MailRecipients> addNew = (e)-> mailRecipientsQueue.put(e);
                    Optional.ofNullable(mailRecipients).ifPresent(addNewToQueue);
                };

                clientDataList.getPageItems().stream().forEach(mailRecipientsConsumer);
            }
            else{

                String lookUpCriteria = String.format("office_id=%d",officeId);
                Collection<ClientData> clientDataList = clientReadPlatformService.retrieveAllForLookup(lookUpCriteria);
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

        List<MailRecipients> mailRecipientsList = emailRecipientsRepository.findByEmailRecipientsKeyId(keyId);
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
