/*

    Created by Sinatra Gunda
    At 5:40 PM on 1/6/2022

*/
package org.apache.fineract.portfolio.commissions.helper;

import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.commissions.data.AttachedCommissionChargesData;
import org.apache.fineract.portfolio.commissions.domain.AttachedCommissionCharges;
import org.apache.fineract.portfolio.commissions.repo.AttachedCommissionChargesRepository;
import org.apache.fineract.portfolio.commissions.service.AttachedCommissionChargesReadPlatformService;
import org.apache.fineract.portfolio.commissions.service.LoansFromAgentsReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.joda.time.LocalDate;


@Service
public class CommissionsHelperService {


    private LoansFromAgentsReadPlatformService loansFromAgentsReadPlatformService;
    private AttachedCommissionChargesReadPlatformService attachedCommissionChargesReadPlatformService ;
    private SavingsAccountAssembler savingsAccountAssembler ;
    private SavingsAccountDomainService savingsAccountDomainService ;
    private AttachedCommissionChargesRepository attachedCommissionChargesRepository ;


    @Autowired
    public CommissionsHelperService(LoansFromAgentsReadPlatformService loansFromAgentsReadPlatformService, AttachedCommissionChargesReadPlatformService attachedCommissionChargesReadPlatformService, SavingsAccountAssembler savingsAccountAssembler, SavingsAccountDomainService savingsAccountDomainService, AttachedCommissionChargesRepository attachedCommissionChargesRepository) {
        this.loansFromAgentsReadPlatformService = loansFromAgentsReadPlatformService;
        this.attachedCommissionChargesReadPlatformService = attachedCommissionChargesReadPlatformService;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.attachedCommissionChargesRepository = attachedCommissionChargesRepository;
    }

    // charge time type comes from loan event class to tell at what time it is now
    // not sure where this will fit now
    // will be called from loan event classes ,when one is fired up then make sure one of these is executed
    public void depositAgentCommissionCharges(Loan loan, ChargeTimeType chargeTimeType){

        /// if not same time prolly skip here
        Long loanId = loan.getId();

        List<AttachedCommissionChargesData> attachedCommissionChargesDataList = attachedCommissionChargesReadPlatformService.retrieveAllByLoan(loanId);

        attachedCommissionChargesDataList.stream().filter(f->{
                return CommissionsHelper.isChargeTime(f ,chargeTimeType);
            }).forEach(attachedCommissionChargesData->{
                // for each charge lets now deposit

            Long attachedCommissionChargeId = attachedCommissionChargesData.getId();

            System.err.println("----------------------------chargetime now and recalculate commissions ------------"+attachedCommissionChargesData.getAmount().doubleValue());

            BigDecimal commissionAmount =     attachedCommissionChargesData.getAmount();

            System.err.println("-------------new recalculated value is -----------------"+commissionAmount.doubleValue());

            boolean isDeposited = attachedCommissionChargesData.isDeposited();

            if(isDeposited) {
                return;
            }
            // from loan agent data we can get savings account id
            System.err.println("----------------------charge time activated now ----------"+isDeposited);

            // now we have loandagent data ,lets get the savingsaccount
            Long savingsAccountId = attachedCommissionChargesData.getSavingsAccountId();
            SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(savingsAccountId);

            // now lets deposit
            String depositNote = String.format("Commission charge deposit from loan %s at %s",loan.getAccountNumber() ,chargeTimeType.getCode());

            LocalDate transactionDate = CommissionsHelper.getTransactionDate(loan ,chargeTimeType);
            savingsAccountDomainService.handleDepositLiteEx(savingsAccount ,transactionDate ,commissionAmount ,depositNote);

            // update is deposited here
            AttachedCommissionCharges attachedCommissionCharges = attachedCommissionChargesRepository.findOne(attachedCommissionChargeId);
            attachedCommissionCharges.setDeposited(true);

        });

        // get loan id now lets get loan agent id
        System.err.println("-------------------we have returned no charge time ?");

    }

}
