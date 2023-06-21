/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 April 2023 at 01:29
 */
package org.apache.fineract.utility.service;


import org.apache.fineract.infrastructure.dataqueries.service.EntityDatatableChecksReadService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.products.service.ProductReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceWrapper {

    private LoanReadPlatformService loanReadPlatformService ;
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private ShareAccountReadPlatformService shareAccountReadPlatformService ;
    private LoanProductReadPlatformService loanProductReadPlatformService;
    private SavingsProductReadPlatformService savingsProductReadPlatformService;
    private ProductReadPlatformService productReadPlatformService;

    @Autowired
    public ServiceWrapper(LoanReadPlatformService loanReadPlatformService, SavingsAccountReadPlatformService savingsAccountReadPlatformService, ShareAccountReadPlatformService shareAccountReadPlatformService, LoanProductReadPlatformService loanProductReadPlatformService, SavingsProductReadPlatformService savingsProductReadPlatformService, ProductReadPlatformService productReadPlatformService) {
        this.loanReadPlatformService = loanReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.shareAccountReadPlatformService = shareAccountReadPlatformService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.savingsProductReadPlatformService = savingsProductReadPlatformService;
        this.productReadPlatformService = productReadPlatformService;
    }

    public LoanReadPlatformService getLoanReadPlatformService() {
        return loanReadPlatformService;
    }

    public SavingsAccountReadPlatformService getSavingsAccountReadPlatformService() {
        //ServerSentSSbProcessUpdate.streamSseMvc()
        return savingsAccountReadPlatformService;
    }

    public ShareAccountReadPlatformService getShareAccountReadPlatformService() {
        return shareAccountReadPlatformService;
    }

    public LoanProductReadPlatformService getLoanProductReadPlatformService() {
        return loanProductReadPlatformService;
    }

    public SavingsProductReadPlatformService getSavingsProductReadPlatformService() {
        return savingsProductReadPlatformService;
    }

    public ProductReadPlatformService getProductReadPlatformService() {
        return productReadPlatformService;
    }
}
