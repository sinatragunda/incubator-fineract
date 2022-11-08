/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 November 2022 at 03:13
 */
package org.apache.fineract.portfolio.remittance.repo;

import org.apache.fineract.portfolio.remittance.domain.RxDeal;
import org.apache.fineract.accounting.rule.exception.AccountingRuleNotFoundException;
import org.apache.fineract.portfolio.remittance.exceptions.RxDealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RxDealRepositoryWrapper {

    private RxDealRepository rxDealRepository;

    @Autowired
    public RxDealRepositoryWrapper(RxDealRepository rxDealRepository) {
        this.rxDealRepository = rxDealRepository;
    }

    public RxDeal findOneWithNotFoundDetection(String key){

        RxDeal rxDeal = rxDealRepository.findOneByKey(key);
        boolean isDealFound = Optional.ofNullable(rxDeal).isPresent();

        if(!isDealFound){
            throw new RxDealNotFoundException(key);
        }
        return rxDeal;
    }


    public RxDeal findOneWithNotFoundDetection(Long id){

        RxDeal rxDeal = rxDealRepository.findOne(id);
        boolean isDealFound = Optional.ofNullable(rxDeal).isPresent();

        if(!isDealFound){
            throw new RxDealNotFoundException(id);
        }
        return rxDeal;
    }
}
