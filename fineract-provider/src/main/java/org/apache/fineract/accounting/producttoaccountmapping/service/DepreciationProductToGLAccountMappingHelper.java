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

//Created from copy pasting on 19/09/2021 

package org.apache.fineract.accounting.producttoaccountmapping.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.accounting.common.AccountingConstants;
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.accounting.common.AccountingConstants.ACCRUAL_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS;
import org.apache.fineract.accounting.common.AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_DATA_PARAMS;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepository;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.accounting.producttoaccountmapping.domain.ProductToGLAccountMappingRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class DepreciationProductToGLAccountMappingHelper extends ProductToGLAccountMappingHelper {

    @Autowired
    public DepreciationProductToGLAccountMappingHelper(final GLAccountRepository glAccountRepository,
            final ProductToGLAccountMappingRepository glAccountMappingRepository, final FromJsonHelper fromApiJsonHelper,
            final ChargeRepositoryWrapper chargeRepositoryWrapper, final GLAccountRepositoryWrapper accountRepositoryWrapper,
            final PaymentTypeRepositoryWrapper paymentTypeRepositoryWrapper) {
        super(glAccountRepository, glAccountMappingRepository, fromApiJsonHelper, chargeRepositoryWrapper, accountRepositoryWrapper,
                paymentTypeRepositoryWrapper);
    }

    /*** Abstractions for payments channel related to loan products ***/


    public Map<String, Object> populateChangesForDepreciationProductToGLAccountMappingCreation(final JsonElement element,
            final AccountingRuleType accountingRuleType) {
        final Map<String, Object> changes = new HashMap<>();

        final Long accumulatedDepreciationAccountId = this.fromApiJsonHelper.extractLongNamed(AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_DATA_PARAMS.ACCUMULATED_DEPRECIATION.getValue(), element);

        final Long assetCostAccountId = this.fromApiJsonHelper.extractLongNamed(
                AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ASSET_COST.getValue(), element);

        final Long depreciationChargeAccountId = this.fromApiJsonHelper.extractLongNamed(
                AccountingConstants.DEPRECIATION_PRODUCT_ACCOUNTING_DATA_PARAMS.DEPRECIATION_CHARGE.getValue(), element);


        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                populateChangesForCashBasedAccounting(changes ,accumulatedDepreciationAccountId, assetCostAccountId, depreciationChargeAccountId);
            break;
        }

        return changes;
    }


    private void populateChangesForCashBasedAccounting(final Map<String, Object> changes, final Long accumulatedDepreciationAccountId, final Long depreciationChargeAccountId, final Long assetCostAccountId) {

        changes.put(DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ASSET_COST.getValue(), assetCostAccountId);
        changes.put(DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ACCUMULATED_DEPRECIATION.getValue(), accumulatedDepreciationAccountId);
        changes.put(DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.DEPRECIATION_CHARGE.getValue(), depreciationChargeAccountId);

    }

    /**
     * Examines and updates each account mapping for given loan product with
     * changes passed in from the Json element
     * 
     * @param loanProductId
     * @param changes
     * @param element
     * @param accountingRuleType
     */
    public void handleChangesToLoanProductToGLAccountMappings(final Long loanProductId, final Map<String, Object> changes,
            final JsonElement element, final AccountingRuleType accountingRuleType) {
        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:

                // asset
               mergeToAssetAccountMappingChanges(element, DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ASSET_COST.getValue(), loanProductId,
                       CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.toString(), changes);
               mergeLoanToAssetAccountMappingChanges(element, DEPRECIATION_PRODUCT_ACCOUNTING_PARAMS.ACCUMULATED_DEPRECIATION.getValue(), loanProductId,
                       CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.toString(), changes);

               // expenses
               mergeLoanToExpenseAccountMappingChanges(element,DEPRECIATION_PRODUCT_ACCOUNTING_DATA_PARAMS.DEPRECIATION_CHARGE.getValue(),
                       loanProductId, CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(),
                       CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.toString(), changes);
                break;
        }
    }

    public void deleteProductToGLAccountMapping(final Long productId) {
        deleteProductToGLAccountMapping(productId, PortfolioProductType.DEPRECIATION);
    }

}
