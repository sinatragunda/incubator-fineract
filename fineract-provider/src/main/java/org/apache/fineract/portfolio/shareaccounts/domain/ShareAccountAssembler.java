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
import java.util.Arrays;
import java.util.Set;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.exception.UnsupportedParameterException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.account.service.AccountTransfersReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepositoryWrapper;
import org.apache.fineract.portfolio.group.exception.CenterNotActiveException;
import org.apache.fineract.portfolio.group.exception.ClientNotInGroupException;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.savings.SavingsCompoundingInterestPeriodType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationDaysInYearType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationType;
import org.apache.fineract.portfolio.savings.SavingsPeriodFrequencyType;
import org.apache.fineract.portfolio.savings.SavingsPostingInterestPeriodType;
import org.apache.fineract.portfolio.savings.exception.SavingsProductNotFoundException;
import org.apache.fineract.portfolio.shareproducts.domain.ShareProductRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class ShareAccountAssembler {

    private final ShareAccountTransactionWrapper shareAccountTransactionWrapper;
    private final ClientRepositoryWrapper clientRepository;
    private final StaffRepositoryWrapper staffRepository;
    private final ShareProductRepository shareProductRepository;
    private final ShareAccountRepositoryWrapper shareAccountRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ShareAccountAssembler(final ShareAccountTransactionWrapper shareAccountTransactionWrapper,
            final ClientRepositoryWrapper clientRepository,
            final StaffRepositoryWrapper staffRepository, final ShareProductRepository shareProductRepository,
            final ShareAccountRepositoryWrapper shareAccountRepository,
            final FromJsonHelper fromApiJsonHelper,
            final AccountTransfersReadPlatformService accountTransfersReadPlatformService) {
        this.shareAccountTransactionWrapper = shareAccountTransactionWrapper;
        this.clientRepository = clientRepository;
        this.staffRepository = staffRepository;
        this.shareProductRepository = shareProductRepository;
        this.shareAccountRepository = shareAccountRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    /**
     * Assembles a new {@link SavingsAccount} from JSON details passed in
     * request inheriting details where relevant from chosen
     * {@link SavingsProduct}.
     */
    public ShareAccount assembleFrom(final Long shareId) {
        final ShareAccount account = this.shareAccountRepository.findOneWithNotFoundDetection(shareId);
        account.setHelpers(this.shareAccountTransactionWrapper);
        return account;
    }

}