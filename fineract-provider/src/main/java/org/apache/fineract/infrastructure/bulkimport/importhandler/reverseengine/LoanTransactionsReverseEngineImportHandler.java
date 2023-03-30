/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 March 2023 at 11:46
 */
package org.apache.fineract.infrastructure.bulkimport.importhandler.reverseengine;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.bulkimport.constants.LoanRepaymentConstants;
import org.apache.fineract.infrastructure.bulkimport.constants.TemplatePopulateImportConstants;
import org.apache.fineract.infrastructure.bulkimport.data.Count;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandler;
import org.apache.fineract.infrastructure.bulkimport.importhandler.ImportHandlerUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.DateSerializer;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.*;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.repo.LoanTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanTransactionReadPlatformService;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanTransactionsReverseEngineImportHandler implements ImportHandler {
    
    private Workbook workbook;
    private List<LoanTransactionData> loanTransactions;
    private final LoanAccountDomainService loanAccountDomainService;
    private final LoanTransactionReadPlatformService loanTransactionReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanTransactionRepositoryWrapper loanTransactionRepositoryWrapper;
    private final LoanReadPlatformService loanReadPlatformService;
    @Autowired
    public LoanTransactionsReverseEngineImportHandler(final PortfolioCommandSourceWritePlatformService
                                              commandsSourceWritePlatformService ,LoanTransactionReadPlatformService loanTransactionReadPlatformService ,LoanAccountDomainService loanAccountDomainService ,final  LoanTransactionRepositoryWrapper loanTransactionRepositoryWrapper ,final LoanReadPlatformService loanReadPlatformService) {
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.loanAccountDomainService = loanAccountDomainService;
        this.loanTransactionReadPlatformService = loanTransactionReadPlatformService;
        this.loanTransactionRepositoryWrapper = loanTransactionRepositoryWrapper;
        this.loanReadPlatformService = loanReadPlatformService;
    }

    @Override
    public Count process(Workbook workbook, String locale, String dateFormat) {
        this.workbook = workbook;
        this.loanTransactions = new ArrayList<>();
        readExcelFile(locale,dateFormat);
        return reverse(dateFormat);
    }

    public void readExcelFile(String locale, String dateFormat) {
        
        Sheet loanTransactionsSheet = workbook.getSheetAt(0);
        
        Integer noOfEntries = ImportHandlerUtils.getNumberOfRows(loanTransactionsSheet,TemplatePopulateImportConstants.ID_NAME_COL);

        System.err.println("--------------number of entries are ==============="+noOfEntries);

        for (int rowIndex = 1; rowIndex <= noOfEntries; rowIndex++) {
            Row row = loanTransactionsSheet.getRow(rowIndex);
            LoanTransactionData loanTransactionData = readLoanTransaction(row ,locale ,dateFormat);
            boolean has = OptionalHelper.isPresent(loanTransactionData);
            if(has){
                System.err.println("====================has entry transaction "+rowIndex+"=======other is "+row.getRowNum());
                loanTransactionData.setRowIndex(rowIndex);
                loanTransactions.add(loanTransactionData);
            }
        }
    }
    private LoanTransactionData readLoanTransaction(Row row,String locale, String dateFormat) {
        
        Long transactionId = ImportHandlerUtils.readAsLong(TemplatePopulateImportConstants.ID_NAME_COL ,row);
        boolean has = OptionalHelper.isPresent(transactionId);
        if(!has){
            return null ;
        }
        System.err.println("==============percieved transaction id is "+transactionId+"-------has value ----"+has);
        LoanTransactionData loanTransactionData = loanTransactionReadPlatformService.retrieveOne(loanReadPlatformService,transactionId);
        return loanTransactionData;
    }


    public Count reverse(String dateFormat){

        Sheet loanTransactionsSheet = workbook.getSheetAt(0);
        int successCount=0;
        int errorCount=0;
        String errorMessage="";
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new DateSerializer(dateFormat));

        for (LoanTransactionData loanTransactionData : loanTransactions) {
            try {

                System.err.println("==================================reversing transactions now "+loanTransactionData.getId());
                Long transactionId = loanTransactionData.getId();
                LoanTransaction loanTransaction = loanTransactionRepositoryWrapper.findOneWithNotFoundDetection(transactionId) ;
                loanAccountDomainService.reverseTransfer(loanTransaction);
                final CommandProcessingResult result = new CommandProcessingResultBuilder().withEntityId(loanTransaction.getId()).build();
                successCount++;
                Cell statusCell = loanTransactionsSheet.getRow(loanTransactionData.getRowIndex()).createCell(LoanRepaymentConstants.STATUS_COL);
                statusCell.setCellValue(TemplatePopulateImportConstants.STATUS_CELL_REVERSED);
                statusCell.setCellStyle(ImportHandlerUtils.getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            }catch (RuntimeException ex){
                errorCount++;
                ex.printStackTrace();
                errorMessage=ImportHandlerUtils.getErrorMessage(ex);
                ImportHandlerUtils.writeErrorMessage(loanTransactionsSheet,loanTransactionData.getRowIndex(),errorMessage,LoanRepaymentConstants.STATUS_COL);
            }
        }

        loanTransactionsSheet.setColumnWidth(LoanRepaymentConstants.STATUS_COL, TemplatePopulateImportConstants.SMALL_COL_SIZE);
        ImportHandlerUtils.writeString(LoanRepaymentConstants.STATUS_COL,
                loanTransactionsSheet.getRow(TemplatePopulateImportConstants.ROWHEADER_INDEX),
                TemplatePopulateImportConstants.STATUS_COL_REPORT_HEADER);
        return Count.instance(successCount,errorCount);
    }
}

