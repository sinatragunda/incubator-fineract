/*

    Created by Sinatra Gunda
    At 11:19 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.commissions.data.CommissionChargeData;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class CommissionChargesReadPlatformServiceImpl implements CommissionChargesReadPlatformService {

    private CurrencyReadPlatformService currencyReadPlatformService ;
    private CommissionChargeDropdownReadPlatformService commissionChargeDropdownReadPlatformService;

    @Autowired
    public CommissionChargesReadPlatformServiceImpl(CurrencyReadPlatformService currencyReadPlatformService, CommissionChargeDropdownReadPlatformService commissionChargeDropdownReadPlatformService) {
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.commissionChargeDropdownReadPlatformService = commissionChargeDropdownReadPlatformService;
    }


    @Override
    public CommissionChargeData retrieveNewCommissionChargeTemplate() {

            final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
            final List<EnumOptionData> allowedCalculationTypeOptions = this.commissionChargeDropdownReadPlatformService.retrieveCalculationTypes();
            final List<EnumOptionData> allowedAppliesToOptions = this.commissionChargeDropdownReadPlatformService.retrieveApplicableToTypes();
            final List<EnumOptionData> allowedCollectionTimeOptions = this.commissionChargeDropdownReadPlatformService.retrieveCollectionTimeTypes();


            return CommissionChargeData.template(currencyOptions, allowedCalculationTypeOptions, allowedAppliesToOptions,
                    allowedCollectionTimeOptions);

    }
}
