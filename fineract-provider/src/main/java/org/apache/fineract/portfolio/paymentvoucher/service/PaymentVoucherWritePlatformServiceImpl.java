/*

    Created by Sinatra Gunda
    At 11:06 AM on 8/10/2022

*/
package org.apache.fineract.portfolio.paymentvoucher.service;

import org.apache.fineract.accounting.enumerations.ENTITY_TYPE;
import org.apache.fineract.accounting.journalentry.data.JournalEntryData;
import org.apache.fineract.accounting.journalentry.service.JournalEntryReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.portfolio.paymentvoucher.domain.PaymentVoucher;
import org.apache.fineract.portfolio.paymentvoucher.repo.PaymentVoucherRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


import org.apache.fineract.wese.helper.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray ;




@Service
public class PaymentVoucherWritePlatformServiceImpl implements PaymentVoucherWritePlatformService {


    private FromJsonHelper fromJsonHelper ;
    private PaymentVoucherRepository paymentVoucherRepository ;
    private JournalEntryReadPlatformService journalEntryReadPlatformService;

    @Autowired
    public PaymentVoucherWritePlatformServiceImpl(FromJsonHelper fromJsonHelper, PaymentVoucherRepository paymentVoucherRepository, JournalEntryReadPlatformService journalEntryReadPlatformService) {
        this.fromJsonHelper = fromJsonHelper;
        this.paymentVoucherRepository = paymentVoucherRepository;
        this.journalEntryReadPlatformService = journalEntryReadPlatformService;
    }

    @Override
    public List<PaymentVoucher> findJournalEntries(JsonCommand jsonCommand){

        System.err.println("==========================json is ================"+jsonCommand.json());

        JsonElement element = jsonCommand.parsedJson();


        String name = fromJsonHelper.extractStringNamed("name",element);

        System.err.println("--------------------name of object is --------------"+name);

        JsonArray jsonArray = fromJsonHelper.extractJsonArrayNamed("paymentvouchers" ,element);

        List<PaymentVoucher> paymentVoucherList = new ArrayList<>();


        for(JsonElement jsonElement : jsonArray){

            System.err.println(jsonElement);

            String transactionId = fromJsonHelper.extractStringNamed("transactionId",jsonElement);

            String type = fromJsonHelper.extractStringNamed("type" ,jsonElement);

            ENTITY_TYPE entityType = ENTITY_TYPE.fromString(type);

            System.err.println("-------------entity type ----------------"+entityType);

            Page<JournalEntryData> journalEntryData = journalEntryReadPlatformService.retrieveJournalEntriesByTransactionId(transactionId ,entityType);

            for(JournalEntryData j : journalEntryData.getPageItems()){
                Long id = j.getId();

                System.err.println("----------------------id is ---------"+id);

                PaymentVoucher paymentVoucher = new PaymentVoucher(id);
                paymentVoucherList.add(paymentVoucher);

            }

        }

        return paymentVoucherList;

    }

    // we need reference id from this ,this what we get back then use to create voucher
    public CommandProcessingResult createEntriesForVoucher(JsonCommand jsonCommand){

        List<PaymentVoucher> paymentVoucherList = findJournalEntries(jsonCommand);

        int count = 0 ;
        Long referenceId = 0L ;

        for (PaymentVoucher paymentVoucher : paymentVoucherList){

            if(count > 0){
                paymentVoucher.setReferenceId(referenceId);
                paymentVoucherRepository.save(paymentVoucher);
            }
            // reference id should be the same thats what we to use to collectively group these items together
            else{
                paymentVoucherRepository.save(paymentVoucher);
                referenceId = paymentVoucher.getId();
                paymentVoucher.setReferenceId(referenceId);
                paymentVoucherRepository.save(paymentVoucher);
            }
            count++ ;
        }

        System.err.println("-------------reference id is -----------------"+referenceId);
        return CommandProcessingResult.withChanges(referenceId ,null);
    }
}
