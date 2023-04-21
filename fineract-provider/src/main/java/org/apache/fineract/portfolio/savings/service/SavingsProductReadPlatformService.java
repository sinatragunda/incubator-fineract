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

import java.util.Collection;

import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.utility.service.DataEnumerationService;

public interface SavingsProductReadPlatformService extends DataEnumerationService {

    Collection<SavingsProductData> retrieveAll();

    Collection<SavingsProductData> retrieveAllForLookup();

    Collection<SavingsProductData> retrieveAllForLookupByType(Boolean isOverdraftType);

    Collection<SavingsProductData> retrieveAllForCurrency(String currencyCode);

    SavingsProductData retrieveOne(Long productId);

    // Added 22/07/2022
    SavingsProductData retrieveOneByName(String productName);

}
