/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 02:02
 */
package org.apache.fineract.portfolio.localref.repo;

import org.apache.fineract.portfolio.localref.domain.LocalRefValue;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Service
public class LocalRefValueRepositoryWrapper {

    private LocalRefValueRepository localRefValueRepository;

    @Autowired
    public LocalRefValueRepositoryWrapper(LocalRefValueRepository localRefValueRepository) {
        this.localRefValueRepository = localRefValueRepository;
    }

    public void save(LocalRefValue localRefValue){
        this.localRefValueRepository.save(localRefValue);
    }

    public Collection<LocalRefValue> findByRecordId(Long recordId){
        return this.localRefValueRepository.findByRecordId(recordId); 
    }


}
