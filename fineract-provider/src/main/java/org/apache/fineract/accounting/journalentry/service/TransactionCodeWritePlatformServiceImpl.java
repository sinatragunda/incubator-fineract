/*

    Created by Sinatra Gunda
    At 12:28 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.constants.TransactionCodeConstants;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.glaccount.domain.GLAccountRepositoryWrapper;
import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.apache.fineract.accounting.journalentry.repo.TransactionCodeRepository;
import org.apache.fineract.accounting.journalentry.repo.TransactionCodeRepositoryWrapper;
import org.apache.fineract.infrastructure.bulkimport.constants.TransactionConstants;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;


@Service
public class TransactionCodeWritePlatformServiceImpl implements TransactionCodeWritePlatformService {


    private final PlatformSecurityContext context ;
    private final GLAccountRepositoryWrapper glAccountRepositoryWrapper;
    private final TransactionCodeRepositoryWrapper transactionCodeRepositoryWrapper;

    @Autowired
    public TransactionCodeWritePlatformServiceImpl(PlatformSecurityContext context, GLAccountRepositoryWrapper glAccountRepositoryWrapper, TransactionCodeRepositoryWrapper transactionCodeRepositoryWrapper) {
        this.context = context;
        this.glAccountRepositoryWrapper = glAccountRepositoryWrapper;
        this.transactionCodeRepositoryWrapper = transactionCodeRepositoryWrapper ;
    }

    /**
     * To add validation classes
     * @param jsonCommand
     * @return
     */
    @Override
    public CommandProcessingResult create(JsonCommand jsonCommand){


        /**
         * Todo : To add code for validating parameters from creation code to avoid nulls etc
         */

        Long code = jsonCommand.longValueOfParameterNamed(TransactionCodeConstants.codeParam);
        String name = jsonCommand.stringValueOfParameterNamed(TransactionCodeConstants.nameParam);
        Long debitAccountId = jsonCommand.longValueOfParameterNamed(TransactionCodeConstants.debitAccountParam);
        Long creditAccountId = jsonCommand.longValueOfParameterNamed(TransactionCodeConstants.creditAccountParam);

        GLAccount debitAcccount = glAccountRepositoryWrapper.findOneWithNotFoundDetection(debitAccountId);
        GLAccount creditAccount = glAccountRepositoryWrapper.findOneWithNotFoundDetection(creditAccountId);

        TransactionCode transactionCode = new TransactionCode(code ,name ,debitAcccount ,creditAccount);

        Long id = transactionCodeRepositoryWrapper.save(transactionCode);

        CommandProcessingResult commandProcessingResult = new CommandProcessingResultBuilder().
                withCommandId(jsonCommand.commandId()).
                withEntityId(id).
                withResourceIdAsString(id.toString()).
                build();

        return commandProcessingResult;
    }

    public CommandProcessingResult update(Long transactionCodeId ,JsonCommand command){

        TransactionCode transactionCode = transactionCodeRepositoryWrapper.findOneWithNotFoundDetection(transactionCodeId);

        System.err.println("----------------update transaction code now -------------------");

        Map changes = transactionCode.update(command ,glAccountRepositoryWrapper);

        System.err.println("-------------------changes are "+changes);

        this.transactionCodeRepositoryWrapper.save(transactionCode);

        CommandProcessingResult result = new CommandProcessingResultBuilder().withCommandId(transactionCodeId).with(changes).build();

        return result;

    }
}
