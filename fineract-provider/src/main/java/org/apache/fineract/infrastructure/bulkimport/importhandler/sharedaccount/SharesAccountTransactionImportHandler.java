/*

    Created by Sinatra Gunda
    At 5:27 AM on 10/16/2021

*/
package org.apache.fineract.infrastructure.bulkimport.importhandler.sharedaccount;

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
import org.apache.fineract.infrastructure.bulkimport.constants.SharedAccountsConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.data.Count;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandlerUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.DateSerializer;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.SharesAccountImportHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountData;
import org.apache.fineract.portfolio.shareaccounts.domain.*;
import org.apache.fineract.portfolio.shareaccounts.handler.ApplyAddtionalSharesCommandHandler;
import org.apache.fineract.portfolio.shareaccounts.handler.ApproveAddtionalSharesCommandHandler;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountReadPlatformService;
import org.apache.fineract.wese.helper.ComparatorUtility;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.ObjectNodeHelper;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Service
public class SharesAccountTransactionImportHandler implements ImportHandler {

    private Workbook workbook;
    private List<ApplyAdditionalShares> applyAdditionalSharesList;
    private List<String>statuses;

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    // added 16/10/2021
    private final ClientReadPlatformService clientReadPlatformService;
    private final ShareAccountReadPlatformService shareAccountReadPlatformService;
    private final FromJsonHelper fromJsonHelper ;
    private final ApplyAddtionalSharesCommandHandler applyAddtionalSharesCommandHandler;
    private final ApproveAddtionalSharesCommandHandler approveAddtionalSharesCommandHandler;

    @Autowired
    public SharesAccountTransactionImportHandler(final PortfolioCommandSourceWritePlatformService
                                              commandsSourceWritePlatformService ,final ClientReadPlatformService clientReadPlatformService ,final ShareAccountReadPlatformService shareAccountReadPlatformService,final ApproveAddtionalSharesCommandHandler approveAddtionalSharesCommandHandler ,final ApplyAddtionalSharesCommandHandler applyAddtionalSharesCommandHandler ,final FromJsonHelper fromJsonHelper) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.clientReadPlatformService = clientReadPlatformService ;
        this.shareAccountReadPlatformService = shareAccountReadPlatformService;
        this.fromJsonHelper = fromJsonHelper;
        this.approveAddtionalSharesCommandHandler = approveAddtionalSharesCommandHandler ;
        this.applyAddtionalSharesCommandHandler = applyAddtionalSharesCommandHandler;
    }
    @Override
    public Count process(Workbook workbook, String locale, String dateFormat) {

        this.workbook = workbook;
        this.applyAdditionalSharesList = new ArrayList<>();
        statuses = new ArrayList<String>();
        readExcelFile(locale,dateFormat);
        return importEntity(dateFormat);
    }

    public void readExcelFile(String locale, String dateFormat) {

        Sheet sharedAccountTransactionSheet = workbook.getSheet(TemplatePopulateImportConstants.SHARED_ACCOUNTS_SHEET_NAME);
        Integer noOfEntries = ImportHandlerUtils.getNumberOfRows(sharedAccountTransactionSheet, TemplatePopulateImportConstants.FIRST_COLUMN_INDEX);

        for (int rowIndex=1;rowIndex<=noOfEntries;rowIndex++){

            Row row;
            row = sharedAccountTransactionSheet.getRow(rowIndex);

            if (ImportHandlerUtils.isNotImported(row, SharedAccountsConstants.STATUS_COL)){

                System.err.println("------------------row index is ----------------"+rowIndex);

                ApplyAdditionalShares applyAdditionalShares = null ;
                try{
                    applyAdditionalShares = readSharesTransactions(row ,locale ,dateFormat);
                }
                catch (Exception n){
                    System.err.println("=============================null pointer exception caught here again on index "+rowIndex);
                }

                Optional.ofNullable(applyAdditionalShares).ifPresent(e->{
                    applyAdditionalSharesList.add(e);
                });
            }
        }
    }

    private ApplyAdditionalShares readSharesTransactions(Row row, String locale, String dateFormat) {

        String clientName = ImportHandlerUtils.readAsString(SharedAccountsConstants.CLIENT_NAME_COL, row);

        Long clientId[] = {ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.CLIENT_SHEET_NAME), clientName)};

        // if client id not present from using the client name to get it then use the client external id if mentioned ,compare to 0L cause default set to 0L
        boolean isClientIdDefault = ComparatorUtility.isLongZero(clientId[0]);

        if(isClientIdDefault){
            clientId[0] = clientIdFromExternalId(row);
        }

        String productName = ImportHandlerUtils.readAsString(SharedAccountsConstants.PRODUCT_COL, row);

        Long productId = ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.SHARED_PRODUCTS_SHEET_NAME),productName);

        ShareAccountData shareAccount = SharesAccountImportHelper.getClientShareAccount(shareAccountReadPlatformService ,productId ,clientId[0]);

        Long shareAccountId[] = {0L};

        Optional.ofNullable(shareAccount).ifPresent(e->{
            shareAccountId[0] = e.getId();
        });

        LocalDate submittedOnDate = ImportHandlerUtils.readAsDate(SharedAccountsConstants.SUBMITTED_ON_COL,row);
        Integer totNoOfShares = ImportHandlerUtils.readAsInt(SharedAccountsConstants.TOTAL_NO_SHARES_COL,row);

        // added new structure on 15/10/2021
        Double unitPrice = ImportHandlerUtils.readAsDouble(SharedAccountsConstants.TODAYS_PRICE_COL ,row);

        // if number of shares is not defined then we using the other value ,amount of shares the client has to determine shares
        boolean numberOfSharesDefined = Optional.ofNullable(totNoOfShares).isPresent();

        if(!numberOfSharesDefined){

            Double amount  = ImportHandlerUtils.readAsDouble(SharedAccountsConstants.SHARE_AMOUNT_COL ,row);
            totNoOfShares = SharesAccountImportHelper.getTotalNumberOfShares(amount ,unitPrice);
        }

        String status = ImportHandlerUtils.readAsString(SharedAccountsConstants.STATUS_COL,row);
        statuses.add(status);

        // we still need to get default savings account here but to remove scatterness lets put it in a seperate function now son
        ApplyAdditionalShares applyAddtionalShares = new ApplyAdditionalShares(shareAccountId[0] ,totNoOfShares ,submittedOnDate ,row.getRowNum());
        return applyAddtionalShares ;

    }

    private Long clientIdFromExternalId(Row row) {

        Long clientId[] = {null} ;
        String externalId = ImportHandlerUtils.readAsString(SharedAccountsConstants.CLIENT_EXTERNAL_ID_COL ,row);
        ClientData client = clientReadPlatformService.retrieveOneByExternalId(externalId);
        Optional.ofNullable(client).ifPresent(e->{
            clientId[0] = e.getId();
        });
        return clientId[0];
    }

    public Count importEntity(String dateFormat) {

        Sheet sharedAccountTransactionsSheet = workbook.getSheet(TemplatePopulateImportConstants.SHARED_ACCOUNTS_SHEET_NAME);
        int successCount=0;
        int errorCount=0;
        String errorMessage="";

        ObjectNode node = ObjectNodeHelper.objectNode();
        node.put("dateFormat" ,"dd MMMM yyyy");
        node.put("locale","en");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));

        for (ApplyAdditionalShares applyAdditionalShares: applyAdditionalSharesList) {

            try {

                Long shareAccountId = applyAdditionalShares.getSharesAccountId();

                JsonObject sharesTransactionJson = gsonBuilder.create().toJsonTree(applyAdditionalShares).getAsJsonObject();

                sharesTransactionJson.remove("sharesAccountId");
                sharesTransactionJson.remove("rowIndex");

                String payload = sharesTransactionJson.toString();
                JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

                final CommandProcessingResult result = applyAddtionalSharesCommandHandler.processCommand(jsonCommand);

                System.err.println("-----------payload is -------------"+payload);

                Long additionalSharesId = (Long)result.getChanges().get("additionalshares");
                //approve sonme shit ass transaction here
                Optional.ofNullable(additionalSharesId).ifPresent(e->{

                    System.err.println("--------------additional shares id is -----------"+additionalSharesId);

                    ObjectNode objectNode = ObjectNodeHelper.objectNode();
                    objectNode.put("id" ,additionalSharesId);
                    node.putPOJO("requestedShares",objectNode);

                    System.err.println("--------------approve command is ------------"+node.toString());

                    JsonCommand jsonCommandApproveShares = JsonCommandHelper.jsonCommand(fromJsonHelper ,node.toString());
                    final CommandProcessingResult result1 = approveAddtionalSharesCommandHandler.processCommand(jsonCommand);

                });

                successCount++;
                Cell statusCell = sharedAccountTransactionsSheet.getRow(applyAdditionalShares.getRowIndex())
                        .createCell(SharedAccountsConstants.STATUS_COL);
                statusCell.setCellValue(TemplatePopulateImportConstants.STATUS_CELL_IMPORTED);
                statusCell.setCellStyle(ImportHandlerUtils.getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            }catch (RuntimeException ex){
                errorCount++;
                ex.printStackTrace();
                errorMessage=ImportHandlerUtils.getErrorMessage(ex);
                ImportHandlerUtils.writeErrorMessage(sharedAccountTransactionsSheet,applyAdditionalShares.getRowIndex(),errorMessage,SharedAccountsConstants.STATUS_COL);
            }
        }

        sharedAccountTransactionsSheet.setColumnWidth(SharedAccountsConstants.STATUS_COL, TemplatePopulateImportConstants.SMALL_COL_SIZE);
        ImportHandlerUtils.writeString(SharedAccountsConstants.STATUS_COL, sharedAccountTransactionsSheet.getRow(TemplatePopulateImportConstants.ROW_HEADER_HEIGHT),
                TemplatePopulateImportConstants.STATUS_COL_REPORT_HEADER);
        return Count.instance(successCount,errorCount);
    }


}
