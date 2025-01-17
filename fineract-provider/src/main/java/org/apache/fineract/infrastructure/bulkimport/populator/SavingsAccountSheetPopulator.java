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
package org.apache.fineract.infrastructure.bulkimport.populator;

import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.template.domain.Template;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;


import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SavingsAccountSheetPopulator extends AbstractWorkbookPopulator {

   private List<SavingsAccountData> savingsAccountDataList;
   private Map<ClientData,List<SavingsAccountData>> clientToSavingsMap;

   private static final int SAVINGS_ACCOUNT_ID_COL=0;
   private static final int SAVING_ACCOUNT_NO=1;
   private static final int CURRENCY_COL=2;
   private static final int CLIENT_NAME=3;
   private static final int AMOUNT=4 ;


    public SavingsAccountSheetPopulator(List<SavingsAccountData> savingsAccountDataList) {
        this.savingsAccountDataList=savingsAccountDataList;
    }

    @Override
    public void populate(Workbook workbook,String dateFormat) {
        Sheet savingsSheet=workbook.createSheet(TemplatePopulateImportConstants.SAVINGS_ACCOUNTS_SHEET_NAME);
        setRules(savingsSheet);
        setLayout(savingsSheet);
        populateSavingsSheet(savingsSheet);
        //savingsSheet.protectSheet("");
    }

    private void populateSavingsSheet(Sheet savingsSheet) {
        int rowIndex=1;
        for (SavingsAccountData savings: savingsAccountDataList) {
            Row row=savingsSheet.createRow(rowIndex++);
            writeLong(SAVINGS_ACCOUNT_ID_COL,row,savings.id());
            writeString(SAVING_ACCOUNT_NO,row,savings.getAccountNo());
            writeString(CURRENCY_COL,row,savings.currency().code());
            writeString(CLIENT_NAME,row,savings.getClientName().replaceAll("[ ]","_"));
        }
    }


    private void setRules(Sheet worksheet){

	    CellRangeAddressList amountRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(),AMOUNT,AMOUNT);
        DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet)worksheet);
        
        DataValidationConstraint amountRangeConstraint = validationHelper.createDecimalConstraint(DataValidationConstraint.OperatorType.GREATER_THAN ,"0" ,null);
        DataValidation amountValidation = validationHelper.createValidation(amountRangeConstraint, amountRange);
        
        worksheet.addValidationData(amountValidation);

   }


    private void setLayout(Sheet savingsSheet) {
        Row rowHeader = savingsSheet.createRow(TemplatePopulateImportConstants.ROWHEADER_INDEX);
        rowHeader.setHeight(TemplatePopulateImportConstants.ROW_HEADER_HEIGHT);

        savingsSheet.setColumnWidth(SAVINGS_ACCOUNT_ID_COL,TemplatePopulateImportConstants.MEDIUM_COL_SIZE);
        writeString(SAVINGS_ACCOUNT_ID_COL,rowHeader,"Savings Account Id");

        savingsSheet.setColumnWidth(SAVING_ACCOUNT_NO,TemplatePopulateImportConstants.MEDIUM_COL_SIZE);
        writeString(SAVING_ACCOUNT_NO,rowHeader,"Savings Account No");

        savingsSheet.setColumnWidth(CURRENCY_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        writeString(CURRENCY_COL,rowHeader,"Currency Code");

        savingsSheet.setColumnWidth(CLIENT_NAME,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        writeString(CLIENT_NAME,rowHeader,"Client Name");

        savingsSheet.setColumnWidth(AMOUNT,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        writeString(AMOUNT,rowHeader,"Amount (Only When DDA Funding)");

    }

    // added 07/02/2022 at 0400
    public void filterDDAFundsAccounts(Predicate<SavingsAccountData> ddaFundsPredicate){
        this.savingsAccountDataList = savingsAccountDataList.stream().filter(ddaFundsPredicate).collect(Collectors.toList());
    }

    public List<SavingsAccountData> getSavingsAccountDataList(){
        return this.savingsAccountDataList;
    }
}
