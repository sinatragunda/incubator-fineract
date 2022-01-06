/*

    Created by Sinatra Gunda
    At 11:19 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.portfolio.commissions.data.CommissionChargeData;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;

import java.util.List;

public interface CommissionChargesReadPlatformService {

    CommissionChargeData retrieveNewCommissionChargeTemplate();
    CommissionChargeData retrieveOne(Long id);
    List<CommissionChargeData> retrieveAll();
}
