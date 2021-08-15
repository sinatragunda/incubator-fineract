/*

    Created by Sinatra Gunda
    At 9:12 AM on 8/15/2021

*/
package org.apache.fineract.portfolio.client.helper;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.EmailRecipients;
import org.apache.fineract.portfolio.client.domain.EmailRecipientsKey;
import org.apache.fineract.portfolio.client.repo.EmailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.EmailRecipientsRepository;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EmailRecipientsHelper {

    public static Long createMailRecipients(EmailRecipientsKeyRepository emailRecipientsKeyRepository , EmailRecipientsRepository emailRecipientsRepository , EmailRecipientsKey emailRecipientsKey){

        int count = emailRecipientsKey.getEmailRecipientsList().size();
        emailRecipientsKey.setCount(count);
        emailRecipientsKeyRepository.saveAndFlush(emailRecipientsKey);

        /// we need to do a whole lot of logics here
        List<EmailRecipients> emailRecipientsList = emailRecipientsKey.getEmailRecipientsList();

        for(EmailRecipients emailRecipients : emailRecipientsList){
            emailRecipients.setEmailRecipientsKey(emailRecipientsKey);
            emailRecipientsRepository.save(emailRecipients);
        }

        Long id = emailRecipientsKey.getId();
        return id ;

    }

    public static List<EmailRecipients> emailRecipients(EmailRecipientsKeyRepository emailRecipientsKeyRepository ,EmailRecipientsRepository emailRecipientsRepository , ClientReadPlatformService clientReadPlatformService, Long keyId){


        System.err.println("----------find all with key "+keyId);
        EmailRecipientsKey emailRecipientsKey = emailRecipientsKeyRepository.findOne(keyId);

        // if true then get office id and fill lists with recipients of clients
        boolean selectAllMode = emailRecipientsKey.getSelectAllMode();
        List<EmailRecipients> mailRecipientsList = emailRecipientsRepository.findByEmailRecipientsKeyId(keyId);

        System.err.println("-----------------selectAllMode is -------------"+selectAllMode);

        if(selectAllMode){

            Long officeId = emailRecipientsKey.getOfficeId();
            
            System.err.println("----------------office id is ========="+officeId);

            if(officeId.equals(0)){
                //we taking all clients
                System.err.println("-------------we sending to all clients that have email address");
                Page<ClientData> clientDataList =  clientReadPlatformService.retrieveAll(null);

                for(ClientData clientData : clientDataList.getPageItems()){
                    EmailRecipients emailRecipients = createMailRecipientObject(clientData);
                    if(emailRecipients==null){
                        continue;
                    }
                    mailRecipientsList.add(emailRecipients);
                }
            }
            else{

                System.err.println("------- else not select all----------------");
                String lookUpCriteria = String.format("office_id=%d",officeId);
                Collection<ClientData> clientDataList = clientReadPlatformService.retrieveAllForLookup(lookUpCriteria);

                for (ClientData clientData : clientDataList){

                    EmailRecipients emailRecipients = createMailRecipientObject(clientData);
                    if(emailRecipients==null){
                        continue;
                    }
                    mailRecipientsList.add(emailRecipients);
                }  
            }
        }
 
        System.err.println("------------------email recipients should be 4 here "+mailRecipientsList.size());
        return mailRecipientsList ;
    }

    public static EmailRecipients createMailRecipientObject(ClientData clientData){

        String name = clientData.displayName();
        String email = clientData.getEmailAddress();
        Long clientId = clientData.getId();

        if(email==null){
            return null;
        }

        return new EmailRecipients(name ,email ,true ,clientId);

    }
}
