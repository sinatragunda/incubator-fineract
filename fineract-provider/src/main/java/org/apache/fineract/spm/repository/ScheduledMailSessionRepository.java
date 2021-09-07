/*

    Created by Sinatra Gunda
    At 5:00 PM on 9/6/2021

*/
package org.apache.fineract.spm.repository;

import org.apache.fineract.wese.portfolio.scheduledreports.domain.ScheduledMailSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ScheduledMailSessionRepository extends JpaRepository<ScheduledMailSession,Long> ,JpaSpecificationExecutor<ScheduledMailSession>{

    List<ScheduledMailSession> findByScheduledReportId(Long scheduledReportId);
}
