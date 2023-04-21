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
package org.apache.fineract.portfolio.products.service;

import java.util.Collection;
import java.util.Set;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.portfolio.products.domain.IProduct;
import org.apache.fineract.portfolio.shareproducts.data.ShareProductData;

public interface ProductReadPlatformService {

    public Page<IProduct> retrieveAllProducts(Integer offSet, Integer limit);

    public IProduct retrieveOne(final Long productId, boolean includeTemplate);

    public IProduct retrieveTemplate();

    public Set<String> getResponseDataParams();
    
    public Collection<IProduct> retrieveAllForLookup() ;

    public Collection<ShareProductData> retrieveAllShareProducts() ;

}
