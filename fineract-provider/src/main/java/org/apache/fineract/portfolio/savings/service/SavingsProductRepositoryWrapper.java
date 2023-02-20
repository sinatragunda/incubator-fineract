/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.portfolio.savings.domain.SavingsProduct;
import org.apache.fineract.portfolio.savings.domain.SavingsProductRepository;
import org.apache.fineract.portfolio.savings.exception.SavingsProductNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SavingsProductRepositoryWrapper {

    private final SavingsProductRepository savingsProductRepository;

    @Autowired
    public SavingsProductRepositoryWrapper(SavingsProductRepository savingsProductRepository) {
        this.savingsProductRepository = savingsProductRepository;
    }

    public SavingsProduct findOneWithNotFoundDetection(Long id){
        
        SavingsProduct product = savingsProductRepository.findOne(id);
        boolean isPresent = Optional.ofNullable(product).isPresent();

        if(!isPresent){
            throw new SavingsProductNotFoundException(id);
        }
        return product;
    }
    
}