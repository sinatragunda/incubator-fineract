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
package org.apache.fineract.accounting.producttoaccountmapping.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.savings.DepositAccountType;

public interface ProductToGLAccountMappingWritePlatformService {

    void createLoanProductToGLAccountMapping(Long loanProductId, JsonCommand command);

    void createSavingProductToGLAccountMapping(Long savingProductId, JsonCommand command, DepositAccountType accountType);

    Map<String, Object> updateLoanProductToGLAccountMapping(Long loanProductId, JsonCommand command, boolean accountingRuleChanged,
            int accountingRuleTypeId);

    Map<String, Object> updateSavingsProductToGLAccountMapping(Long savingsProductId, JsonCommand command, boolean accountingRuleChanged,
            int accountingRuleTypeId, DepositAccountType accountType);

    void createShareProductToGLAccountMapping(Long shareProductId, JsonCommand command);

    Map<String, Object> updateShareProductToGLAccountMapping(Long shareProductId, JsonCommand command, boolean accountingRuleChanged,
            int accountingRuleTypeId);

    // Added 19/09/2021
    void createDepreciationProductToGLAccountMapping(Long productId, JsonCommand command);


}