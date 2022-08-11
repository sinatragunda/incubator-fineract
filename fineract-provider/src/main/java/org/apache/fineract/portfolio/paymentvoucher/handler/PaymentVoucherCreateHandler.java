/*

    Created by Sinatra Gunda
    At 12:05 PM on 8/10/2022

*/
package org.apache.fineract.portfolio.paymentvoucher.handler;


import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.paymentvoucher.service.PaymentVoucherWritePlatformService;
import org.apache.fineract.portfolio.savings.service.DepositAccountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "PAYMENTVOUCHER", action = "CREATE")
public class PaymentVoucherCreateHandler implements NewCommandSourceHandler {

    private PaymentVoucherWritePlatformService paymentVoucherWritePlatformService;


    @Autowired
    public PaymentVoucherCreateHandler(final PaymentVoucherWritePlatformService paymentVoucherWritePlatformService) {
        this.paymentVoucherWritePlatformService = paymentVoucherWritePlatformService ;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.paymentVoucherWritePlatformService.createEntriesForVoucher(command);
    }
}