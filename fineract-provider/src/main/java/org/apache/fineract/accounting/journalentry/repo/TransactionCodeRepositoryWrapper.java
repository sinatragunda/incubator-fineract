/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 December 2022 at 15:05
 */
package org.apache.fineract.accounting.journalentry.repo;

import org.apache.fineract.accounting.journalentry.domain.TransactionCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionCodeRepositoryWrapper {

    private TransactionCodeRepository transactionCodeRepository;

    @Autowired
    public TransactionCodeRepositoryWrapper(TransactionCodeRepository transactionCodeRepository) {
        this.transactionCodeRepository = transactionCodeRepository;
    }

    public TransactionCode findOneWithNotFoundDetection(Long id){
        TransactionCode transactionCode = null ;
        try{
            transactionCode = transactionCodeRepository.findOne(id);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return transactionCode;
    }

    public Long save(TransactionCode transactionCode){
        this.transactionCodeRepository.save(transactionCode);
        Long id = transactionCode.getId();
        return id ;
    }
}
