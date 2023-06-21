/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 March 2023 at 08:28
 */
package org.apache.fineract.infrastructure.dataqueries.service;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.dataqueries.domain.ApplicationRecord;
import org.apache.fineract.infrastructure.dataqueries.domain.HybridTableEntity;
import org.apache.fineract.infrastructure.dataqueries.exception.ApplicationRecordNotFoundException;
import org.apache.fineract.infrastructure.dataqueries.repo.ApplicationRecordRepository;
import org.apache.fineract.infrastructure.dataqueries.repo.HybridTableEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List ;
@Service
public class HybridTableEntityRepositoryWrapper {

    private HybridTableEntityRepository hybridTableEntityRepository;

    @Autowired
    public HybridTableEntityRepositoryWrapper(HybridTableEntityRepository hybridTableEntityRepository) {
        this.hybridTableEntityRepository = hybridTableEntityRepository;
    }

    public Long save(HybridTableEntity hybridTableEntity){
        hybridTableEntityRepository.saveAndFlush(hybridTableEntity);
        return hybridTableEntity.getId();
    }

    public HybridTableEntity findOneWithNotFoundDetection(Long id){

        HybridTableEntity hybridTableEntity = hybridTableEntityRepository.findOne(id);
        boolean isPresent = OptionalHelper.isPresent(hybridTableEntity);
        if(!isPresent){
            throw new ApplicationRecordNotFoundException(id);
        }
        return hybridTableEntity;
    }

    public List<HybridTableEntity> findByRefId(Long id){
        List<HybridTableEntity> entities = hybridTableEntityRepository.findByAbstractPersistableCustomId(id);
        return entities;
    }
}
