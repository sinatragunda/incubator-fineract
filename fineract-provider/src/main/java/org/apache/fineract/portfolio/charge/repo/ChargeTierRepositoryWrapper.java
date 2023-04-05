/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 April 2023 at 01:57
 */
package org.apache.fineract.portfolio.charge.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeTier;
import org.apache.fineract.portfolio.charge.exception.ChargeIsNotActiveException;
import org.apache.fineract.portfolio.charge.exception.ChargeNotFoundException;
import org.apache.fineract.portfolio.charge.exception.ChargeTierNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChargeTierRepositoryWrapper {

    private final ChargeTierRepository repository;

    @Autowired
    public ChargeTierRepositoryWrapper(final ChargeTierRepository repository) {
        this.repository = repository;
    }

    public ChargeTier findOneWithNotFoundDetection(final Long id) {

        final ChargeTier chargeDefinition = this.repository.findOne(id);
        final boolean has = OptionalHelper.isPresent(chargeDefinition);
        if(!has){
            throw  new ChargeTierNotFoundException(id);
        }
        return chargeDefinition;
    }

    public ChargeTier save(ChargeTier chargeTier){
        this.repository.saveAndFlush(chargeTier);
        return chargeTier;
    }
}
