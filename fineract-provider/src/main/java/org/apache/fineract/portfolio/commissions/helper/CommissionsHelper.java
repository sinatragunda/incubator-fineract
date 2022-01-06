/*

    Created by Sinatra Gunda
    At 9:52 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.helper;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.commissions.constants.CommissionsApiConstants;
import org.apache.fineract.portfolio.commissions.data.LoanAgentDataBridge;
import org.apache.fineract.portfolio.commissions.domain.LoanAgent;
import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.enumerations.LOAN_COMMISSION_CHARGE_TIME;
import org.apache.fineract.portfolio.commissions.service.AttachedCommissionChargesWritePlatformService;
import org.apache.fineract.portfolio.commissions.service.LoansFromAgentsWritePlatformService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.wese.helper.ComparatorUtility;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;
import org.apache.fineract.wese.helper.TimeHelper;
import org.joda.time.LocalDate;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map ;

public class CommissionsHelper {


    // charge time type comes from loan event class to tell at what time it is now
    // not sure where this will fit now
    // will be called from loan event classes ,when one is fired up then make sure one of these is executed
    public static void deductAgentCommissionCharges(LoansFromAgents loansFromAgents,ChargeTimeType chargeTimeType , CommissionCharge commissionCharge, SavingsAccountDomainService savingsAccountDomainService){

        /// if not same time prolly skip here
        boolean isChargeTime = isChargeTime(loansFromAgents,commissionCharge , chargeTimeType);

        Loan loan = loansFromAgents.getLoan();


        if(isChargeTime){

            /// this is charge time now do the honors
            System.err.println("-------------------------------loan commission charge time then deposit to savings account");
            BigDecimal commissionAmount = calculateCommission(loansFromAgents,commissionCharge);
            LoanAgent loanAgent = loansFromAgents.getLoanAgent();
            SavingsAccount savingsAccount = loanAgent.getSavingsAccount();
            LocalDate transactionDate = getTransactionDate(loan ,chargeTimeType);

            // some transaction getter date function here
            savingsAccountDomainService.handleDepositLiteEx(savingsAccount ,transactionDate ,commissionAmount ,"Loan Agent Commission");

            /// update loan commission to mark it as deposited here

        }
    }


    // this will be called where? and when ?
    public static void linkLoanToCommissions(Loan loanApplication , LoansFromAgentsWritePlatformService loansFromAgentsWritePlatformService , AttachedCommissionChargesWritePlatformService attachedCommissionChargesWritePlatformService , FromJsonHelper fromJsonHelper , JsonCommand jsonCommand){

        // link loan to commissions using jsoncommand entry .Trying to the mess out of already long classes with lots of functions now
        final JsonElement jsonElement = jsonCommand.parsedJson();
        boolean parameterExists = fromJsonHelper.parameterExists(CommissionsApiConstants.agentDataParam, jsonElement);

        System.err.println("-------------------------------parameter exists here ----------------"+parameterExists);

        Optional.of(parameterExists).filter(f -> f).ifPresent(e->{
            /// do a whole lot of things to do with creating new commission data
            final JsonObject topLevelJsonElement = jsonElement.getAsJsonObject();
            final JsonObject agentDataJsonObject  = topLevelJsonElement.get(CommissionsApiConstants.agentDataParam).getAsJsonObject();

            final String apiJson = agentDataJsonObject.toString();

            System.err.println("---------------does it even show any data ?----------------------");

            final LoanAgentDataBridge loanAgentDataBridge = LoanAgentDataBridge.fromJson(apiJson);

            /// create loans from agents link and get agent data
            Long loanAgentId  = loanAgentDataBridge.getLoanAgentData().getId();

            System.err.println("--------------------loan agent id is -----------------"+loanAgentId);

            Map<String ,Object> map = new HashMap<>();
            map.put(CommissionsApiConstants.loanFromAgentsIdParam ,loanAgentId);
            map.put(CommissionsApiConstants.loanIdParam ,loanApplication.getId());

            String payload = JsonHelper.serializeMapToJson(map);

            System.err.println("-----------------------payload is ---------------"+payload);

            JsonCommand loansFromAgentsJsonCommand  = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);

            // or we can use another nice class here that just gives us command processing without first converting to json command

            //final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoansFromAgents().withJson(payload).build();

            //final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);



            CommandProcessingResult loansFromAgentsCommandResult = loansFromAgentsWritePlatformService.create(loansFromAgentsJsonCommand);

            Long loansFromAgentsId = loansFromAgentsCommandResult.resourceId();

            System.err.println("------------------loansfromagents id is now ---------------------"+loansFromAgentsId);

            LoansFromAgents loansFromAgents = LoansFromAgents.fromId(loansFromAgentsId);

            loansFromAgents.setLoan(loanApplication);

            loanAgentDataBridge.setLoansFromAgents(loansFromAgents);

            CommandProcessingResult commandProcessingResult = attachedCommissionChargesWritePlatformService.create(loanAgentDataBridge);

            Long entityId = commandProcessingResult.resourceId();

            System.err.println("----------------------------create new agent data --------since all been validated ---------"+entityId);

        });

    }

    public static boolean isChargeTime(LoansFromAgents loansFromAgents,CommissionCharge commissionCharge , ChargeTimeType chargeTime){

        ChargeTimeType loanCommissionChargeTime = commissionCharge.getChargeTimeType();
        boolean equals = ComparatorUtility.areObjectsEqual(loanCommissionChargeTime ,chargeTime);
        return equals ;

    }

    public static BigDecimal calculateCommission(LoansFromAgents loansFromAgents, CommissionCharge commissionCharge){

        System.err.println("---------------------loan could have been null ,but is loansfromagents present  ? -------------------"+Optional.ofNullable(loansFromAgents).isPresent());

        Loan loan = loansFromAgents.getLoan();


        System.err.println("====================of nullable loan ? ===================="+Optional.ofNullable(loan).isPresent());

        System.err.println("----------------------------we crush here now ------------------");

        BigDecimal commissionAmount = BigDecimal.ZERO;
        BigDecimal principal = loan.getProposedPrincipal();
        BigDecimal interest = loan.getTotalInterest();
        BigDecimal valuationAmount = commissionCharge.getAmount();

        ChargeTimeType loanCommissionChargeTime = commissionCharge.getChargeTimeType();

        switch (loanCommissionChargeTime){
            case DISBURSEMENT:
                principal = loan.getDisbursedAmount();
                break;
            case LOAN_CLOSED:
                principal = loan.getDisbursedAmount();
                break;
            case LOAN_APPLICATION:
                principal = loan.getProposedPrincipal();
                break;
        }

        ChargeCalculationType chargeCalculationCriteria = commissionCharge.getChargeCalculationType();
        BigDecimal percentageValuation = valuationAmount.divide(BigDecimal.valueOf(100));

        switch (chargeCalculationCriteria){
            case FLEXIBLE_AMOUNT:
                commissionAmount = commissionCharge.getAmount();
                break;
            case PERCENT_OF_AMOUNT:
                commissionAmount = principal.multiply(percentageValuation);
                break;
            case PERCENT_OF_INTEREST:
                commissionAmount = interest.multiply(percentageValuation);
                break;
            case PERCENT_OF_AMOUNT_AND_INTEREST:
                 commissionAmount = percentageValuation.multiply(interest.add(principal));
                 break;
            case FLAT:
                commissionAmount = valuationAmount;
                break;

        }
        return commissionAmount ;

    }

    public static LocalDate getTransactionDate(Loan loan ,ChargeTimeType chargeTimeType){

        LocalDate transactionDate = null ;

        switch (chargeTimeType){
            case LOAN_CLOSED:
                Date date = loan.getClosedOnDate();
                transactionDate = TimeHelper.javaDateToJodaLocalDate(date);
                break;
            case LOAN_APPLICATION:
                transactionDate = loan.getSubmittedOnDate();
                break;
            case DISBURSEMENT:
                transactionDate = loan.getDisbursementDate();
                break;
        }

        return transactionDate ;

    }



}
