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
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountWritePlatformService;
import org.apache.fineract.portfolio.shareproducts.domain.ShareProduct;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShareAccountDomainServiceJpa implements ShareAccountDomainService {

    private ShareAccountWritePlatformService shareAccountWritePlatformService ;
    private final NoteRepository noteRepository;
    private final PlatformSecurityContext context;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final FromJsonHelper fromJsonHelper ;
    private final ShareAccountTransactionWrapper shareAccountTransactionWrapper ;
    //private final Sh

    @Autowired
    public ShareAccountDomainServiceJpa(final NoteRepository noteRepository, final PlatformSecurityContext context, final BusinessEventNotifierService businessEventNotifierService,final ShareAccountWritePlatformService shareAccountWritePlatformService ,final FromJsonHelper fromJsonHelper ,final ShareAccountTransactionWrapper shareAccountTransactionWrapper) {
        this.noteRepository = noteRepository;
        this.context = context;
        this.businessEventNotifierService = businessEventNotifierService;
        this.shareAccountWritePlatformService = shareAccountWritePlatformService ;
        this.fromJsonHelper = fromJsonHelper ;
        this.shareAccountTransactionWrapper = shareAccountTransactionWrapper ;
    }

    @Transactional
    @Override
    public ShareAccountTransaction purchaseShares(final ShareAccount shareAccount, final CommandProcessingResultBuilder builderResult,
                                         final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final Note note,
                                         final String txnExternalId,boolean isAccountTransfer) {

        AppUser currentUser = getAppUserIfPresent();
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.SHARES_PURCHASE,
                constructEntityMap(BUSINESS_ENTITY.SHARE_ACCOUNT, shareAccount));


        final Money repaymentAmount = Money.of(shareAccount.getCurrency(), transactionAmount);
        ShareAccountTransaction newShareAccountTransaction = null;
        final LocalDateTime currentDateTime = DateUtils.getLocalDateTimeOfTenant();

        /***
         * TODO Vishwas Batch save is giving me a
         * HibernateOptimisticLockingFailureException, looping and saving for
         * the time being, not a major issue for now as this loop is entered
         * only in edge cases (when a payment is made before the latest payment
         * recorded against the loan)
         ***/

        Map<String ,Object> jsonMap = new HashMap<>();
        ShareProduct shareProduct = shareAccount.getShareProduct();

        Integer requestedShares = shareAccountTransactionWrapper.calculateSharesPossibleForAmount(shareProduct ,repaymentAmount);

        jsonMap.put("dateFormat","dd MMMM yyyy");
        jsonMap.put("locale" ,"en");
        jsonMap.put("unitPrice",shareProduct.getUnitPrice());
        jsonMap.put("requestedShares" ,requestedShares);
        jsonMap.put("requestedDate",transactionDate.toString("dd MMMM yyyy"));

        String payload = JsonHelper.serializeMapToJson(jsonMap);


        System.err.println("-------------------------------payload is -------------------"+payload);

        JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

        Long shareAccountId = shareAccount.getId();

        CommandProcessingResult commandProcessingResult = this.shareAccountWritePlatformService.applyAddtionalShares(shareAccountId ,jsonCommand);


//        if (StringUtils.isNotBlank(noteText)) {
//            //final Note note = Note.shareNote(shareAccount, noteText);
//
//            this.noteRepository.save(note);
//
//        }

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.SHARES_PURCHASE, constructEntityMap(BUSINESS_ENTITY.SHARE_ACCOUNT, newShareAccountTransaction));

        // disable all active standing orders linked to this loan if status changes to closed
        //disableStandingInstructionsLinkedToClosedLoan(loan);

        builderResult.withEntityId(commandProcessingResult.commandId()) //
                .withOfficeId(commandProcessingResult.getOfficeId()) //
                .withClientId(commandProcessingResult.getClientId()) ;

        Long id = commandProcessingResult.commandId();
        Long res = commandProcessingResult.resourceId();

        /// with command id can we get the transaction id ?

        System.err.println("---------------------------what is share account transaction ------------------"+id+"----------------and resource id ---------------"+res);

        ShareAccountTransaction shareAccountTransaction[] = {null} ;

        Optional.ofNullable(res).ifPresent(e->{

            shareAccountTransaction[0] = new ShareAccountTransaction(res);

            Optional.ofNullable(note).ifPresent(n->{
                note.setShareAccountTransaction(shareAccountTransaction[0]);
                this.noteRepository.save(note);
            });
        });


        return shareAccountTransaction[0] ;

    }


    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }

}
