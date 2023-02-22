/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 01:55
 */
package org.apache.fineract.portfolio.localref.repo;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.exception.LocalRefNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;


@Service
public class LocalRefRepositoryWrapper {

    private LocalRefRepository localRefRepository;

    @Autowired
    public LocalRefRepositoryWrapper(LocalRefRepository localRefRepository) {
        this.localRefRepository = localRefRepository;
    }

    public LocalRef findOneWithoutNotFoundDetection(final Long id){
        LocalRef localRef = localRefRepository.findOne(id);
        Supplier error = ()-> new LocalRefNotFoundException(id);
        Optional.ofNullable(localRef).orElseThrow(error);
        return localRef;
    }

    public Collection<LocalRef> findByRefTable(REF_TABLE refTable){
        return localRefRepository.findByRefTable(refTable);
    }
}
