/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 February 2023 at 12:32
 */
package org.apache.fineract.infrastructure.dataqueries.service;


import org.apache.fineract.infrastructure.dataqueries.domain.ApplicationRecord;
import org.apache.fineract.infrastructure.dataqueries.repo.ApplicationRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationRecordRepositoryWrapper {
    private ApplicationRecordRepository applicationRecordRepository;

    @Autowired
    public ApplicationRecordRepositoryWrapper(ApplicationRecordRepository applicationRecordRepository) {
        this.applicationRecordRepository = applicationRecordRepository;
    }

    public Long save(ApplicationRecord applicationRecord){

        applicationRecord = this.applicationRecordRepository.saveAndFlush(applicationRecord);

        System.err.println("-----------------record id is "+applicationRecord.getId());
        return applicationRecord.getId() ;
    }
}
