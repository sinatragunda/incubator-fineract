/*

    Created by Sinatra Gunda
    At 11:43 AM on 9/19/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.service;

import org.apache.fineract.spm.repository.DepreciationProductRepository;
import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciationProduct;
import org.apache.fineract.wese.portfolio.depreciation.exceptions.DepreciationProductNotFoundException;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


public class DepreciationProductReadPlatformServiceImpl implements DepreciationProductReadPlatformService{

    private DepreciationProductRepository depreciationProductRepository;

    @Autowired
    DepreciationProductReadPlatformServiceImpl(DepreciationProductRepository depreciationProductRepository){
        this.depreciationProductRepository = depreciationProductRepository;
    }

    @Override
    public Collection<DepreciationProduct> retrieveAll() {
        return null;
    }

    @Override
    public DepreciationProduct retrieveOne(Long id) {

        DepreciationProduct depreciationProduct = depreciationProductRepository.findOne(id);
        if(depreciationProduct==null){
            throw new DepreciationProductNotFoundException(id);
        }
        return depreciationProduct;
    }
}
