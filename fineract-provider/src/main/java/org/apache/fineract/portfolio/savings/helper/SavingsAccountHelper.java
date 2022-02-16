/*

    Created by Sinatra Gunda
    At 9:32 PM on 12/16/2021

*/
package org.apache.fineract.portfolio.savings.helper;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;

import org.joda.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;


public class SavingsAccountHelper {

    public static CommandProcessingResult openSavingsAccount(SavingsAccountReadPlatformService savingsAccountReadPlatformService, ClientWritePlatformService clientWritePlatformService, ClientRepositoryWrapper clientRepositoryWrapper ,final DateTimeFormatter dateTimeFormatter ,final Long savingsProductId ,final Long officeId){

        CommandProcessingResult commandProcessingResult[] = {null};

        List<Client> clientsCollection = clientRepositoryWrapper.findAllByOfficeId(officeId);

        clientsCollection.stream().forEach(client->{

            Long clientId = client.getId();

            client.updateSavingsProduct(savingsProductId);
            
            client.updateSavingsAccount(null);

            boolean isEmpty = savingsAccountReadPlatformService.retrieveAllForClientUnderPortfolio(clientId ,savingsProductId).isEmpty();

            if(isEmpty){
                commandProcessingResult[0] = clientWritePlatformService.openSavingsAccountEx(client ,dateTimeFormatter,savingsProductId);
            }
        });

        return commandProcessingResult[0] ;

    }
}
