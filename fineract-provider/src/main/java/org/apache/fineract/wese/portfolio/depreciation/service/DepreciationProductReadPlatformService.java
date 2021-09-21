/*

    Created by Sinatra Gunda
    At 9:41 AM on 9/19/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.service;

import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciationProduct;

import java.util.Collection;

public interface DepreciationProductReadPlatformService {

    Collection<DepreciationProduct> retrieveAll();
    DepreciationProduct retrieveOne(Long id);
}
