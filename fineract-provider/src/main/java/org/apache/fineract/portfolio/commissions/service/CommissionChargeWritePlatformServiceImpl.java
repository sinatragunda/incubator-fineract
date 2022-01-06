/*

    Created by Sinatra Gunda
    At 12:46 PM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.charge.domain.ChargeAppliesTo;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.commissions.constants.CommissionChargeApiConstants;
import org.apache.fineract.portfolio.commissions.data.CommissionChargeData;

import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.repo.CommissionChargesRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


@Service
public class CommissionChargeWritePlatformServiceImpl implements CommissionChargeWritePlatformService{

    private CommissionChargesRepository commissionChargesRepository ;

    @Autowired
    public CommissionChargeWritePlatformServiceImpl(CommissionChargesRepository commissionChargesRepository) {
        this.commissionChargesRepository = commissionChargesRepository;
    }

    @Override
    public CommandProcessingResult create(JsonCommand jsonCommand) {

        Integer chargeAppliesToInt = jsonCommand.integerValueOfParameterNamed(CommissionChargeApiConstants.chargeAppliesToParam);
        Integer chargeTimeTypeInt = jsonCommand.integerValueOfParameterNamed(CommissionChargeApiConstants.chargeTimeTypeParam);
        Integer chargeCalculationTypeInt = jsonCommand.integerValueOfParameterNamed(CommissionChargeApiConstants.chargeCalculationTypeParam);

        String currencyCode = jsonCommand.stringValueOfParameterNamed(CommissionChargeApiConstants.currencyCodeParam);
        BigDecimal amount = jsonCommand.bigDecimalValueOfParameterNamed(CommissionChargeApiConstants.amountParam);
        String name = jsonCommand.stringValueOfParameterNamed(CommissionChargeApiConstants.nameParam);
        Boolean isActive = jsonCommand.booleanPrimitiveValueOfParameterNamed(CommissionChargeApiConstants.isActiveParam);

        ChargeTimeType chargeTimeType = ChargeTimeType.fromInt(chargeTimeTypeInt);
        ChargeCalculationType chargeCalculationType = ChargeCalculationType.fromInt(chargeCalculationTypeInt);
        ChargeAppliesTo chargeAppliesTo = ChargeAppliesTo.fromInt(chargeAppliesToInt);

        CommissionCharge commissionCharge = new CommissionCharge(name ,currencyCode ,amount ,chargeTimeType ,chargeAppliesTo ,chargeCalculationType,isActive);
        this.commissionChargesRepository.save(commissionCharge);

        return new CommandProcessingResultBuilder() //
                .withCommandId(jsonCommand.commandId()) //
                .withEntityId(commissionCharge.getId()) //
                .build();

    }
}
