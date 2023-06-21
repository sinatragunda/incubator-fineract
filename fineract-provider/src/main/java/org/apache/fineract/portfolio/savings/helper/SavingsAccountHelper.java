/*

    Created by Sinatra Gunda
    At 9:32 PM on 12/16/2021

*/
package org.apache.fineract.portfolio.savings.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountConstant;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;

import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


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

    public static void closeSavingsAccounts(Client client , SavingsAccountReadPlatformService savingsAccountReadPlatformService , SavingsAccountWritePlatformService savingsAccountWritePlatformService,LocalDate transactionDate){

        Long clientId = client.getId();
        Collection<SavingsAccountData> savingsAccountDataList = savingsAccountReadPlatformService.retrieveAllForLookup(clientId);

        Consumer<SavingsAccountData> closeAccount = (e)->{
            Long id = e.id();

            Map<String ,Object> map = new HashMap<>();
            map.put("closedOnDate" ,transactionDate);
            map.put("locale" ,"en");
            map.put("dateFormat" ,"yyyy-MM-dd");
            map.put(SavingsAccountConstant.withdrawBalanceParamName ,true);
            map.put(SavingsAccountConstant.postInterestValidationOnClosure ,false);
            JsonCommand command = JsonCommandHelper.jsonCommand(new FromJsonHelper() ,map);

            System.err.println("----------command is "+command);

            savingsAccountWritePlatformService.close(id ,command);
        };

        savingsAccountDataList.stream().forEach(closeAccount);
    }
}
