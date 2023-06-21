/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 June 2023 at 09:13
 */
package org.apache.fineract.utility.service;

import org.apache.fineract.infrastructure.dataqueries.service.EntityDatatableChecksReadService;


import org.apache.fineract.infrastructure.dataqueries.service.EntityDatatableChecksReadService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.products.service.ProductReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

@Service
public class InfrastructureServiceWrapper {

    private EntityDatatableChecksReadService entityDatatableChecksReadService;

    @Autowired
    public InfrastructureServiceWrapper(@Lazy EntityDatatableChecksReadService entityDatatableChecksReadService) {
        this.entityDatatableChecksReadService = entityDatatableChecksReadService;
    }

    public EntityDatatableChecksReadService getEntityDatatableChecksReadService() {
        return entityDatatableChecksReadService;
    }
}
