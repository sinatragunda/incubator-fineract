/*

    Created by Sinatra Gunda
    At 4:11 PM on 4/10/2022

*/
package org.apache.fineract.portfolio.loanaccount.template.service;

import org.apache.fineract.portfolio.loanaccount.template.NkwaziLoanTemplate;

public interface NkwaziLoanTemplateReadPlatformService {

    NkwaziLoanTemplate retrieveOne(Long clientId);
}
