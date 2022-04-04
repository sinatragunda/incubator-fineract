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
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.accounts.constants.ShareAccountApiConstants;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.shareaccounts.repo.ShareAccountTransactionRepository;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountWritePlatformService;
import org.apache.fineract.portfolio.shareproducts.domain.ShareProduct;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// added 27/03/2022
import java.util.Date;


@Service
public class ShareAccountDomainServiceJpa implements ShareAccountDomainService {

    private ShareAccountWritePlatformService shareAccountWritePlatformService ;
    private final NoteRepository noteRepository;
    private final PlatformSecurityContext context;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final FromJsonHelper fromJsonHelper ;
    private final ShareAccountTransactionWrapper shareAccountTransactionWrapper ;
    

    // added 27/03/2022
    private final ShareAccountTransactionRepository shareAccountTransactionRepository ;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService ;

    // added 29/03/2022
    private final ShareAccountRepository shareAccountRepository ;

    @Autowired
    public ShareAccountDomainServiceJpa(final NoteRepository noteRepository, final PlatformSecurityContext context, final BusinessEventNotifierService businessEventNotifierService,final ShareAccountWritePlatformService shareAccountWritePlatformService ,final FromJsonHelper fromJsonHelper ,final ShareAccountTransactionWrapper shareAccountTransactionWrapper ,final ShareAccountTransactionRepository shareAccountTransactionRepository ,final JournalEntryWritePlatformService journalEntryWritePlatformService ,final ShareAccountRepository shareAccountRepository) {
        this.noteRepository = noteRepository;
        this.context = context;
        this.businessEventNotifierService = businessEventNotifierService;
        this.shareAccountWritePlatformService = shareAccountWritePlatformService ;
        this.fromJsonHelper = fromJsonHelper ;
        this.shareAccountTransactionWrapper = shareAccountTransactionWrapper ;
        this.shareAccountTransactionRepository = shareAccountTransactionRepository ;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService ;

        // added 29/03/2022
        this.shareAccountRepository = shareAccountRepository ;
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

        Map<String ,Object> jsonMap = new HashMap<>();
        ShareProduct shareProduct = shareAccount.getShareProduct();

        Integer requestedShares = shareAccountTransactionWrapper.calculateSharesPossibleForAmount(shareProduct ,repaymentAmount);

        jsonMap.put("dateFormat","dd MMMM yyyy");
        jsonMap.put("locale" ,"en");
        jsonMap.put("unitPrice",shareProduct.getUnitPrice());
        jsonMap.put("requestedShares" ,requestedShares);
        jsonMap.put("requestedDate",transactionDate.toString("dd MMMM yyyy"));

        String payload = JsonHelper.serializeMapToJson(jsonMap);

        JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

        Long shareAccountId = shareAccount.getId();

        CommandProcessingResult commandProcessingResult = this.shareAccountWritePlatformService.applyAddtionalShares(shareAccountId ,jsonCommand);

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.SHARES_PURCHASE, constructEntityMap(BUSINESS_ENTITY.SHARE_ACCOUNT, newShareAccountTransaction));

        // disable all active standing orders linked to this loan if status changes to closed
        //disableStandingInstructionsLinkedToClosedLoan(loan);

        Long transactionId[] = {null} ;

        /// with command id can we get the transaction id ?

        Map<String ,Object> changes = commandProcessingResult.getChanges();

        if(!changes.isEmpty()){
            
            Object val = changes.get(ShareAccountApiConstants.additionalshares_paramname);
            String strVal = String.valueOf(val);
            transactionId[0] = Long.parseLong(strVal);

        }


        builderResult.withEntityId(transactionId[0]) //
                .withOfficeId(commandProcessingResult.getOfficeId()) //
                .withClientId(commandProcessingResult.getClientId()) ;

        ShareAccountTransaction shareAccountTransaction[] = {null} ;

        Optional.ofNullable(transactionId).ifPresent(e->{

            shareAccountTransaction[0] = new ShareAccountTransaction(transactionId[0]);

            Optional.ofNullable(note).ifPresent(n->{
                note.setShareAccountTransaction(shareAccountTransaction[0]);
                this.noteRepository.save(note);
            });
        });

        return shareAccountTransaction[0] ;

    }

    public boolean reverseShareAccountTransaction(ReverseShareAccountTransaction reverseShareAccountTransaction){

        Long shareAccountTransactionId = reverseShareAccountTransaction.getId();
        Date transactionDate = reverseShareAccountTransaction.getTransactionDate();
        ShareAccountTransaction shareAccountTransaction = shareAccountTransactionWrapper.findShareAccountTransaction(shareAccountTransactionRepository ,shareAccountTransactionId);
        boolean isPresent = Optional.ofNullable(shareAccountTransaction).isPresent();
        boolean status = true;

        if(isPresent){
            try{
                List transactionIdsList = Arrays.asList(shareAccountTransactionId);
                ArrayList<Long> arrayList = new ArrayList(transactionIdsList); 
                journalEntryWritePlatformService.revertShareAccountJournalEntries(arrayList ,transactionDate);
                /// if done then delete share account 
                // so transaction fail here now ,instead of delete we should reverse ,set boolean to true ''

                shareAccountTransaction.setReverse(true);

                ///
                Long reversedShares = shareAccountTransaction.getTotalShares();

                ShareAccount shareAccount = shareAccountTransaction.getShareAccount();

                Long totalShares = shareAccount.getTotalApprovedShares();
                Long balanceShares = totalShares - reversedShares ;
                shareAccountTransactionRepository.save(shareAccountTransaction);
                shareAccount.setTotalSharesApproved(balanceShares);
                shareAccountRepository.save(shareAccount);

            }
            catch(Exception e){
                /// 
                e.printStackTrace();
                status = false ;
            }
        }
        return status ;
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
