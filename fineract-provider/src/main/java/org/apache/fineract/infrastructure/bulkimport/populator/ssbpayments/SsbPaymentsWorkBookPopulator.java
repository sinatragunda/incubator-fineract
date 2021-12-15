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
import org.apache.fineract.infrastructure.bulkimport.populator.AbstractWorkbookPopulator;
import org.apache.fineract.infrastructure.bulkimport.populator.ClientSheetPopulator;
import org.apache.fineract.infrastructure.bulkimport.populator.SavingsAccountSheetPopulator;
import org.apache.fineract.infrastructure.bulkimport.populator.SharedProductsSheetPopulator;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.shareproducts.data.ShareProductData;
import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;

public class SsbPaymentsWorkBookPopulator extends AbstractWorkbookPopulator {

    private  ClientSheetPopulator clientSheetPopulator;
    
    public SsbPaymentsWorkBookPopulator(ClientSheetPopulator clientSheetPopulator) {
        this.clientSheetPopulator=clientSheetPopulator;
    
    }

    @Override
    public void populate(Workbook workbook,String dateFormat) {

        Sheet SsbPaymentsSheet = workbook.createSheet(TemplatePopulateImportConstants.SSB_PAYMENTS_SHEET_NAME);
        clientSheetPopulator.populate(workbook,dateFormat);
        setLayout(SsbPaymentsSheet);
        setRules(SsbPaymentsSheet,dateFormat);
        setDefaults(SsbPaymentsSheet);
    }

    private void setDefaults(Sheet SsbPaymentsSheet) {

    }

    private void setRules(Sheet SsbPaymentsSheet,String dateFormat) {

        CellRangeAddressList clientNameRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(),
                SsbPaymentsConstants.CLIENT_NAME_COL,SsbPaymentsConstants.CLIENT_NAME_COL);

        DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet) SsbPaymentsSheet);
        setNames(SsbPaymentsSheet);

        DataValidationConstraint clientNameConstraint = validationHelper.createFormulaListConstraint("Clients");

        DataValidationConstraint booleanConstraint=validationHelper.createExplicitListConstraint(new String[]{"True","False"});
        DataValidation clientValidation = validationHelper.createValidation(clientNameConstraint, clientNameRange);

        SsbPaymentsSheet.addValidationData(clientValidation);

    }

    private void setNames(Sheet SsbPaymentsSheet) {
        
        List<ClientData> clients=clientSheetPopulator.getClients();

        Workbook SsbPaymentsWorkbook= SsbPaymentsSheet.getWorkbook();

        Name clientsGroup=SsbPaymentsWorkbook.createName();
        clientsGroup.setNameName("Clients");
        clientsGroup.setRefersToFormula(TemplatePopulateImportConstants.CLIENT_SHEET_NAME+"!$B$2:$B$"+clients.size()+1);

    }

    private void setLayout(Sheet worksheet) {
        
        Row rowHeader=worksheet.createRow(TemplatePopulateImportConstants.ROWHEADER_INDEX);
        rowHeader.setHeight(TemplatePopulateImportConstants.ROW_HEADER_HEIGHT);

        worksheet.setColumnWidth(SsbPaymentsConstants.CLIENT_NAME_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        writeString(SsbPaymentsConstants.CLIENT_NAME_COL,rowHeader,"Client Name");

        worksheet.setColumnWidth(SsbPaymentsConstants.LOAN_ACCOUNT_NO_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        writeString(SsbPaymentsConstants.LOAN_ACCOUNT_NO_COL,rowHeader,"Loan Account Number");

        worksheet.setColumnWidth(SsbPaymentsConstants.EMPLOYEE_ID_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        writeString(SsbPaymentsConstants.EMPLOYEE_ID_COL,rowHeader,"Employee Id ");

        worksheet.setColumnWidth(SsbPaymentsConstants.DOCUMENT_ID_COL,TemplatePopulateImportConstants.SMALL_COL_SIZE);
        writeString(SsbPaymentsConstants.DOCUMENT_ID_COL,rowHeader,"Document Id or Client ID *");

        worksheet.setColumnWidth(SsbPaymentsConstants.AMOUNT_COL,TemplatePopulateImportConstants.MEDIUM_COL_SIZE);
        writeString(SsbPaymentsConstants.AMOUNT_COL,rowHeader,"Amount *");

    }
}
