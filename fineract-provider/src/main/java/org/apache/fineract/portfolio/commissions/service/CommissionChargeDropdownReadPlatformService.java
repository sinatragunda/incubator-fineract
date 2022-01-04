/*

    Created by Sinatra Gunda
    At 11:23 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.List;

public interface CommissionChargeDropdownReadPlatformService {

    List<EnumOptionData> retrieveCalculationTypes();
    List<EnumOptionData> retrieveCollectionTimeTypes();
    List<EnumOptionData> retrieveApplicableToTypes();

}
