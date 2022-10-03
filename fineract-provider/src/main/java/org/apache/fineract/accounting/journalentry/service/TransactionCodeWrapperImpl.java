/*

    Created by Sinatra Gunda
    At 12:20 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.accounting.journalentry.exception.TransactionCodeDuplicateException;
import org.apache.fineract.accounting.journalentry.exception.TransactionCodeNotFoundException;
import org.apache.fineract.accounting.journalentry.repo.TransactionCodeRepository;

import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionCodeWrapperImpl implements TransactionCodeWrapper {

    private TransactionCodeRepository transactionCodeRepository;

    @Autowired
    public TransactionCodeWrapperImpl(TransactionCodeRepository transactionCodeRepository) {
        this.transactionCodeRepository = transactionCodeRepository;
    }

    @Override
    public TransactionCode findOneWithNotFoundException(Long id) {

        TransactionCode transactionCode = transactionCodeRepository.findOne(id);

        Supplier thrownException = ()-> new TransactionCodeNotFoundException(id);

        return Optional.ofNullable(transactionCode).orElseThrow(thrownException);
    }

    @Override
    public Long save(TransactionCode transactionCode){

        Long id = null ;
        try{
            transactionCodeRepository.save(transactionCode);
            id = transactionCode.getId();
        }
        catch (RuntimeException r){
            Long code = transactionCode.getCode();
            throw new TransactionCodeDuplicateException(code);
        }
        return id ;
    }
}
