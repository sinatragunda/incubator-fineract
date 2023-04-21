/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 April 2023 at 10:17
 */
package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.utility.service.DataEnumerationService;

import java.util.Collection;

public interface SavingsAccountTransactionReadPlatformService extends DataEnumerationService {

    Collection<SavingsAccountTransactionData> retrieveAll();
}
