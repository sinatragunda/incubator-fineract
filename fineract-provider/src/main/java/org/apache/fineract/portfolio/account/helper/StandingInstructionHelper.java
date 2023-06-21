/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 24 April 2023 at 10:44
 */
package org.apache.fineract.portfolio.account.helper;

import org.apache.fineract.portfolio.account.data.StandingInstructionData;
import org.apache.fineract.portfolio.account.domain.AccountTransferStandingInstruction;
import org.apache.fineract.portfolio.account.repo.StandingInstructionRepositoryWrapper;
import org.apache.fineract.portfolio.account.service.StandingInstructionReadPlatformService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class StandingInstructionHelper {

    /**
     * Added 24/04/2023 at 1047
     * This is a hack function that arose from the problem with exceptions when joining tables in standing instruction
     * So this will use jdbc to get standing instructions of loan then get one by one its entity object
     */
    public static Collection<AccountTransferStandingInstruction> getStandingInstructionsByLoanAndStatus(StandingInstructionReadPlatformService standingInstructionReadPlatformService , StandingInstructionRepositoryWrapper standingInstructionRepositoryWrapper ,Long loanId ,Integer status){

        Collection<StandingInstructionData> standingInstructionDataCollection = standingInstructionReadPlatformService.findByLoanAndStatus(loanId ,status);

        Collection<AccountTransferStandingInstruction> accountTransferStandingInstructions = new ArrayList<>();

        Consumer<StandingInstructionData> convertToEntity = (e)->{
            Long id = e.getId();
            AccountTransferStandingInstruction accountTransferStandingInstruction = standingInstructionRepositoryWrapper.findOneWithNotFoundDetection(id);
            accountTransferStandingInstructions.add(accountTransferStandingInstruction);
        };

        System.err.println("-----------standing instructions are ------------"+standingInstructionDataCollection.size());

        for(StandingInstructionData e : standingInstructionDataCollection){

            Long id = e.getId();
            AccountTransferStandingInstruction accountTransferStandingInstruction = standingInstructionRepositoryWrapper.findOneWithNotFoundDetection(id);
            //accountTransferStandingInstructions.add(accountTransferStandingInstruction);
        }

        //standingInstructionDataCollection.stream().forEach(convertToEntity);

        return accountTransferStandingInstructions;
    }
}
