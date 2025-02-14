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
package org.apache.fineract.infrastructure.bulkimport.importhandler.loan;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.bulkimport.constants.LoanConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.data.Count;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandlerUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.DateSerializer;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.EnumOptionDataValueSerializer;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.*;
import org.apache.fineract.portfolio.loanaccount.helper.NkwaziLoanAdjustmentsHelper;
import org.apache.fineract.portfolio.loanproduct.enumerations.LOAN_FACTOR_SOURCE_ACCOUNT_TYPE;
import org.apache.fineract.wese.helper.ComparatorUtility;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

// Added 14/10/2021
import org.apache.fineract.infrastructure.core.service.DateUtils;


@Service
public class LoanImportHandler implements ImportHandler {
    private Workbook workbook;
    private List<LoanAccountData> loans;
    private List<LoanApprovalData> approvalDates;
    private List<LoanTransactionData> loanRepayments;
    private List<DisbursementData> disbursalDates;
    private List<String> statuses;

    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    // Added 04/10/2021
    private final ClientReadPlatformService clientReadPlatformService ;

    @Autowired
    public LoanImportHandler(final PortfolioCommandSourceWritePlatformService
            commandsSourceWritePlatformService ,final  ClientReadPlatformService clientReadPlatformService) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
    }
    @Override
    public Count process(Workbook workbook, String locale, String dateFormat) {
        this.workbook=workbook;
        this.loans = new ArrayList<>();
        this.approvalDates = new ArrayList<>();
        this.loanRepayments = new ArrayList<>();
        this.disbursalDates = new ArrayList<>();
        this.statuses=new ArrayList<>();
        readExcelFile(locale,dateFormat);
        return importEntity(dateFormat);
    }

    public void readExcelFile(final String locale, final String dateFormat) {

        workbook.setForceFormulaRecalculation(true);

        Sheet loanSheet = workbook.getSheet(TemplatePopulateImportConstants.LOANS_SHEET_NAME);
        Integer noOfEntries = ImportHandlerUtils.getNumberOfRows(loanSheet, TemplatePopulateImportConstants.FIRST_COLUMN_INDEX);

        try{    
            for (int rowIndex = 1; rowIndex <= noOfEntries; rowIndex++) {

                    Row row;
                    row = loanSheet.getRow(rowIndex);
                    if ( ImportHandlerUtils.isNotImported(row, LoanConstants.STATUS_COL)) {
                        try{
                            addLoanToList(locale, dateFormat, row);
                        }
                        catch (FormulaParseException f){
                            continue;
                        }
                    }

                    System.err.println("----------------move to next value ");
            }            
        }
        catch(Exception n){
            n.printStackTrace();
        }


        System.err.println("------------------------done now processing loans ------------"+noOfEntries);
    }

    private void addLoanToList(String locale, String dateFormat, Row row) {

        LoanAccountData loanAccountData = readLoan(row ,locale ,dateFormat);

        Optional.ofNullable(loanAccountData).ifPresent(e->{

            loans.add(loanAccountData);
            approvalDates.add(readLoanApproval(row,locale,dateFormat));
            
            disbursalDates.add(readDisbursalData(row,locale,dateFormat));
            LoanTransactionData loanTransactionData = readLoanRepayment(row ,locale ,dateFormat);
            
            Optional.ofNullable(loanTransactionData).ifPresent(e1->{
                loanRepayments.add(e1);
            });
        });
    }

    private LoanTransactionData readLoanRepayment(Row row,String locale,String dateFormat) {
        BigDecimal repaymentAmount=null;
        if (ImportHandlerUtils.readAsDouble(LoanConstants.TOTAL_AMOUNT_REPAID_COL, row)!=null)
            repaymentAmount = BigDecimal.valueOf(ImportHandlerUtils.readAsDouble(LoanConstants.TOTAL_AMOUNT_REPAID_COL, row));
        
        LocalDate lastRepaymentDate = ImportHandlerUtils.readAsDate(LoanConstants.LAST_REPAYMENT_DATE_COL, row);
        
        /// if last repayment date is in future lets make it to today current date ,some system errors regarding imported data from Nkwazi 
        if(lastRepaymentDate.isAfter(DateUtils.getLocalDateOfTenant())){
            lastRepaymentDate = DateUtils.getLocalDateOfTenant();
        }

        String repaymentType = ImportHandlerUtils.readAsString(LoanConstants.REPAYMENT_TYPE_COL, row);

        boolean isRepaymentTypePresent = Optional.of(repaymentType).isPresent();

        if(!isRepaymentTypePresent){
            repaymentType = ImportHandlerUtils.readAsString(LoanConstants.DISBURSED_PAYMENT_TYPE_COL ,row);
        }

        Long repaymentTypeId = ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.EXTRAS_SHEET_NAME), repaymentType);
        
        if(repaymentAmount!=null&&lastRepaymentDate!=null&&repaymentType!=null&&repaymentTypeId!=null)
            return  LoanTransactionData.importInstance(repaymentAmount, lastRepaymentDate, repaymentTypeId, row.getRowNum(),locale,dateFormat);
        else
            return null;
    }

    private DisbursementData readDisbursalData(Row row,String locale,String dateFormat) {

        //LocalDate disbursedDate = ImportHandlerUtils.readAsDate(LoanConstants.DISBURSED_DATE_COL, row);
        LocalDate disbursedDate = DateUtils.parseLocalDate("31/12/2021" ,"dd/MM/YYYY");

        String linkAccountId = null;
        if ( ImportHandlerUtils.readAsLong(LoanConstants.LINK_ACCOUNT_ID, row)!=null){
            linkAccountId =  ImportHandlerUtils.readAsLong(LoanConstants.LINK_ACCOUNT_ID, row).toString();
        }

        if (disbursedDate!=null) {
            return DisbursementData.importInstance(disbursedDate,linkAccountId,row.getRowNum(),locale,dateFormat);
        }

        return null;
    }

    private LoanApprovalData readLoanApproval(Row row,String locale,String dateFormat) {
        
        //LocalDate approvedDate = ImportHandlerUtils.readAsDate(LoanConstants.APPROVED_DATE_COL, row);
        LocalDate approvedDate = DateUtils.parseLocalDate("31/12/2021","dd/MM/YYYY");
        
        if (approvedDate!=null)
            return LoanApprovalData.importInstance(approvedDate, row.getRowNum(),locale,dateFormat);
        else
            return null;
    }

    private LoanAccountData readLoan(Row row,String locale,String dateFormat) {

        // Added 04/10/2021
        String clientExternalId = ImportHandlerUtils.readAsString(LoanConstants.CLIENT_EXTERNAL_ID ,row);

        String externalId =  ImportHandlerUtils.readAsString(LoanConstants.EXTERNAL_ID_COL, row);
        String status =  ImportHandlerUtils.readAsString(LoanConstants.STATUS_COL, row);
        String productName =  ImportHandlerUtils.readAsString(LoanConstants.PRODUCT_COL, row);
        Long productId =  ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.PRODUCT_SHEET_NAME), productName);
        String loanOfficerName =  ImportHandlerUtils.readAsString(LoanConstants.LOAN_OFFICER_NAME_COL, row);
        Long loanOfficerId =  ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.STAFF_SHEET_NAME), loanOfficerName);
        
        LocalDate submittedOnDate =  ImportHandlerUtils.readAsDate(LoanConstants.SUBMITTED_ON_DATE_COL, row);
        String fundName =  Optional.ofNullable(ImportHandlerUtils.readAsString(LoanConstants.FUND_NAME_COL, row)).orElse(null);

        final Long[] fundId = {0L} ;

        Consumer<String> fundNameConsumer = (e)->{
            fundId[0] =  ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.EXTRAS_SHEET_NAME), fundName);
        };

        Optional.ofNullable(fundName).ifPresent(fundNameConsumer);

        BigDecimal principal = null;
        if ( ImportHandlerUtils.readAsDouble(LoanConstants.PRINCIPAL_COL, row) != null) {
            principal = BigDecimal.valueOf(ImportHandlerUtils.readAsDouble(LoanConstants.PRINCIPAL_COL, row));
        }

        Integer numberOfRepayments =  ImportHandlerUtils.readAsInt(LoanConstants.NO_OF_REPAYMENTS_COL, row);
        Integer repaidEvery =  ImportHandlerUtils.readAsInt(LoanConstants.REPAID_EVERY_COL, row);


        String repaidEveryFrequency =  ImportHandlerUtils.readAsString(LoanConstants.REPAID_EVERY_FREQUENCY_COL, row);
        String repaidEveryFrequencyId = "";
        EnumOptionData repaidEveryFrequencyEnums = null;
        if (repaidEveryFrequency != null) {
            if (repaidEveryFrequency.equalsIgnoreCase("Days"))
                repaidEveryFrequencyId = "0";
            else if (repaidEveryFrequency.equalsIgnoreCase("Weeks"))
                repaidEveryFrequencyId = "1";
            else if (repaidEveryFrequency.equalsIgnoreCase("Months")) repaidEveryFrequencyId = "2";
            repaidEveryFrequencyEnums = new EnumOptionData(null, null, repaidEveryFrequencyId);
        }

        Integer loanTerm =  ImportHandlerUtils.readAsInt(LoanConstants.LOAN_TERM_COL, row);
        
        String loanTermFrequency =  ImportHandlerUtils.readAsString(LoanConstants.LOAN_TERM_FREQUENCY_COL, row);
        EnumOptionData loanTermFrequencyEnum = null;
        if (loanTermFrequency != null) {

            String loanTermFrequencyId = "";
            if (loanTermFrequency.equalsIgnoreCase("Days"))
                loanTermFrequencyId = "0";
            else if (loanTermFrequency.equalsIgnoreCase("Weeks"))
                loanTermFrequencyId = "1";
            else if (loanTermFrequency.equalsIgnoreCase("Months"))
                loanTermFrequencyId = "2";
            loanTermFrequencyEnum = new EnumOptionData(null, null, loanTermFrequencyId);
        }

        // loan adjustments here 
        int newNumberOfRepayments = NkwaziLoanAdjustmentsHelper.adjustLoan("31/12/2021" ,submittedOnDate ,numberOfRepayments,loanTermFrequency);

        /// nkwazi adjustments to be removed 
        if(newNumberOfRepayments <= 0){
            return null;
        }

        submittedOnDate = DateUtils.parseLocalDate("31/12/2021" ,"dd/MM/YYYY");

        numberOfRepayments = newNumberOfRepayments;

        BigDecimal nominalInterestRate = null;
        if ( ImportHandlerUtils.readAsDouble(LoanConstants.NOMINAL_INTEREST_RATE_COL, row) != null)
            nominalInterestRate = BigDecimal.valueOf( ImportHandlerUtils.readAsDouble(LoanConstants.NOMINAL_INTEREST_RATE_COL, row));
        String amortization =  ImportHandlerUtils.readAsString(LoanConstants.AMORTIZATION_COL, row);
        String amortizationId = "";
        EnumOptionData amortizationEnumOption = null;
        if (amortization != null) {
            if (amortization.equalsIgnoreCase("Equal principal payments"))
                amortizationId = "0";
            else if (amortization.equalsIgnoreCase("Equal installments")) amortizationId = "1";
            amortizationEnumOption = new EnumOptionData(null, null, amortizationId);
        }
        String interestMethod =  ImportHandlerUtils.readAsString(LoanConstants.INTEREST_METHOD_COL, row);
        String interestMethodId = "";
        EnumOptionData interestMethodEnum = null;
        if (interestMethod != null) {
            if (interestMethod.equalsIgnoreCase("Flat"))
                interestMethodId = "1";
            else if (interestMethod.equalsIgnoreCase("Declining Balance")) interestMethodId = "0";
            interestMethodEnum = new EnumOptionData(null, null, interestMethodId);
        }
        String interestCalculationPeriod = ImportHandlerUtils.readAsString(LoanConstants.INTEREST_CALCULATION_PERIOD_COL, row);
        String interestCalculationPeriodId = "";
        EnumOptionData interestCalculationPeriodEnum = null;
        if (interestCalculationPeriod != null) {
            if (interestCalculationPeriod.equalsIgnoreCase("Daily"))
                interestCalculationPeriodId = "0";
            else if (interestCalculationPeriod.equalsIgnoreCase("Same as repayment period"))
                interestCalculationPeriodId = "1";
            interestCalculationPeriodEnum = new EnumOptionData(null, null, interestCalculationPeriodId);

        }

        BigDecimal arrearsTolerance = null;
        if ( ImportHandlerUtils.readAsDouble(LoanConstants.ARREARS_TOLERANCE_COL, row) != null)
            arrearsTolerance = BigDecimal.valueOf( ImportHandlerUtils.readAsDouble(LoanConstants.ARREARS_TOLERANCE_COL, row));
        String repaymentStrategy =  ImportHandlerUtils.readAsString(LoanConstants.REPAYMENT_STRATEGY_COL, row);
        Long repaymentStrategyId = null;
        if (repaymentStrategy!=null) {
            if (repaymentStrategy.equalsIgnoreCase("Penalties, Fees, Interest, Principal order"))
                repaymentStrategyId = 1L;
            else if (repaymentStrategy.equalsIgnoreCase("HeavensFamily Unique"))
                repaymentStrategyId = 2L;
            else if (repaymentStrategy.equalsIgnoreCase("Creocore Unique"))
                repaymentStrategyId = 3L;
            else if (repaymentStrategy.equalsIgnoreCase("Overdue/Due Fee/Int,Principal"))
                repaymentStrategyId = 4L;
            else if (repaymentStrategy.equalsIgnoreCase("Principal, Interest, Penalties, Fees Order"))
                repaymentStrategyId = 5L;
            else if (repaymentStrategy.equalsIgnoreCase("Interest, Principal, Penalties, Fees Order"))
                repaymentStrategyId = 6L;
            else if (repaymentStrategy.equalsIgnoreCase("Early Repayment Strategy"))
                repaymentStrategyId = 7L;
        }
        Integer graceOnPrincipalPayment =  ImportHandlerUtils.readAsInt(LoanConstants.GRACE_ON_PRINCIPAL_PAYMENT_COL, row);
        Integer graceOnInterestPayment =  ImportHandlerUtils.readAsInt(LoanConstants.GRACE_ON_INTEREST_PAYMENT_COL, row);
        Integer graceOnInterestCharged =  ImportHandlerUtils.readAsInt(LoanConstants.GRACE_ON_INTEREST_CHARGED_COL, row);
        
        // modified 18/08/2021 interestChargedFrom should always default to null so that interest wont be charged on day of importing 

        //LocalDate interestChargedFromDate =  ImportHandlerUtils.readAsDate(LoanConstants.INTEREST_CHARGED_FROM_COL, row);
        LocalDate interestChargedFromDate = null ;
        // if first repayment date not around we should just use the one in scheduling not today date
        //LocalDate firstRepaymentOnDate =  ImportHandlerUtils.readAsDate(LoanConstants.FIRST_REPAYMENT_COL, row);
        
        LocalDate firstRepaymentOnDate = null ;

        // here is an error again ,interest charged from should be null 
                
        String loanType=null;
        EnumOptionData loanTypeEnumOption=null;
        if ( ImportHandlerUtils.readAsString(LoanConstants.LOAN_TYPE_COL, row)!=null) {
            loanType =  ImportHandlerUtils.readAsString(LoanConstants.LOAN_TYPE_COL, row).toLowerCase(Locale.ENGLISH);

            loanTypeEnumOption = new EnumOptionData(null, null, loanType);
        }

        String clientOrGroupName = ImportHandlerUtils.readAsString(LoanConstants.CLIENT_NAME_COL, row);

        List<LoanChargeData> charges = new ArrayList<>();

        Long charge1 =  ImportHandlerUtils.readAsLong(LoanConstants.CHARGE_ID_1, row);
        Long charge2 =  ImportHandlerUtils.readAsLong(LoanConstants.CHARGE_ID_2, row);

        Long groupId =  ImportHandlerUtils.readAsLong(LoanConstants.GROUP_ID, row);

        String linkAccountId =  ImportHandlerUtils.readAsString(LoanConstants.LINK_ACCOUNT_ID, row);

        if (charge1!=null) {
            if ( ImportHandlerUtils.readAsDouble(LoanConstants.CHARGE_AMOUNT_1, row)!=null) {
                charges.add(new LoanChargeData( ImportHandlerUtils.readAsLong(LoanConstants.CHARGE_ID_1, row),
                        ImportHandlerUtils.readAsDate(LoanConstants.CHARGE_DUE_DATE_1, row),
                        BigDecimal.valueOf( ImportHandlerUtils.readAsDouble(LoanConstants.CHARGE_AMOUNT_1, row))));
            }else {
                charges.add(new LoanChargeData( ImportHandlerUtils.readAsLong(LoanConstants.CHARGE_ID_1, row),
                        ImportHandlerUtils.readAsDate(LoanConstants.CHARGE_DUE_DATE_1, row),null));
            }
        }

        if (charge2!=null) {
            if ( ImportHandlerUtils.readAsDouble(LoanConstants.CHARGE_AMOUNT_2, row)!=null){
            charges.add(new LoanChargeData( ImportHandlerUtils.readAsLong(LoanConstants.CHARGE_ID_2, row),
                    ImportHandlerUtils.readAsDate(LoanConstants.CHARGE_DUE_DATE_2, row),
                    BigDecimal.valueOf( ImportHandlerUtils.readAsDouble(LoanConstants.CHARGE_AMOUNT_2, row))));
            }else {
                charges.add(new LoanChargeData( ImportHandlerUtils.readAsLong(LoanConstants.CHARGE_ID_2, row),
                        ImportHandlerUtils.readAsDate(LoanConstants.CHARGE_DUE_DATE_2, row),
                       null));
            }
        }


        System.err.println("------------------------------loan added somewhere is error ----------------");

        /// bulk imports dont work for revolving accounts 
        /// added 21/08/2021  ,loanfactoring doesnt work at this stage maybe in the later versions it will work
        
        final  Long loanFactorAccountId = null ;
        statuses.add(status);

        if (loanType!=null) {
            if (loanType.equals("individual")) {

                // returns 0L if value of clientId is null ,so we must search using client external id
                // check if client exist first by using client external id . 
                Long clientId = ImportHandlerUtils.getIdByExternalId(clientReadPlatformService ,clientExternalId);

                Boolean notValidClientId = ComparatorUtility.isLongZero(clientId);

                // if client id is null again lets skip but will have problem in finding out which loans failed or succedded
                if(notValidClientId){           
                    clientId =  ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.CLIENT_SHEET_NAME), clientOrGroupName);
                }

                return LoanAccountData.importInstanceIndividual(loanTypeEnumOption, clientId, productId, loanOfficerId, submittedOnDate, fundId[0],
                        principal, numberOfRepayments,
                        repaidEvery, repaidEveryFrequencyEnums, loanTerm, loanTermFrequencyEnum, nominalInterestRate, submittedOnDate,
                        amortizationEnumOption, interestMethodEnum, interestCalculationPeriodEnum, arrearsTolerance, repaymentStrategyId,
                        graceOnPrincipalPayment, graceOnInterestPayment, graceOnInterestCharged, interestChargedFromDate, firstRepaymentOnDate,
                        row.getRowNum(), externalId, null, charges, linkAccountId,locale,dateFormat ,null ,false ,loanFactorAccountId);
            } else if (loanType.equals("jlg")) {
                Long clientId =  ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.CLIENT_SHEET_NAME), clientOrGroupName);
                return LoanAccountData.importInstanceIndividual(loanTypeEnumOption, clientId, productId, loanOfficerId, submittedOnDate, fundId[0],
                        principal, numberOfRepayments,
                        repaidEvery, repaidEveryFrequencyEnums, loanTerm, loanTermFrequencyEnum, nominalInterestRate, submittedOnDate,
                        amortizationEnumOption, interestMethodEnum, interestCalculationPeriodEnum, arrearsTolerance, repaymentStrategyId,
                        graceOnPrincipalPayment, graceOnInterestPayment, graceOnInterestCharged, interestChargedFromDate, firstRepaymentOnDate,
                        row.getRowNum(), externalId, groupId, charges, linkAccountId,locale,dateFormat ,null ,false ,loanFactorAccountId);
            } else {
                Long groupIdforGroupLoan =  ImportHandlerUtils.getIdByName(workbook.getSheet(TemplatePopulateImportConstants.GROUP_SHEET_NAME), clientOrGroupName);
                return LoanAccountData.importInstanceGroup(loanTypeEnumOption, groupIdforGroupLoan, productId, loanOfficerId, submittedOnDate, fundId[0],
                        principal, numberOfRepayments,
                        repaidEvery, repaidEveryFrequencyEnums, loanTerm, loanTermFrequencyEnum, nominalInterestRate,
                        amortizationEnumOption, interestMethodEnum, interestCalculationPeriodEnum, arrearsTolerance,
                        repaymentStrategyId, graceOnPrincipalPayment, graceOnInterestPayment, graceOnInterestCharged,
                        interestChargedFromDate, firstRepaymentOnDate, row.getRowNum(), externalId, linkAccountId,locale,dateFormat ,null ,false ,loanFactorAccountId);
            }
        }else {
            return null;
        }
    }

    public Count importEntity(String dateFormat) {
        Sheet loanSheet = workbook.getSheet(TemplatePopulateImportConstants.LOANS_SHEET_NAME);
        int successCount=0;
        int errorCount=0;
        int progressLevel = 0;
        String loanId;
        String errorMessage="";
        for (int i = 0; i < loans.size(); i++) {
            Row row = loanSheet.getRow(loans.get(i).getRowIndex());
            Cell errorReportCell = row.createCell(LoanConstants.FAILURE_REPORT_COL);
            Cell statusCell = row.createCell(LoanConstants.STATUS_COL);
            CommandProcessingResult result=null;
            loanId="";
            try {
                String status = statuses.get(i);;
                progressLevel = getProgressLevel(status);

                if (progressLevel == 0&& loans.get(i)!=null) {
                    result = importLoan(i,dateFormat);
                    loanId = result.getLoanId().toString();
                    progressLevel = 1;
                } else
                    loanId = ImportHandlerUtils.readAsString(LoanConstants.LOAN_ID_COL, loanSheet.getRow(loans.get(i).getRowIndex()));

                if (progressLevel <= 1 && approvalDates.get(i)!=null) progressLevel = importLoanApproval(result, i,dateFormat);

                if (progressLevel <= 2 && disbursalDates.get(i)!=null) progressLevel = importDisbursalData(result, i,dateFormat);

                if (loanRepayments.get(i) != null) progressLevel = importLoanRepayment(result, i,dateFormat);

                successCount++;
                statusCell.setCellValue(TemplatePopulateImportConstants.STATUS_CELL_IMPORTED);
                statusCell.setCellStyle(ImportHandlerUtils.getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            }catch (RuntimeException ex){

                System.err.println("-------------------------------------failed processing loan ----------------");
                errorCount++;
                ex.printStackTrace();
                errorMessage=ImportHandlerUtils.getErrorMessage(ex);
                writeLoanErrorMessage(loanId,errorMessage,progressLevel,statusCell,errorReportCell,row);
            }

        }
        setReportHeaders(loanSheet);
        return Count.instance(successCount,errorCount);
    }

    private void writeLoanErrorMessage(String loanId,String errorMessage,int progressLevel,Cell statusCell,Cell errorReportCell,Row row){
        String status = "";
        if (progressLevel == 0)
            status = TemplatePopulateImportConstants.STATUS_CREATION_FAILED;
        else if (progressLevel == 1)
            status = TemplatePopulateImportConstants.STATUS_APPROVAL_FAILED;
        else if (progressLevel == 2)
            status = TemplatePopulateImportConstants.STATUS_DISBURSAL_FAILED;
        else if (progressLevel == 3) status = TemplatePopulateImportConstants.STATUS_DISBURSAL_REPAYMENT_FAILED;
        statusCell.setCellValue(status);
        statusCell.setCellStyle(ImportHandlerUtils.getCellStyle(workbook, IndexedColors.RED));

        if (progressLevel > 0) row.createCell(LoanConstants.LOAN_ID_COL).setCellValue(Integer.parseInt(loanId));
        errorReportCell.setCellValue(errorMessage);
    }
    private void setReportHeaders(Sheet sheet) {
        sheet.setColumnWidth(LoanConstants.STATUS_COL, TemplatePopulateImportConstants.SMALL_COL_SIZE);
        Row rowHeader = sheet.getRow(TemplatePopulateImportConstants.ROWHEADER_INDEX);
        ImportHandlerUtils.writeString(LoanConstants.STATUS_COL, rowHeader, "Status");
        ImportHandlerUtils.writeString(LoanConstants.LOAN_ID_COL, rowHeader, "Loan ID");
        ImportHandlerUtils.writeString(LoanConstants.FAILURE_REPORT_COL, rowHeader, "Report");
    }

    private Integer importLoanRepayment(CommandProcessingResult result, int rowIndex,String dateFormat) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));
        JsonObject loanRepaymentJsonob=gsonBuilder.create().toJsonTree(loanRepayments.get(rowIndex)).getAsJsonObject();
        loanRepaymentJsonob.remove("manuallyReversed");
        String payload=loanRepaymentJsonob.toString();
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .loanRepaymentTransaction(result.getLoanId()) //
                .withJson(payload) //
                .build(); //
        final CommandProcessingResult loanRepaymentResult = commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return 4;
    }

    private Integer importDisbursalData(CommandProcessingResult result, int rowIndex, String dateFormat) {
        if (approvalDates.get(rowIndex) != null && disbursalDates.get(rowIndex) != null) {

            DisbursementData disbusalData = disbursalDates.get(rowIndex);
            String linkAccountId = disbusalData.getLinkAccountId();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));
            if (linkAccountId != null && linkAccountId != "") {
                String payload =gsonBuilder.create().toJson(disbusalData);
                final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                        .disburseLoanToSavingsApplication(result.getLoanId()) //
                        .withJson(payload) //
                        .build(); //
                final CommandProcessingResult loanDisburseToSavingsResult = commandsSourceWritePlatformService.logCommandSource(commandRequest);
            } else {
                String payload = gsonBuilder.create().toJson(disbusalData);

                //System.err.println("-----------------------diburse loan to not savings ----------------"+payload);
                final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                        .disburseLoanApplication(result.getLoanId()) //
                        .withJson(payload) //
                        .build(); //
                final CommandProcessingResult loanDisburseResult = commandsSourceWritePlatformService.logCommandSource(commandRequest);
            }
        }
        return 3;
    }

    private Integer importLoanApproval(CommandProcessingResult result, int rowIndex,String dateFormat) {
        if (approvalDates.get(rowIndex) != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));
            String payload = gsonBuilder.create().toJson(approvalDates.get(rowIndex));
            final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                    .approveLoanApplication(result.getLoanId()) //
                    .withJson(payload) //
                    .build(); //
            final CommandProcessingResult loanapprovalresult = commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }
        return 2;
    }

    private CommandProcessingResult importLoan(int rowIndex,String dateFormat) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));
        gsonBuilder.registerTypeAdapter(EnumOptionData.class,new EnumOptionDataValueSerializer());
        JsonObject loanJsonOb  = gsonBuilder.create().toJsonTree(loans.get(rowIndex)).getAsJsonObject();
        loanJsonOb.remove("isLoanProductLinkedToFloatingRate");
        loanJsonOb.remove("isInterestRecalculationEnabled");
        loanJsonOb.remove("isFloatingInterestRate");
        JsonArray chargesJsonAr=loanJsonOb.getAsJsonArray("charges");
        if (chargesJsonAr!=null) {
            for (int i = 0; i < chargesJsonAr.size(); i++) {
                JsonElement chargesJsonElement = chargesJsonAr.get(i);
                JsonObject chargeJsonOb = chargesJsonElement.getAsJsonObject();
                chargeJsonOb.remove("penalty");
                chargeJsonOb.remove("paid");
                chargeJsonOb.remove("waived");
                chargeJsonOb.remove("chargePayable");
            }
        }
        loanJsonOb.remove("isTopup");
        String payload=loanJsonOb.toString();

        System.err.println("----------------loan payload ------------------"+payload);
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createLoanApplication() //
                .withJson(payload) //
                .build(); //
        final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return result;
    }

    private int getProgressLevel(String status) {
        if (status==null || status.equals(TemplatePopulateImportConstants.STATUS_CREATION_FAILED))
            return 0;
        else if (status.equals(TemplatePopulateImportConstants.STATUS_APPROVAL_FAILED))
            return 1;
        else if (status.equals(TemplatePopulateImportConstants.STATUS_DISBURSAL_FAILED))
            return 2;
        else if (status.equals(TemplatePopulateImportConstants.STATUS_DISBURSAL_REPAYMENT_FAILED)) return 3;
        return 0;
    }


}
