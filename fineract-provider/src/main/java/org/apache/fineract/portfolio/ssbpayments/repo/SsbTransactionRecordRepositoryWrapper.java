/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 May 2023 at 10:43
 */
package org.apache.fineract.portfolio.ssbpayments.repo;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.ssbpayments.domain.SsbTransactionRecord;
import org.apache.fineract.portfolio.ssbpayments.exception.SsbTransactionNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SsbTransactionRecordRepositoryWrapper {

    private SSbTransactionRecordRepository repository;

    @Autowired
    public SsbTransactionRecordRepositoryWrapper(SSbTransactionRecordRepository repository) {
        this.repository = repository;
    }

    public SsbTransactionRecord findOneWithNotFoundDetection(Long id){
        SsbTransactionRecord ssbTransactionRecord = repository.findOne(id);
        boolean has = OptionalHelper.has(ssbTransactionRecord);
        System.err.println("-----------has record ? "+has);
        if(!has){
            throw new SsbTransactionNotFound(id);
        }
        return ssbTransactionRecord;
    }

    public SsbTransactionRecord findOneByFilename(String filename){

        SsbTransactionRecord ssbTransactionRecord = repository.findOneByFilename(filename);
        return ssbTransactionRecord;
    }



    public SsbTransactionRecord save(SsbTransactionRecord ssbTransactionRecord){
        repository.saveAndFlush(ssbTransactionRecord);
        return ssbTransactionRecord;
    }
}
