/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 February 2023 at 10:17
 */
package org.apache.fineract.infrastructure.dataqueries.service;


import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
public class ServiceAdapter {

    private LoanReadPlatformService loanReadPlatformService;
    private ClientReadPlatformService clientReadPlatformService;


    @Autowired
    public ServiceAdapter(LoanReadPlatformService loanReadPlatformService, ClientReadPlatformService clientReadPlatformService) {
        this.loanReadPlatformService = loanReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
    }

    public ClientReadPlatformService getClientReadPlatformService() {
        return clientReadPlatformService;
    }

    public LoanReadPlatformService getLoanReadPlatformService() {
        return loanReadPlatformService;
    }
}
