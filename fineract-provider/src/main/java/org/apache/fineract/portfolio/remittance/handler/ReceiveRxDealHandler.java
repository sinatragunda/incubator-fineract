/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 07 November 2022 at 13:52
 */
package org.apache.fineract.portfolio.remittance.handler;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.remittance.service.RxDealWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "RXDEAL", action = "RECEIVE")
public class ReceiveRxDealHandler implements NewCommandSourceHandler {

    private final RxDealWritePlatformService rxDealWritePlatformService ;

    @Autowired
    public ReceiveRxDealHandler(final RxDealWritePlatformService rxDealWritePlatformService) {
        this.rxDealWritePlatformService = rxDealWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.rxDealWritePlatformService.receiveRxDeal(command.entityId(),command);
    }
}

