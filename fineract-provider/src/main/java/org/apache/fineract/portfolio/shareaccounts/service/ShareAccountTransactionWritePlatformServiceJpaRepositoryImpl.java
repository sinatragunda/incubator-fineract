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

// created 27/03/2022

package org.apache.fineract.portfolio.shareaccounts.service;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.shareaccounts.domain.*;
import org.apache.fineract.portfolio.shareaccounts.serialization.ShareAccountDataSerializer;
import org.apache.fineract.portfolio.shareproducts.domain.ShareProductRepositoryWrapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ShareAccountTransactionWritePlatformServiceJpaRepositoryImpl implements ShareAccountTransactionWritePlatformService {

    private final ShareAccountDataSerializer accountDataSerializer;
    private final ShareAccountRepositoryWrapper shareAccountRepository;
    private final ShareProductRepositoryWrapper shareProductRepository ;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final BusinessEventNotifierService businessEventNotifierService;

    private final ShareAccountDomainService shareAccountDomainService;
    
    @Autowired
    public ShareAccountTransactionWritePlatformServiceJpaRepositoryImpl(final ShareAccountDataSerializer accountDataSerializer,
            final ShareAccountRepositoryWrapper shareAccountRepository, 
            final ShareProductRepositoryWrapper shareProductRepository,
            final AccountNumberGenerator accountNumberGenerator,
            final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final NoteRepository noteRepository,
            final BusinessEventNotifierService businessEventNotifierService,
            final ShareAccountDomainService shareAccountDomainService) {
        this.accountDataSerializer = accountDataSerializer;
        this.shareAccountRepository = shareAccountRepository;
        this.shareProductRepository = shareProductRepository ;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.businessEventNotifierService = businessEventNotifierService;
        this.shareAccountDomainService = shareAccountDomainService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CommandProcessingResult updateShareAccountTransaction(Long transactionId,String command ,JsonCommand jsonCommand) {

        System.err.println("--------------------------------reverse some transactions son-------------");
        try {
            ReverseShareAccountTransaction transaction = accountDataSerializer.validateForTransactionReversal(jsonCommand ,transactionId);
            transaction.setId(transactionId);
            boolean status = shareAccountDomainService.reverseShareAccountTransaction(transaction);
            return CommandProcessingResult.commandOnlyResult(1L);

        } catch (DataIntegrityViolationException dve) {
            System.err.println("-----------------integrity error ? ");
            handleDataIntegrityIssues(jsonCommand, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }catch (final PersistenceException dve) {
            System.err.println("-------------------another integrity error -------------");
        	Throwable throwable = ExceptionUtils.getRootCause(dve.getCause()) ;
        	handleDataIntegrityIssues(jsonCommand, throwable, dve);
        	return CommandProcessingResult.empty();
        }
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {
        throw new PlatformDataIntegrityException("error.msg.shareaccount.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
}
