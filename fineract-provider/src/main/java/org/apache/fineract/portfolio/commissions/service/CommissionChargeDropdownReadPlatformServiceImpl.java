/*

    Created by Sinatra Gunda
    At 11:24 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.charge.domain.ChargeAppliesTo;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargePaymentMode;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.fineract.portfolio.charge.service.ChargeEnumerations.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class CommissionChargeDropdownReadPlatformServiceImpl implements CommissionChargeDropdownReadPlatformService {

    @Override
    public List<EnumOptionData> retrieveCalculationTypes() {

        return Arrays.asList(chargeCalculationType(ChargeCalculationType.FLAT),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_AMOUNT_AND_INTEREST),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_INTEREST),
                chargeCalculationType(ChargeCalculationType.PERCENT_OF_DISBURSEMENT_AMOUNT));
    }

    @Override
    public List<EnumOptionData> retrieveApplicableToTypes() {

        return Arrays.asList(chargeAppliesTo(ChargeAppliesTo.LOAN));

    }

    @Override
    public List<EnumOptionData> retrieveCollectionTimeTypes() {
        return Arrays.asList(chargeTimeType(ChargeTimeType.LOAN_APPLICATION),
                chargeTimeType(ChargeTimeType.DISBURSEMENT),
                chargeTimeType(ChargeTimeType.LOAN_CLOSED));

    }

}
