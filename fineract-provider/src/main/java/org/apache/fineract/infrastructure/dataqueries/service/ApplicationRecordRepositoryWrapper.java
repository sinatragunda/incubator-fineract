/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 February 2023 at 12:32
 */
package org.apache.fineract.infrastructure.dataqueries.service;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.dataqueries.domain.ApplicationRecord;
import org.apache.fineract.infrastructure.dataqueries.exception.ApplicationRecordNotFoundException;
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
        return applicationRecord.getId() ;
    }

    public ApplicationRecord findOneWithNotFoundDetection(Long id){

        ApplicationRecord applicationRecord = applicationRecordRepository.findOne(id);
        boolean isPresent = OptionalHelper.isPresent(applicationRecord);
        if(!isPresent){
            throw new ApplicationRecordNotFoundException(id);
        }
        return applicationRecord;
    }
}
