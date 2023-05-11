/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 May 2023 at 08:10
 */
package org.apache.fineract.presentation.screen.service;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.utility.service.ServiceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VersionPortfolioCommandWritePlatformServiceImpl implements VersionPortfolioCommandWritePlatformService{

    private ServiceWrapper serviceWrapper;
    private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;

    @Autowired
    public VersionPortfolioCommandWritePlatformServiceImpl(PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService) {
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
    }

    public CommandProcessingResult create(String payload , REF_TABLE refTable){

        CommandWrapperBuilder builder = new CommandWrapperBuilder();

        switch (refTable){
            case CLIENT:
                builder = builder.createClient();
                break;
        }

        CommandWrapper commandWrapper = builder.withJson(payload).build();
        CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandWrapper);
        return result;
    }
}
