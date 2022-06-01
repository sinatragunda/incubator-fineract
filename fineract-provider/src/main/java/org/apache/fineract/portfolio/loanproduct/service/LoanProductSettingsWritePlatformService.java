/*

    Created by Sinatra Gunda
    At 12:24 AM on 5/31/2022

*/
package org.apache.fineract.portfolio.loanproduct.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;

public interface LoanProductSettingsWritePlatformService {

    CommandProcessingResult create(JsonCommand jsonCommand,LoanProduct loanProduct);
}
