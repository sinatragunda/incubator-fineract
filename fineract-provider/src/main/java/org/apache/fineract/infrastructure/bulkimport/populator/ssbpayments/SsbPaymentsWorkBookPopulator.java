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
package org.apache.fineract.infrastructure.bulkimport.populator.ssbpayments;

import org.apache.fineract.infrastructure.bulkimport.constants.SsbPaymentsConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TransactionConstants;
import org.apache.fineract.infrastructure.bulkimport.populator.*;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.shareproducts.data.ShareProductData;
import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SsbPaymentsWorkBookPopulator extends AbstractWorkbookPopulator {


    private OfficeSheetPopulator officeSheetPopulator;
    private ClientSheetPopulatorEx clientSheetPopulator;
    private List<SavingsAccountData> savingsAccountsDataList ;
    private SavingsAccountSheetPopulator savingsAccountSheetPopulator ;
    private List<ClientData> clientDataList ;


    public SsbPaymentsWorkBookPopulator(OfficeSheetPopulator officeSheetPopulator,
                                                ClientSheetPopulatorEx clientSheetPopulator,SavingsAccountSheetPopulator savingsAccountSheetPopulator) {
        this.officeSheetPopulator = officeSheetPopulator;
        this.clientSheetPopulator = clientSheetPopulator;
        this.savingsAccountSheetPopulator = savingsAccountSheetPopulator ;
        this.savingsAccountsDataList = savingsAccountSheetPopulator.getSavingsAccountDataList();

        // added 14/04/2022
        this.clientDataList = clientSheetPopulator.getClients();

    }

    @Override
    public void populate(Workbook workbook,String dateFormat) {

        Sheet ssbPaymentsSheet = workbook.createSheet(TemplatePopulateImportConstants.SSB_PAYMENTS_SHEET_NAME);

        savingsAccountSheetPopulator.populate(workbook ,dateFormat);
        officeSheetPopulator.populate(workbook,dateFormat);
        clientSheetPopulator.populate(workbook,dateFormat);
        setRules(ssbPaymentsSheet,dateFormat);
        setDefaults(ssbPaymentsSheet);
        setLayout(ssbPaymentsSheet);

    }


    private void setDefaults(Sheet worksheet) {

        for(Integer rowNo = 1; rowNo < 1000; rowNo++) {
            Row row = worksheet.createRow(rowNo);
            
            writeFormula(SsbPaymentsConstants.CURRENCY_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE(\"Currency_\",$I" + (rowNo + 1)
                    + "))),\"\",INDIRECT(CONCATENATE(\"Currency_\",$I" + (rowNo + 1) + ")))");

            writeFormula(SsbPaymentsConstants.DOCUMENT_ID_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE(\"Client_Id_\",$A" + (rowNo + 1)
                    + "))),\"\",INDIRECT(CONCATENATE(\"Client_Id_\",$A" + (rowNo + 1) + ")))");

            writeFormula(SsbPaymentsConstants.DDA_FUND_ACCOUNT_ID_COL, row,
                    "IF(ISERROR(INDIRECT(CONCATENATE(\"FUND_ACCOUNT_ID_\",$I" + (rowNo + 1)
                            + "))),\"\",INDIRECT(CONCATENATE(\"FUND_ACCOUNT_ID_\",$I" + (rowNo + 1) + ")))");
        }
    }


    private void setRules(Sheet worksheet,String dateFormat) {

        CellRangeAddressList clientNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(),
                SsbPaymentsConstants.CLIENT_NAME_COL, SsbPaymentsConstants.CLIENT_NAME_COL);

        CellRangeAddressList amountRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(),
                SsbPaymentsConstants.AMOUNT_COL, SsbPaymentsConstants.AMOUNT_COL);

        CellRangeAddressList ddaFundAccountNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(),
                SsbPaymentsConstants.DDA_FUND_ACCOUNT_NAME_COL, SsbPaymentsConstants.DDA_FUND_ACCOUNT_NAME_COL);


        DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet)worksheet);

        setNames(worksheet);

        DataValidationConstraint amountRangeConstraint = validationHelper.createDecimalConstraint(DataValidationConstraint.OperatorType.GREATER_THAN ,"0" ,null);
        DataValidationConstraint clientNameConstraint = validationHelper.createFormulaListConstraint(TemplatePopulateImportConstants.CLIENT_SHEET_NAME);
        DataValidationConstraint ddaFundAccountNameConstraint = validationHelper.createFormulaListConstraint(SsbPaymentsConstants.DDA_FUND_SHEET_NAME);

        DataValidation clientValidation = validationHelper.createValidation(clientNameConstraint, clientNameRange);
        DataValidation amountValidation = validationHelper.createValidation(amountRangeConstraint, amountRange);
        DataValidation ddaFundAccountNameValidation = validationHelper.createValidation(ddaFundAccountNameConstraint ,ddaFundAccountNameRange);

        worksheet.addValidationData(clientValidation);
        worksheet.addValidationData(ddaFundAccountNameValidation);
        worksheet.addValidationData(amountValidation);

    }

    private void setNames(Sheet worksheet) {

        Workbook ssbPaymentsWorkbook = worksheet.getWorkbook();

        //savings accounts
        Name ddaFundAccountsGroup = ssbPaymentsWorkbook.createName();
        ddaFundAccountsGroup.setNameName(SsbPaymentsConstants.DDA_FUND_SHEET_NAME);
        ddaFundAccountsGroup.setRefersToFormula(TemplatePopulateImportConstants.SAVINGS_ACCOUNTS_SHEET_NAME+"!$D$2:$D$" + (savingsAccountsDataList.size() + 1));

        for (int i = 0; i < savingsAccountsDataList.size() ;++i){

            String accountName = savingsAccountsDataList.get(i).getClientName().replaceAll("[ ]" ,"_");
            Name accountNameName  = ssbPaymentsWorkbook.createName();

            accountNameName.setNameName("Fund_Account_Name_"+accountName);
            accountNameName.setRefersToFormula(TemplatePopulateImportConstants.SAVINGS_ACCOUNTS_SHEET_NAME+"!$A$" + (i + 2));

            Name currencyName = ssbPaymentsWorkbook.createName();
            currencyName.setNameName("Currency_"+accountName);
            currencyName.setRefersToFormula(TemplatePopulateImportConstants.SAVINGS_ACCOUNTS_SHEET_NAME+"!$C$" + (i + 2));


            Name fundAccountIdName = ssbPaymentsWorkbook.createName();
            fundAccountIdName.setNameName("Fund_Account_Id_"+accountName);
            fundAccountIdName.setRefersToFormula(TemplatePopulateImportConstants.SAVINGS_ACCOUNTS_SHEET_NAME+"!$A$" + (i + 2));
        }


        // client accounts
        Name clientNameGroup = ssbPaymentsWorkbook.createName();
        clientNameGroup.setNameName("Clients");
        clientNameGroup.setRefersToFormula(TemplatePopulateImportConstants.CLIENT_SHEET_NAME+"!$A$2:$A$" + (clientDataList.size() + 1));

        for (int i = 0; i < clientDataList.size() ;++i){

            String clientName = clientDataList.get(i).displayName().replaceAll("[ ]" ,"_");
            Name clientNameName  = ssbPaymentsWorkbook.createName();

            clientNameName.setNameName("Client_Name"+clientName);
            clientNameName.setRefersToFormula(TemplatePopulateImportConstants.CLIENT_SHEET_NAME+"!$A$" + (i + 2));

            Name accountIdName = ssbPaymentsWorkbook.createName();
            accountIdName.setNameName("Client_Id_"+ clientName);
            accountIdName.setRefersToFormula(TemplatePopulateImportConstants.CLIENT_SHEET_NAME+"!$B$" + (i + 2));

        }
    }

    private void setLayout(Sheet worksheet) {
        
        Row rowHeader=worksheet.createRow(TemplatePopulateImportConstants.ROWHEADER_INDEX);
        rowHeader.setHeight(TemplatePopulateImportConstants.ROW_HEADER_HEIGHT);

        worksheet.setColumnWidth(SsbPaymentsConstants.CLIENT_NAME_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        worksheet.setColumnWidth(SsbPaymentsConstants.LOAN_ACCOUNT_NO_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        worksheet.setColumnWidth(SsbPaymentsConstants.EMPLOYEE_ID_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        worksheet.setColumnWidth(SsbPaymentsConstants.DOCUMENT_ID_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        worksheet.setColumnWidth(SsbPaymentsConstants.AMOUNT_COL,TemplatePopulateImportConstants.MEDIUM_COL_SIZE);
        worksheet.setColumnWidth(SsbPaymentsConstants.DDA_FUND_ACCOUNT_NAME_COL,TemplatePopulateImportConstants.MEDIUM_COL_SIZE);
        worksheet.setColumnWidth(SsbPaymentsConstants.DDA_FUND_ACCOUNT_ID_COL,TemplatePopulateImportConstants.MEDIUM_COL_SIZE);
        worksheet.setColumnWidth(SsbPaymentsConstants.CURRENCY_COL ,TemplatePopulateImportConstants.MEDIUM_COL_SIZE);
        worksheet.setColumnWidth(SsbPaymentsConstants.STAGING_COL ,TemplatePopulateImportConstants.MEDIUM_COL_SIZE);
        


        writeString(SsbPaymentsConstants.CLIENT_NAME_COL,rowHeader,"Client Name");
        writeString(SsbPaymentsConstants.LOAN_ACCOUNT_NO_COL,rowHeader,"Loan Account Number");
        writeString(SsbPaymentsConstants.EMPLOYEE_ID_COL,rowHeader,"Employee Id ");
        writeString(SsbPaymentsConstants.DOCUMENT_ID_COL,rowHeader,"Client Id");
        writeString(SsbPaymentsConstants.AMOUNT_COL,rowHeader,"Amount *");
        writeString(SsbPaymentsConstants.STAGING_COL,rowHeader,"Staging");
        writeString(SsbPaymentsConstants.DDA_FUND_ACCOUNT_NAME_COL,rowHeader,"Fund Account Name");
        writeString(SsbPaymentsConstants.DDA_FUND_ACCOUNT_ID_COL,rowHeader,"Fund Account ID");
        writeString(SsbPaymentsConstants.CURRENCY_COL ,rowHeader ,"Currency");

    }
}
