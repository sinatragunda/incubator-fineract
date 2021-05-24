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

/// Added 24/05/2021 by Sinatra Gunda


package org.apache.fineract.portfolio.loanproduct.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * {@link AbstractPlatformDomainRuleException} thrown when lending strategy
 * mismatch occurs
 */
public class AllowMultipleLoanInstancesException extends AbstractPlatformDomainRuleException {

	public String message = "Multiple loan instances for this product are not allowed ,user already has an existing loan of this product";

    public AllowMultipleLoanInstancesException(final String loanProductName) {
        super(String.format("Multiple loan instances for this product (%s) are not allowed ,user already has an existing loan of this product" ,loanProductName), String.format("Multiple loan instances for this product (%s) are not allowed ,user already has an existing loan of this product" ,loanProductName));
    }
}