/*

    Created by Sinatra Gunda
    At 8:12 PM on 9/25/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.service;

import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciationSchedule;

import java.util.List;

public interface DepreciationMethod {

    public List<DepreciationSchedule> depreciation();

}
