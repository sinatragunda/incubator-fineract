/*

    Created by Sinatra Gunda
    At 9:52 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.commissions.constants.CommissionsApiConstants;
import org.apache.fineract.portfolio.commissions.data.AttachedCommissionChargesData;
import org.apache.fineract.portfolio.commissions.data.LoanAgentDataBridge;
import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.service.AttachedCommissionChargesWritePlatformService;
import org.apache.fineract.portfolio.commissions.service.LoansFromAgentsWritePlatformService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.wese.helper.ComparatorUtility;

import java.math.BigDecimal;
import java.util.*;

import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;
import org.apache.fineract.wese.helper.TimeHelper;
import org.joda.time.LocalDate;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CommissionsHelper {


    // this now seems more professional all many classes autowired in
    public static void depositCommissionCharges(CommissionsHelperService commissionsHelperService ,Loan loan ,ChargeTimeType chargeTimeType){
        commissionsHelperService.depositAgentCommissionCharges(loan ,chargeTimeType);
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

    public static boolean isChargeTime(AttachedCommissionChargesData attachedCommissionChargesData , ChargeTimeType chargeTime){

        ChargeTimeType loanCommissionChargeTime = attachedCommissionChargesData.getChargeTimeType();
        boolean equals = ComparatorUtility.areObjectsEqual(loanCommissionChargeTime ,chargeTime);
        return equals ;

    }


    public static BigDecimal getAmount(Loan loan ,ChargeTimeType  loanCommissionChargeTime ,ChargeTimeType currentChargeTimeType){

        BigDecimal principal = BigDecimal.ZERO ;
        /// lets say d = 2
        // current stage a = 1
//
//        if(currentChargeTimeType.ordinal() < loanCommissionChargeTime.ordinal()){
//            System.err.println("-----------we dont have this charge ----------------");
//            return principal ;
//        }

        switch (loanCommissionChargeTime){
            case DISBURSEMENT:
                principal = loan.getProposedPrincipal();
                break;
            case LOAN_CLOSED:
                principal = loan.getProposedPrincipal();
                break;
            case LOAN_APPROVED:
                principal = loan.getProposedPrincipal();
                break;

        }
        return principal ;
    }

    public static BigDecimal calculateCommission(LoansFromAgents loansFromAgents, CommissionCharge commissionCharge ,ChargeTimeType currentChargeTimeType){

        System.err.println("---------------------loan could have been null ,but is loansfromagents present  ? -------------------"+Optional.ofNullable(loansFromAgents).isPresent());

        Loan loan = loansFromAgents.getLoan();


        System.err.println("====================of nullable loan ? ===================="+Optional.ofNullable(loan).isPresent());

        System.err.println("----------------------------we crush here now ------------------");


        ChargeTimeType loanCommissionChargeTime = commissionCharge.getChargeTimeType();

        BigDecimal commissionAmount = BigDecimal.ZERO;
        BigDecimal principal = getAmount(loan ,loanCommissionChargeTime ,currentChargeTimeType);
        BigDecimal interest = loan.getTotalInterest();
        BigDecimal valuationAmount = commissionCharge.getAmount();


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
            case LOAN_APPROVED:
                transactionDate = loan.getSubmittedOnDate();
                break;
            case DISBURSEMENT:
                transactionDate = loan.getDisbursementDate();
                break;
        }

        return transactionDate ;

    }



}
