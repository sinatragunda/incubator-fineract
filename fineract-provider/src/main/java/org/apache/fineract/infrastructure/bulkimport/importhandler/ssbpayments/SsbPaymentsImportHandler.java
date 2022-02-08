/*

    Created by Sinatra Gunda
    At 5:53 PM on 2/7/2022

*/
package org.apache.fineract.infrastructure.bulkimport.importhandler.ssbpayments;


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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.constants.SavingsConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.SsbPaymentsConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.data.Count;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandlerUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.DateSerializer;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.EnumOptionDataIdSerializer;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountChargeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsActivation;
import org.apache.fineract.portfolio.savings.data.SavingsApproval;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.wese.helper.TimeHelper;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SsbPaymentsImportHandler implements ImportHandler {

    private Workbook workbook;
    private List<SavingsAccountData> savings;
    private List<SavingsApproval> approvalDates;
    private List<SavingsActivation> activationDates;
    private List<String>statuses;

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final SavingsAccountDomainService savingsAccountDomainService ;

    @Autowired
    public SsbPaymentsImportHandler(final PortfolioCommandSourceWritePlatformService
                                        commandsSourceWritePlatformService , final SavingsAccountDomainService savingsAccountDomainService) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.savingsAccountDomainService = savingsAccountDomainService ;
    }

    @Override
    public Count process(Workbook workbook, String locale, String dateFormat) {
        this.workbook=workbook;
        this.savings=new ArrayList<>();
        this.approvalDates=new ArrayList<>();
        this.activationDates=new ArrayList<>();
        this.statuses=new ArrayList<>();
        readExcelFile(locale,dateFormat);
        //return importEntity(dateFormat);
        return null ;
    }

    public void readExcelFile(String locale, String dateFormat) {

        Sheet savingsSheet = workbook.getSheet(TemplatePopulateImportConstants.SAVINGS_ACCOUNTS_SHEET_NAME);
        Integer noOfEntries = ImportHandlerUtils.getNumberOfRows(savingsSheet, TemplatePopulateImportConstants.FIRST_COLUMN_INDEX);

        for (int rowIndex = 1; rowIndex <= noOfEntries; rowIndex++) {
            Row row;
            row = savingsSheet.getRow(rowIndex);
            if (ImportHandlerUtils.isNotImported(row, SavingsConstants.STATUS_COL)) {
                savings.add(readSavings(row,locale,dateFormat));
                //approvalDates.add(readSavingsApproval(row,locale,dateFormat));
                //activationDates.add(readSavingsActivation(row,locale,dateFormat));
            }
        }
    }

    private SavingsAccountData readSavings(Row row,String locale, String dateFormat) {

        Long accountId = ImportHandlerUtils.readAsLong(SsbPaymentsConstants.DDA_FUND_ACCOUNT_ID_COL ,row);

        return null ;
    }

}

