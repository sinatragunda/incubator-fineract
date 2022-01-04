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
import org.apache.fineract.wese.helper.ComparatorUtility;
import org.apache.fineract.wese.helper.JsonHelper;


import java.util.HashMap;
import java.util.Map ;

public class ClientCreateHelper {

    private static String AGENT = "AGENT";

    public static void createLoanAgent(PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService , Client client){

        CodeValue clientType = client.clientType();

        String label  = clientType.label();
        boolean isLoanAgent = ComparatorUtility.compareStringsIgnoreCase(label ,AGENT);

        if(!isLoanAgent){
            return ;
        }

        Long clientId  = client.getId();
        Map<String,Object> map = new HashMap<>();
        map.put("clientId",clientId);

        String jsonRequest = JsonHelper.serializeMapToJson(map);
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanAgent().withJson(jsonRequest).build();

        final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);

        Long id = result.resourceId();

        /// now lets create some cool super stuff
    }
}
