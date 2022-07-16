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
package org.apache.fineract.infrastructure.bulkimport.importhandler.savings;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TransactionConstants;
import org.apache.fineract.infrastructure.bulkimport.data.Count;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandlerUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.equitygrowth.EquityGrowthImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.DateSerializer;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.SavingsAccountTransactionEnumValueSerialiser;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.*;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionEnumData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthDividendsRepository;
import org.apache.fineract.portfolio.savings.repo.EquityGrowthOnSavingsAccountRepository;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.LocalDate;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SavingsTransactionImportHandler implements ImportHandler {
    private Workbook workbook;
    private List<SavingsAccountTransactionData> savingsTransactions;
    private String savingsAccountId = "";

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    // Added 05/10/2021
    private final ClientReadPlatformService clientReadPlatformService ;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;

    //Added 08/10/2021
    private final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository;
    private final EquityGrowthDividendsRepository equityGrowthDividendsRepository;
    private final SavingsAccountAssembler savingsAccountAssembler;

    @Autowired
    public SavingsTransactionImportHandler(final PortfolioCommandSourceWritePlatformService
        commandsSourceWritePlatformService , final ClientReadPlatformService clientReadPlatformService , final SavingsAccountReadPlatformService savingsAccountReadPlatformService , final EquityGrowthDividendsRepository equityGrowthDividendsRepository , final EquityGrowthOnSavingsAccountRepository equityGrowthOnSavingsAccountRepository , final SavingsAccountAssembler savingsAccountAssembler) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.clientReadPlatformService = clientReadPlatformService ;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService ;
        this.equityGrowthDividendsRepository = equityGrowthDividendsRepository ;
        this.equityGrowthOnSavingsAccountRepository = equityGrowthOnSavingsAccountRepository;
        this.savingsAccountAssembler = savingsAccountAssembler;
    }

    @Override
    public Count process(Workbook workbook, String locale, String dateFormat) {
        this.workbook=workbook;
        this.savingsTransactions=new ArrayList<>();
        
        readExcelFile(locale,dateFormat);

        // added new check if savings account is null then set this value only ,some its tedious to list all clients with account numbers and balances
        // hence generic way has to be used to skip that and just get the client id only
        // especially if its migrations
        this.savingsTransactions.stream().filter(e->{
            boolean isSavingsAccountNumberPresent = Optional.ofNullable(e.getSavingsAccountId()).isPresent();
            return !isSavingsAccountNumberPresent;
        }).forEach(f ->{
            String clientExternalId = f.getClientExternalId();
            SavingsAccountData savingsAccountData = SavingsAccountToClientLinkingHelper.linkBlindlySavingsAccount(clientReadPlatformService ,savingsAccountReadPlatformService ,clientExternalId);
            Optional.ofNullable(savingsAccountData).ifPresent(acc->{
                Long accountId = acc.id();
                f.setSavingsAccountId(accountId);
                // so that it doesnt fail validations to do with parameter not supported
                f.setClientExternalId(null);
            });
        });

        return importEntity(dateFormat);
    }

    public void readExcelFile(String locale, String dateFormat) {
        
        Sheet savingsTransactionSheet = workbook.getSheet(TemplatePopulateImportConstants.SAVINGS_TRANSACTION_SHEET_NAME);
        
        Integer noOfEntries = ImportHandlerUtils.getNumberOfRowsWithErrorHandling(savingsTransactionSheet, TransactionConstants.AMOUNT_COL);

        for (int rowIndex = 1; rowIndex <= noOfEntries; rowIndex++) {

            Row row = savingsTransactionSheet.getRow(rowIndex);
            if(ImportHandlerUtils.isNotImported(row, TransactionConstants.STATUS_COL)) {

                SavingsAccountTransactionData savingsAccountTransactionData = readSavingsTransaction(row, locale, dateFormat);
                Optional.ofNullable(savingsAccountTransactionData).ifPresent(e->{
                    // does it have any equity transaction ?
                    savingsTransactions.add(e);
                });
            }
        }
    }

    private SavingsAccountTransactionData readSavingsTransaction(Row row,String locale, String dateFormat) {

        String savingsAccountIdCheck=null;
        Long savingsAccountIdL = null ;

        if (ImportHandlerUtils.readAsLong(TransactionConstants.SAVINGS_ACCOUNT_NO_COL, row)!=null)
            savingsAccountIdCheck = ImportHandlerUtils.readAsLong(TransactionConstants.SAVINGS_ACCOUNT_NO_COL, row).toString();
        if(savingsAccountIdCheck!=null) {
            //savingsAccountId = savingsAccountIdCheck;
            /// value not not null here so try to convert it to long instead of null value
            try {
                savingsAccountIdL = Long.parseLong(savingsAccountIdCheck);
            } catch (NumberFormatException n) {

            }
        }


        String transactionType = ImportHandlerUtils.readAsString(TransactionConstants.TRANSACTION_TYPE_COL, row);
        SavingsAccountTransactionEnumData savingsAccountTransactionEnumData=new SavingsAccountTransactionEnumData(null,null,transactionType);

        BigDecimal amount=null;
        if (ImportHandlerUtils.readAsDouble(TransactionConstants.AMOUNT_COL, row)!=null)
            amount = BigDecimal.valueOf(ImportHandlerUtils.readAsDouble(TransactionConstants.AMOUNT_COL, row));

        LocalDate transactionDate = ImportHandlerUtils.readAsDate(TransactionConstants.TRANSACTION_DATE_COL, row);
        
        // this is not usually provided so we just need to default to 1 if its null 
        // modified 09/12/2021 6.00am
        String paymentType[] = {ImportHandlerUtils.readAsString(TransactionConstants.PAYMENT_TYPE_COL, row)};
        Long paymentTypeIdEx[] = {1L};

        Optional.ofNullable(paymentType[0]).ifPresent(e->{
            paymentTypeIdEx[0] =  ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.EXTRAS_SHEET_NAME), paymentType[0]);
        });

        Long paymentTypeId = paymentTypeIdEx[0];

        String accountNumber = ImportHandlerUtils.readAsString(TransactionConstants.ACCOUNT_NO_COL, row);
        String checkNumber = ImportHandlerUtils.readAsString(TransactionConstants.CHECK_NO_COL, row);
        String routingCode = ImportHandlerUtils.readAsString(TransactionConstants.ROUTING_CODE_COL, row);
        String receiptNumber = ImportHandlerUtils.readAsString(TransactionConstants.RECEIPT_NO_COL, row);
        String bankNumber = ImportHandlerUtils.readAsString(TransactionConstants.BANK_NO_COL, row);

        String clientExternalId = ImportHandlerUtils.readAsString(TransactionConstants.CLIENT_EXTERNAL_ID_COL ,row);

        // Added 08/10/2021
        Double equityBalance = ImportHandlerUtils.readAsDouble(TransactionConstants.EQUITY_BALANCE_ID_COL ,row);



        //added 08/12/2021
        String note = ImportHandlerUtils.readAsString(TransactionConstants.NOTE_COL ,row);

        SavingsAccountTransactionData savingsAccountTransactionData = SavingsAccountTransactionData.importInstance(amount, transactionDate, paymentTypeId, accountNumber,
                checkNumber, routingCode, receiptNumber, bankNumber, savingsAccountIdL, savingsAccountTransactionEnumData, row.getRowNum(),locale,dateFormat);

        Optional.ofNullable(clientExternalId).ifPresent(e->{
            savingsAccountTransactionData.setClientExternalId(e);
        });

        Optional.ofNullable(equityBalance).ifPresent(e->{
            System.err.println("---equity balance is -------------"+equityBalance.doubleValue());
            savingsAccountTransactionData.setEquityBalance(new BigDecimal(equityBalance));
        });

        // Added 08/12/2021
        addTransactionNote(savingsAccountTransactionData ,note);
        return savingsAccountTransactionData ;

    }

    public Count importEntity(String dateFormat) {
        Sheet savingsTransactionSheet = workbook.getSheet(TemplatePopulateImportConstants.SAVINGS_TRANSACTION_SHEET_NAME);
        int successCount=0;
        int errorCount=0;
        String errorMessage="";
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));
        gsonBuilder.registerTypeAdapter(SavingsAccountTransactionEnumData.class
                ,new SavingsAccountTransactionEnumValueSerialiser());

        for (SavingsAccountTransactionData transaction : savingsTransactions) {
            try {
                JsonObject savingsTransactionJsonob= gsonBuilder.create().toJsonTree(transaction).getAsJsonObject();
                savingsTransactionJsonob.remove("transactionType");
                savingsTransactionJsonob.remove("reversed");
                savingsTransactionJsonob.remove("interestedPostedAsOn");
                String payload= savingsTransactionJsonob.toString();
                CommandWrapper commandRequest=null;

                if (transaction.getTransactionType().getValue().equals("Withdrawal")) {
                    commandRequest = new CommandWrapperBuilder() //
                            .savingsAccountWithdrawal(transaction.getSavingsAccountId()) //
                            .withJson(payload) //
                            .build(); //

                }else if (transaction.getTransactionType().getValue().equals("Deposit")){
                    commandRequest = new CommandWrapperBuilder() //
                            .savingsAccountDeposit(transaction.getSavingsAccountId()) //
                            .withJson(payload) //
                            .build();
                }

                final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(commandRequest);
                successCount++;

                System.err.println("-----------------------------------success count is ---------------"+successCount);


                Cell statusCell = savingsTransactionSheet.getRow(transaction.getRowIndex()).createCell(TransactionConstants.STATUS_COL);
                statusCell.setCellValue(TemplatePopulateImportConstants.STATUS_CELL_IMPORTED);
                statusCell.setCellStyle(ImportHandlerUtils.getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            }catch (RuntimeException ex){
                errorCount++;

                System.err.println("----------------------------------errror count is ----------------------"+errorCount);
                ex.printStackTrace();

                errorMessage=ImportHandlerUtils.getErrorMessage(ex);
                
                ImportHandlerUtils.writeErrorMessage(savingsTransactionSheet,transaction.getRowIndex(),errorMessage,TransactionConstants.STATUS_COL);
            }
        }


        /// when dome with whole list we should deposit to equity growth if possible
        EquityGrowthImportHandler.postOpeningBalance(equityGrowthDividendsRepository ,equityGrowthOnSavingsAccountRepository , savingsTransactions ,savingsAccountAssembler ,1L);

        savingsTransactionSheet.setColumnWidth(TransactionConstants.STATUS_COL, TemplatePopulateImportConstants.SMALL_COL_SIZE);
        ImportHandlerUtils.writeString(TransactionConstants.STATUS_COL, savingsTransactionSheet.getRow(TransactionConstants.STATUS_COL), TemplatePopulateImportConstants.STATUS_COL_REPORT_HEADER);
        return Count.instance(successCount,errorCount);
    }


    public void addTransactionNote(SavingsAccountTransactionData savingsAccountTransactionData ,String note){

        boolean hasNote = Optional.ofNullable(note).isPresent();        
        if(!hasNote){
            note = "Migration Balance";
        }
        savingsAccountTransactionData.setNote(note);
    }

}
