/*

    Created by Sinatra Gunda
    At 12:33 AM on 8/11/2021

*/
package org.apache.fineract.infrastructure.dataqueries.service;

/*

 Wrapper class for {@link ScheduledReportRepository}

 */

import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReportRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ScheduledReportRepositoryWrapper {

    private final ScheduledReportRepository scheduledReportRepository ;


    @Autowired
    public ScheduledReportRepositoryWrapper(ScheduledReportRepository scheduledReportRepository){
        this.scheduledReportRepository = scheduledReportRepository ;
    }

    public ScheduledReport findOneByJobId(Long jobId){
        ScheduledReport scheduledReport = scheduledReportRepository.findOneByJobId(jobId);
        return scheduledReport ;
    }

    public void saveOrUpdate(ScheduledReport scheduledReport){
        this.scheduledReportRepository.save(scheduledReport);
    }



}
