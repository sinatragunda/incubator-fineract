/*

    Created by Sinatra Gunda
    At 12:21 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.client.helper;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.commissions.exceptions.LoanAgentRequireSavingsAccountException;
import org.apache.fineract.wese.helper.ComparatorUtility;
import org.apache.fineract.wese.helper.JsonHelper;


import java.util.HashMap;
import java.util.Map ;
import java.util.Optional;

public class ClientCreateHelper {

    private static String AGENT = "AGENT";

    // modified 26/01/2022 to add optional nullable cause clientType wont be defined in other instances and would throw null pointer exception
    public static void createLoanAgent(PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService , Client client){

        Optional.ofNullable(client.clientType()).ifPresent(e->{

            CodeValue clientType = e;

            String label  = Optional.ofNullable(clientType.label()).orElse("Normal");

            boolean isLoanAgent = ComparatorUtility.compareStringsIgnoreCase(label ,AGENT);

            if(isLoanAgent){

                Long clientId  = client.getId();
                boolean hasSavingsAccount = Optional.ofNullable(client.savingsAccountId()).isPresent();

                if(!hasSavingsAccount){
                    throw new LoanAgentRequireSavingsAccountException(null);
                }

                Map<String,Object> map = new HashMap<>();
                map.put("clientId",clientId);
                map.put("savingsAccountId",client.savingsAccountId());

                String jsonRequest = JsonHelper.serializeMapToJson(map);
                final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanAgent().withJson(jsonRequest).build();
                final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
                Long id = result.resourceId();
            }
        });
    }
}
