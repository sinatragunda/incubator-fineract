/*

    Created by Sinatra Gunda
    At 12:56 PM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.handler;


import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.commissions.service.CommissionChargeWritePlatformService;
import org.apache.fineract.portfolio.commissions.service.LoanAgentWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "COMMISSIONCHARGE", action = "CREATE")
public class CreateCommissionChargeCommandHandler implements NewCommandSourceHandler {

    private final CommissionChargeWritePlatformService commissionChargeWritePlatformService;

    @Autowired
    public CreateCommissionChargeCommandHandler(CommissionChargeWritePlatformService commissionChargeWritePlatformService) {
        this.commissionChargeWritePlatformService = commissionChargeWritePlatformService;
    }



    @Override
    public CommandProcessingResult processCommand(JsonCommand jsonCommand) {
        return this.commissionChargeWritePlatformService.create(jsonCommand);
    }

}
