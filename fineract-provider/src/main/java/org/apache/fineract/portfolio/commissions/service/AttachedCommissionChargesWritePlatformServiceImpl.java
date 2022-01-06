/*

    Created by Sinatra Gunda
    At 3:42 AM on 1/6/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.commissions.data.CommissionChargeData;
import org.apache.fineract.portfolio.commissions.data.LoanAgentDataBridge;
import org.apache.fineract.portfolio.commissions.domain.AttachedCommissionCharges;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;
import org.apache.fineract.portfolio.commissions.helper.CommissionsHelper;
import org.apache.fineract.portfolio.commissions.repo.AttachedCommissionChargesRepository;
import org.apache.fineract.portfolio.commissions.repo.CommissionChargesRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class AttachedCommissionChargesWritePlatformServiceImpl implements AttachedCommissionChargesWritePlatformService {

    private CommissionChargesRepository commissionChargesRepository ;
    private AttachedCommissionChargesRepository attachedCommissionChargesRepository ;

    @Autowired
    public AttachedCommissionChargesWritePlatformServiceImpl(CommissionChargesRepository commissionChargesRepository, AttachedCommissionChargesRepository attachedCommissionChargesRepository) {
        this.commissionChargesRepository = commissionChargesRepository;
        this.attachedCommissionChargesRepository = attachedCommissionChargesRepository;
    }

    @Override
    public CommandProcessingResult create(LoansFromAgents loansFromAgents , CommissionCharge commissionCharge){

        BigDecimal amount = CommissionsHelper.calculateCommission(loansFromAgents ,commissionCharge);


        System.err.println("---------------------commission amount is --------------------"+amount.doubleValue());

        AttachedCommissionCharges attachedCommissionCharges = new AttachedCommissionCharges(loansFromAgents ,commissionCharge,false ,amount);

        attachedCommissionChargesRepository.save(attachedCommissionCharges);

        return new CommandProcessingResultBuilder() //
                .withEntityId(attachedCommissionCharges.getId()) //
                .build();

    }

    @Override
    public CommandProcessingResult create(LoanAgentDataBridge loanAgentDataBridge) {

        LoansFromAgents loansFromAgents = loanAgentDataBridge.getLoansFromAgents();

        System.err.println("-------------is loans from agents null ? ---------"+ Optional.ofNullable(loansFromAgents).isPresent());

        List<CommissionChargeData> commissionChargeList = loanAgentDataBridge.getCommissionChargesList();

        System.err.println("----------------------attachedsize list is ---------------------------"+commissionChargeList.size());

        CommandProcessingResult[] commandProcessingResults = {null};

        commissionChargeList.stream().forEach(e->{

            Long commissionChargeId = e.getId();

            System.err.println("---------------commission charge id is --------------------"+commissionChargeId);

            CommissionCharge commissionCharge = this.commissionChargesRepository.findOne(commissionChargeId);

            System.err.println("-----------amount value is ------------------"+e.getAmount().doubleValue());

            commissionCharge.setAmount(e.getAmount());

            commandProcessingResults[0] = create(loansFromAgents ,commissionCharge);

            System.err.println("------------where is null out of bounds now ?---------------");
        });

        return commandProcessingResults[0];
    }
}
