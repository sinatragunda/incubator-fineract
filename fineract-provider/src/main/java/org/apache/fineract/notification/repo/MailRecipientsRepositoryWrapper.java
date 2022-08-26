/*

    Created by Sinatra Gunda
    At 1:08 PM on 8/25/2022

*/
package org.apache.fineract.notification.repo;

import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;
import org.apache.fineract.portfolio.client.repo.MailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.MailRecipientsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MailRecipientsRepositoryWrapper {

    private final MailRecipientsKeyRepository mailRecipientsKeyRepository;
    private final MailRecipientsRepository mailRecipientsRepository;

    @Autowired
    public MailRecipientsRepositoryWrapper(MailRecipientsKeyRepository mailRecipientsKeyRepository, MailRecipientsRepository mailRecipientsRepository) {
        this.mailRecipientsKeyRepository = mailRecipientsKeyRepository;
        this.mailRecipientsRepository = mailRecipientsRepository;
    }

    public MailRecipientsKey findOneMailRecipientsKeyWithNotDetected(Long id){

        MailRecipientsKey mailRecipientsKey = null;

        try{
            mailRecipientsKey = mailRecipientsKeyRepository.findOne(id);
        }
        catch (Exception n){
            // change for a specific exception
        }

        return mailRecipientsKey;

    }
}
