/*

    Created by Sinatra Gunda
    At 11:19 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.portfolio.commissions.data.CommissionChargeData;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;

public interface CommissionChargesReadPlatformService {

    CommissionChargeData retrieveNewCommissionChargeTemplate();
}
