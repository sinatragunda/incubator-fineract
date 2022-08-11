/*

    Created by Sinatra Gunda
    At 10:56 AM on 8/10/2022

*/
package org.apache.fineract.portfolio.paymentvoucher.api;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.portfolio.paymentvoucher.domain.PaymentVoucher;

@Path("/paymentvoucher")
@Component
@Scope("singleton")
public class PaymentVoucherApi {

    private ToApiJsonSerializer<PaymentVoucher> toApiJsonSerializer ;
    private PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService ;

    @Autowired
    public PaymentVoucherApi(ToApiJsonSerializer<PaymentVoucher> toApiJsonSerializer, PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandSourceWritePlatformService = commandSourceWritePlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String generatePaymentVoucher(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createPaymentVoucherEntry().withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}
