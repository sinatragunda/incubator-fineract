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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SavingsAccountToClientLinkingHelper {

    // single entity client importation ,assumes client has only one savings account and deposit whatever that account is
    public static SavingsAccountData linkBlindlySavingsAccount(ClientReadPlatformService clientReadPlatformService , SavingsAccountReadPlatformService savingsAccountReadPlatformService, String externalId){

        SavingsAccountData[] savingsAccountData = {null};

        Optional.ofNullable(externalId).ifPresent(e->{

            ClientData clientData = clientReadPlatformService.retrieveOneByExternalId(externalId);

            Optional.ofNullable(clientData).ifPresent(c->{

                Long clientId = c.getId();
                Collection<SavingsAccountData> clientAccounts = savingsAccountReadPlatformService.retrieveAllForLookup(clientId);

                if(!clientAccounts.isEmpty()){
                    savingsAccountData[0] = clientAccounts.get(0);
                }
            });
        });

        return savingsAccountData[0];
    }
}
