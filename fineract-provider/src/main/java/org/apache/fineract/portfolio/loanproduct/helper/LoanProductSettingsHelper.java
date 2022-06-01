/*

    Created by Sinatra Gunda
    At 12:47 AM on 5/31/2022

*/
package org.apache.fineract.portfolio.loanproduct.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductSettingsWritePlatformService;

public class LoanProductSettingsHelper {

    public static CommandProcessingResult create(LoanProductSettingsWritePlatformService loanProductSettingsWritePlatformService , LoanProduct loanProduct , JsonCommand command){
        return loanProductSettingsWritePlatformService.create(command ,loanProduct);

    }
}
