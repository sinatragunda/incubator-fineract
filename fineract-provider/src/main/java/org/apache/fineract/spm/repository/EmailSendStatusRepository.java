/*

    Created by Sinatra Gunda
    At 5:05 PM on 9/6/2021

*/
package org.apache.fineract.spm.repository;

import org.apache.fineract.wese.portfolio.scheduledreports.domain.EmailSendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EmailSendStatusRepository extends JpaRepository<EmailSendStatus,Long> ,JpaSpecificationExecutor<EmailSendStatus>{

    List<EmailSendStatus> findByScheduledMailSessionId(Long scheduledMailSession);
}
