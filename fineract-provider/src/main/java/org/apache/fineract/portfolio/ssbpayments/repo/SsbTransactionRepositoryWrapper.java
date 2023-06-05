/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 May 2023 at 08:37
 */
package org.apache.fineract.portfolio.ssbpayments.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.ssbpayments.domain.SsbTransaction;
import org.apache.fineract.portfolio.ssbpayments.exception.SsbTransactionNotFound;
import org.taat.wese.weseaddons.ssb.enumerations.PORTFOLIO_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SsbTransactionRepositoryWrapper {
    private SsbTransactionRepository repository;

    @Autowired
    public SsbTransactionRepositoryWrapper(SsbTransactionRepository repository) {
        this.repository = repository;
    }

    public SsbTransaction findOneWithNotFoundDetection(Long objectId , Long transactionId , PORTFOLIO_TYPE portfolioType){

        SsbTransaction ssbTransaction = repository.findByObjectIdAndTransactionIdAndPortfolioType(portfolioType, objectId, transactionId);
        boolean has = OptionalHelper.has(ssbTransaction);
        if(!has){
            throw new SsbTransactionNotFound(transactionId);
        }
        return ssbTransaction;
    }

    public SsbTransaction save(SsbTransaction ssbTransaction){
        repository.saveAndFlush(ssbTransaction);
        return ssbTransaction;
    }

}
