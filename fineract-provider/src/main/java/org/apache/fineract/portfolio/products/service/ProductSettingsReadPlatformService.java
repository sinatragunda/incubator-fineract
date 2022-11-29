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

import org.apache.fineract.portfolio.products.data.ProductData;
import org.apache.fineract.portfolio.products.data.PropertyTypeData;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;

public interface ProductSettingsReadPlatformService {

    /**
     *  Modified 29/11/2022 at 0118
     *  Ideally all productdata is found by product type and the corresponding product id
     */
    public Collection<ProductData> retrieveAllByProductType(final PRODUCT_TYPE productType);

    public ProductData retrieveOneByProductType(final Long productId, final PRODUCT_TYPE productType);

    public ProductData template();

    /**
     * Added 29/11/2022 at 0119
     * ProductId here means the id from the class Product instead of implementing classes ie ShareProduct ,LoanProduct
     */
    public Collection<PropertyTypeData> retrieveAllPropertyTypesForLookupByProductId(Long productId) ;

}
