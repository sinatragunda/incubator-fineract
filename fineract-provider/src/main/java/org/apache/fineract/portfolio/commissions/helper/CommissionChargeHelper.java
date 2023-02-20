/*

    Created by Sinatra Gunda
    At 9:52 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.helper;

import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.domain.AccountAssociations;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.commissions.service.CommissionsChargeService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.wese.helper.ComparatorUtility;

import java.math.BigDecimal;
import java.util.*;

import org.apache.fineract.wese.helper.TimeHelper;
import org.joda.time.LocalDate;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CommissionChargeHelper {

    // this now seems more professional all many classes autowired in
    public static void depositCommissionCharges(CommissionsChargeService commissionsChargeService, Loan loan , ChargeTimeType chargeTimeType){
        commissionsChargeService.depositCommissionCharges(loan ,chargeTimeType);
    }

    public static void ftCommissionCharges(CommissionsChargeService commissionsChargeService, AccountTransferDTO accountTransferDTO, ChargeTimeType chargeTimeType){
        commissionsChargeService.ftCommissionCharges(accountTransferDTO ,chargeTimeType);
    }

    public static boolean isChargeTime(Charge charge , ChargeTimeType chargeTime){
        int chargeTimeInt = charge.getChargeTimeType();
        ChargeTimeType loanCommissionChargeTime =  ChargeTimeType.fromInt(chargeTimeInt);
        boolean equals = ComparatorUtility.areObjectsEqual(loanCommissionChargeTime ,chargeTime);
        return equals ;

    }

    /**
     * Modified 16/02/2023 at 1653
     * BaseAmount is the amount at current stage of loan
     */ 
    private static BigDecimal getBaseAmount(Loan loan ,ChargeTimeType  loanCommissionChargeTime ,ChargeTimeType currentChargeTimeType){

        BigDecimal principal = BigDecimal.ZERO ;

        switch (loanCommissionChargeTime){
            case DISBURSEMENT:
                principal = loan.getDisbursedAmount();
                break;
            case LOAN_CLOSED:
                principal = loan.getDisbursedAmount();
                break;
            case LOAN_APPROVED:
                principal = loan.getApprovedPrincipal();
                break;

        }
        return principal ;
    }

    public static BigDecimal calculateCommission(Loan loan ,Charge charge , ChargeTimeType currentChargeTimeType){

        ChargeTimeType loanCommissionChargeTime = ChargeTimeType.fromInt(charge.getChargeTimeType());

        BigDecimal commissionAmount = BigDecimal.ZERO;
        BigDecimal principal = getBaseAmount(loan ,loanCommissionChargeTime ,currentChargeTimeType);
        BigDecimal interest = loan.getTotalInterest();
        BigDecimal valuationAmount = charge.getAmount();


        ChargeCalculationType chargeCalculationCriteria = ChargeCalculationType.fromInt(charge.getChargeTimeType());
        BigDecimal percentageValuation = valuationAmount.divide(BigDecimal.valueOf(100));

        switch (chargeCalculationCriteria){
            case FLEXIBLE_AMOUNT:
                commissionAmount = charge.getAmount();
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
