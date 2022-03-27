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
package org.apache.fineract.portfolio.shareaccounts.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.shareproducts.domain.ShareProduct;
import org.springframework.stereotype.Component;

/**
 * A wrapper for dealing with side-effect free functionality related to a
 * {@link ShareAccount}'s {@link ShareAccountTransaction}'s.
 */
@Component
public final class ShareAccountTransactionWrapper {


    private ShareAccount shareAccount ;

    public Integer calculateSharesPossibleForAmount(final ShareProduct shareProduct , final Money total) {
        
        Integer shares[] = {0};
        
        Optional.ofNullable(shareProduct).ifPresent(e->{
            BigDecimal amount = total.getAmount();
            BigDecimal unitPrice = shareProduct.getUnitPrice();

            BigDecimal sharePurchasable = amount.divide(unitPrice);
            shares[0] = sharePurchasable.intValue();
        
        });
        return shares[0];
    }

    public void setShareAccount(ShareAccount shareaccount){
        this.shareAccount = shareAccount;
    }


}