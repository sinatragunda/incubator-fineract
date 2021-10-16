/*

    Created by Sinatra Gunda
    At 2:51 PM on 10/15/2021

*/
package org.apache.fineract.infrastructure.bulkimport.importhandler.helper;

import org.apache.fineract.portfolio.accounts.data.AccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountData;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccountRepositoryWrapper;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountReadPlatformService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SharesAccountImportHelper {

    public static Integer getTotalNumberOfShares(Double amount ,Double unitPrice){

        boolean isUnitPrice = Optional.ofNullable(unitPrice).isPresent();
        boolean isAmount = Optional.ofNullable(amount).isPresent();

        Double value = 0.0 ;

        if(isUnitPrice || isAmount){
            value = amount / unitPrice ;
        }

        return value.intValue();
    }

    public static ShareAccountData getClientShareAccount(ShareAccountReadPlatformService shareAccountReadPlatformService, Long productId , Long clientId){

        ShareAccountData shareAccountData[] = {null} ;

        Optional.ofNullable(clientId).ifPresent(e->{

            List<AccountData> shareAccountList = shareAccountReadPlatformService.retrieveForClient(clientId);

            Optional<AccountData> optional = shareAccountList.stream().filter(e1->{
                ShareAccountData shareAccountData1 = (ShareAccountData)e1;
                return shareAccountData1.getProductId().equals(productId);
            }).findFirst();

            optional.ifPresent(e2->{
                shareAccountData[0] = (ShareAccountData) e2 ;
            });
        });
        return shareAccountData[0];
    }

    public static SavingsAccountData getLinkedAccount(SavingsAccountReadPlatformService savingsAccountReadPlatformService , Long clientId , Long productId){

        SavingsAccountData savingsAccount[] = {null} ;
        Optional.ofNullable(clientId).ifPresent(e->{

            Collection<SavingsAccountData> savingsAccountDataList = savingsAccountReadPlatformService.retrieveAllForLookup(clientId);

            Optional<SavingsAccountData> savingsAccountDataOptional = savingsAccountDataList.stream().filter(e1->{
                return e1.getSavingsProductId().equals(productId);
            }).findFirst();

            savingsAccountDataOptional.ifPresent(e1->{
                savingsAccount[0] = e1;
            });

        });

        return savingsAccount[0];
    }
}
