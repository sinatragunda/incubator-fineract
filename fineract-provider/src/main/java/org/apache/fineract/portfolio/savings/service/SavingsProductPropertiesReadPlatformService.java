/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 23 March 2023 at 10:16
 */
package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.portfolio.savings.data.SavingsProductPropertiesData;

public interface SavingsProductPropertiesReadPlatformService {

    public SavingsProductPropertiesData retrieveOne(Long productId);
    public SavingsProductPropertiesData template(Long officeId);
}
