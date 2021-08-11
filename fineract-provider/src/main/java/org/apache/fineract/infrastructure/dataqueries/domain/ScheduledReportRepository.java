/*

    Created by Sinatra Gunda
    At 1:23 AM on 8/6/2021

*/
package org.apache.fineract.infrastructure.dataqueries.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ScheduledReportRepository extends JpaRepository<ScheduledReport, Long>, JpaSpecificationExecutor<ScheduledReport>{

    //public ScheduledReport findById(Long id);
    ScheduledReport findOneByJobId(Long id);

}
