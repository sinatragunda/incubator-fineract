/*

    Created by Sinatra Gunda
    At 5:40 PM on 1/6/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.apache.fineract.portfolio.agentbanking.data.AgentData;
import org.apache.fineract.portfolio.agentbanking.service.AgentReadPlatformService;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.repo.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.commissions.helper.CommissionChargeHelper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.exception.InvalidCurrencyException;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.fineract.portfolio.savings.exception.SavingsAccountNotFoundException;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.joda.time.LocalDate;


@Service
public class CommissionsChargeService {

    private SavingsAccountAssembler savingsAccountAssembler ;
    private SavingsAccountDomainService savingsAccountDomainService ;
    private AgentReadPlatformService agentReadPlatformService;
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private LoanRepositoryWrapper loanRepositoryWrapper;
    private AccountTransfersWritePlatformService accountTransfersWritePlatformService;
    private ChargeRepositoryWrapper chargeRepositoryWrapper;
    private LoanChargeRepository loanChargeRepository;

    @Autowired
    public CommissionsChargeService(SavingsAccountAssembler savingsAccountAssembler, SavingsAccountDomainService savingsAccountDomainService ,AgentReadPlatformService agentReadPlatformService ,final SavingsAccountReadPlatformService savingsAccountReadPlatformService ,final LoanRepositoryWrapper loanRepositoryWrapper ,final AccountTransfersWritePlatformService accountTransfersWritePlatformService ,final ChargeRepositoryWrapper chargeRepositoryWrapper ,final LoanChargeRepository loanChargeRepository) {
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.agentReadPlatformService = agentReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
        this.chargeRepositoryWrapper = chargeRepositoryWrapper;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.loanChargeRepository = loanChargeRepository;
    }

    public void ftCommissionCharges(AccountTransferDTO accountTransferDTO ,ChargeTimeType chargeTimeType){

        System.err.println("---------------------------object as when we recieve it ---- "+accountTransferDTO);

        Long loanId = accountTransferDTO.getToAccountId();

        System.err.println("--------------------at this instance to account type is loan ,will set it to savings once we get loan officer detaisl to account id is which we should search "+loanId);

        Loan loan = loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);

        Long loanChargeId = accountTransferDTO.getChargeId();

        System.err.println("-----------------------loan charge id is  -------------"+loanChargeId);

        System.err.println("-----------------------------charge being used it "+loanChargeId);

        LoanCharge loanCharge = loanChargeRepository.findOne(loanChargeId);

        Charge charge = loanCharge.getCharge();

        System.err.println("---------------------is loan object present ?  "+Optional.ofNullable(loan).isPresent());

        System.err.println("---------------------was client not loaded ,is client load ? ? "+Optional.ofNullable(loan.getClient()).isPresent());

        Client client = loan.getClient();

        //Charge charge = chargeRepositoryWrapper.findOneWithNotFoundDetection(chargeId);
        Staff staff = loan.getLoanOfficer();

        System.err.println("---------------------transfer funds to staff with name "+staff.displayName());

        Long staffId = staff.getId();
        String currencyCode = loan.getCurrencyCode();
        AgentData agentData = agentReadPlatformService.retrieveOne(staffId);

        Long clientId = agentData.clientId();
        SavingsAccountData savingsAccountData = savingsAccount(clientId,currencyCode);
        Long savingsAccountId = savingsAccountData.id();

        String depositNote = String.format("Commission charge deposit from loan %s for client %s at %s",client.getDisplayName(),loan.getAccountNumber() ,chargeTimeType.getCode());

        System.err.println("--------------------------set from savings account to "+savingsAccountId);

        accountTransferDTO.setToAccountId(savingsAccountId);
        accountTransferDTO.setToAccountType(PortfolioAccountType.SAVINGS);

        accountTransferDTO.setNoteText(depositNote);

        System.err.println("============object as we are sending it "+accountTransferDTO);

        Predicate<Charge> chargeTimePredicate = (e)-> CommissionChargeHelper.isChargeTime(e ,chargeTimeType);

        Predicate<Charge> isAgentChargePredicate = (e)-> e.isAgentCharge();

        Consumer<Charge> forCharges = (e)->{

            System.err.println("------------------post transaction for this  charge ----------------");
            BigDecimal amount =  CommissionChargeHelper.calculateCommission(loan ,e ,chargeTimeType);
            accountTransfersWritePlatformService.transferFunds(accountTransferDTO);

        };

        List<Charge> chargesList = Arrays.asList(charge);
        chargesList.stream().filter(isAgentChargePredicate).filter(chargeTimePredicate).forEach(forCharges);
    }

    public void depositCommissionCharges(Loan loan, ChargeTimeType chargeTimeType){

        System.err.println("---------------------------this is the problem child confirm ? ");

        Client client = loan.getClient();
        Staff staff = loan.getLoanOfficer();
        LoanProduct loanProduct = loan.loanProduct();

        Long staffId = staff.getId();
        String currencyCode = loan.getCurrencyCode();
        AgentData agentData = agentReadPlatformService.retrieveOne(staffId);

        Long clientId = agentData.clientId();
        SavingsAccountData savingsAccountData = savingsAccount(clientId,currencyCode);
        Long savingsAccountId = savingsAccountData.id();
        List<Charge> chargesList = loanProduct.getLoanProductCharges();

        System.err.println("-------------charges on this stream are "+chargesList.size());

        Predicate<Charge> chargeTimePredicate = (e)-> CommissionChargeHelper.isChargeTime(e ,chargeTimeType);
        Predicate<Charge> isAgentChargePredicate = (e)-> e.isAgentCharge();

        Consumer<Charge> forCharges = (e)->{

            BigDecimal amount =  CommissionChargeHelper.calculateCommission(loan ,e ,chargeTimeType);

            System.err.println("----------------------------------------commission of ,paid  "+amount);

            SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(savingsAccountId);

            String depositNote = String.format("Commission charge deposit from loan %s for client %s at %s",client.getDisplayName(),loan.getAccountNumber() ,chargeTimeType.getCode());

            System.err.println("------------------deposit note is ------"+depositNote);

            LocalDate transactionDate = CommissionChargeHelper.getTransactionDate(loan ,chargeTimeType);
            savingsAccountDomainService.handleDepositLite(savingsAccount ,transactionDate ,amount ,depositNote);
        };
        chargesList.stream().filter(isAgentChargePredicate).filter(chargeTimePredicate).forEach(forCharges);
    }

    private SavingsAccountData savingsAccount(Long clientId,String ccy){

        Collection<SavingsAccountData> clientAccounts = savingsAccountReadPlatformService.findByCurrency(clientId ,ccy);
        boolean hasAccount = clientAccounts.stream().findFirst().isPresent();
        if(!hasAccount){
            throw new SavingsAccountNotFoundException(clientId);
        }

        return clientAccounts.stream().findFirst().get();

    }

    private boolean currencyValidator(String currencyCode , SavingsAccountData savingsAccountData){
        String accountCcy = savingsAccountData.currency().code();
        boolean equals = currencyCode.equalsIgnoreCase(accountCcy);
        if(!equals){
            //throw new InvalidCurrencyException(accountCcy);
        }
        return true;


    }

}
