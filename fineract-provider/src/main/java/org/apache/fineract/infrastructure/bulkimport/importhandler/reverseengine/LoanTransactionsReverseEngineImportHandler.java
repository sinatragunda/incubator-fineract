/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 March 2023 at 11:46
 */
package org.apache.fineract.infrastructure.bulkimport.importhandler.reverseengine;
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
package org.apache.fineract.infrastructure.bulkimport.importhandler.loanrepayment;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.constants.LoanRepaymentConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.data.Count;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandlerUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.DateSerializer;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.*;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class LoanRepaymentImportHandler implements ImportHandler {
    
    private Workbook workbook;
    private List<LoanTransactionData> loanTransactions;
    private 

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    @Autowired
    public LoanRepaymentImportHandler(final PortfolioCommandSourceWritePlatformService
                                              commandsSourceWritePlatformService) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @Override
    public Count process(Workbook workbook, String locale, String dateFormat) {
        this.workbook = workbook;
        this.loanTransactions = new ArrayList<>();
        readExcelFile(locale,dateFormat);
        return reverse(dateFormat);
    }

    public void readExcelFile(String locale, String dateFormat) {
        
        Sheet loanTransactionSheet = workbook.getSheet(0);
        
        Integer noOfEntries = ImportHandlerUtils.getNumberOfRows(loanRepaymentSheet, LoanRepaymentConstants.AMOUNT_COL);
        
        for (int rowIndex = 1; rowIndex <= noOfEntries; rowIndex++) {
            Row row = loanTransactionsSheet.getRow(rowIndex);
            LoanTransactionData loanTransactionData = readLoanTransaction(row ,locale ,dateFormat);
            boolean has = OptionalHelper.isPresent(loanTransactionData);
            if(has){
                loanTransactions.add(readLoanTransaction(row,locale,dateFormat));
            }
        }
    }

    private LoanTransactionData readLoanTransaction(Row row,String locale, String dateFormat) {
        
        Long transactionId = ImportHandlerUtils.readAsLong(TemplatePopulateImportConstants.ID_NAME_COL);
        boolean has = OptionalHelper.isPresent(transactionId);

        if(!has){
            return null ;
        }
        LoanTransactionData loanTransactionData = loanTransactionReadPlatformService.retrieveOne(transactionId);
        return loanTransactionData;
    }


    public Count reverse(String dateFormat){

        Sheet loanRepaymentSheet = workbook.getSheet(0);
        int successCount=0;
        int errorCount=0;
        String errorMessage="";
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));

        for (LoanTransactionData loanTransactionData : loanTransactions) {
            try {

                JsonObject loanTransactionJsonob=gsonBuilder.create().toJsonTree(loanTransactionData).getAsJsonObject();
                String payload=loanRepaymentJsonob.toString();
                
                final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                        .loanRepaymentTransaction(loanRepayment.getAccountId().longValue()) //
                        .withJson(payload) //
                        .build(); //
                final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(commandRequest);
                successCount++;
                Cell statusCell = loanRepaymentSheet.getRow(loanRepayment.getRowIndex()).createCell(LoanRepaymentConstants.STATUS_COL);
                statusCell.setCellValue(TemplatePopulateImportConstants.STATUS_CELL_IMPORTED);
                statusCell.setCellStyle(ImportHandlerUtils.getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            }catch (RuntimeException ex){
                errorCount++;
                ex.printStackTrace();
                errorMessage=ImportHandlerUtils.getErrorMessage(ex);
                ImportHandlerUtils.writeErrorMessage(loanTransactionSheet,loanTransaction.getRowIndex(),errorMessage,LoanRepaymentConstants.STATUS_COL);
            }

        }

        loanTransactionSheet.setColumnWidth(LoanRepaymentConstants.STATUS_COL, TemplatePopulateImportConstants.SMALL_COL_SIZE);
        ImportHandlerUtils.writeString(LoanRepaymentConstants.STATUS_COL,
                loanRepaymentSheet.getRow(TemplatePopulateImportConstants.ROWHEADER_INDEX),
                TemplatePopulateImportConstants.STATUS_COL_REPORT_HEADER);
        return Count.instance(successCount,errorCount);
    }
}

