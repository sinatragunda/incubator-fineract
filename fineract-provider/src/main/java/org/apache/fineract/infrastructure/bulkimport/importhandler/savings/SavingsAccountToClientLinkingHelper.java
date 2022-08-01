/*

    Created by Sinatra Gunda
    At 4:20 PM on 10/5/2021

*/
package org.apache.fineract.infrastructure.bulkimport.importhandler.savings;

import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SavingsAccountToClientLinkingHelper {

    // single entity client importation ,assumes client has only one savings account and deposit whatever that account is
    // function no longer works need to accomodate new functionality 
    public static SavingsAccountData linkBlindlySavingsAccount(ClientReadPlatformService clientReadPlatformService , SavingsAccountReadPlatformService savingsAccountReadPlatformService, String externalId){

        SavingsAccountData[] savingsAccountData = {null};

        Optional.ofNullable(externalId).ifPresent(e->{

            ClientData clientData = clientReadPlatformService.retrieveOneByExternalId(externalId);

            Optional.ofNullable(clientData).ifPresent(c->{

                Long clientId = c.getId();
                Collection<SavingsAccountData> clientAccounts = savingsAccountReadPlatformService.retrieveAllForLookup(clientId);

                List<SavingsAccountData> savingsAccountDataList = new ArrayList<>(clientAccounts);
                if(!savingsAccountDataList.isEmpty()){
                    savingsAccountData[0] = savingsAccountDataList.stream().findFirst().orElse(null);
                }
            });
        });

        return savingsAccountData[0];
    }


    // added 22/07/2022 
    // need to accomodate new functionality now clients have many account rather than the single 1
    public static SavingsAccountData linkBlindlySavingsAccountEx(ClientReadPlatformService clientReadPlatformService , SavingsAccountReadPlatformService savingsAccountReadPlatformService,  String externalId ,Long productId){

        SavingsAccountData[] savingsAccountData = {null};

        Optional.ofNullable(externalId).ifPresent(e->{

            ClientData clientData = clientReadPlatformService.retrieveOneByExternalId(externalId);

            Optional.ofNullable(clientData).ifPresent(c->{

                Long clientId = c.getId();

                Collection<SavingsAccountData> clientAccounts = savingsAccountReadPlatformService.retrieveAllForClientUnderPortfolio(clientId ,productId);

                List<SavingsAccountData> savingsAccountDataList = new ArrayList<>(clientAccounts);
                
                savingsAccountData[0] = savingsAccountDataList.stream().findFirst().orElse(null);
            });
        });

        return savingsAccountData[0];
    }
}
