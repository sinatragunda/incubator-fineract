/*

    Created by Sinatra Gunda
    At 8:53 AM on 8/15/2021

*/
package org.apache.fineract.portfolio.client.repo;


import org.apache.fineract.portfolio.client.domain.MailRecipients;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface MailRecipientsRepository extends JpaRepository<MailRecipients,Long> ,JpaSpecificationExecutor<MailRecipients>{

    List<MailRecipients> findByMailRecipientsKeyId(Long id);


}
