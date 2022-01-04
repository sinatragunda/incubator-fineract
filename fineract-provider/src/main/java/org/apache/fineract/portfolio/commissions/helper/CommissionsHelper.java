/*

    Created by Sinatra Gunda
    At 9:52 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.helper;

import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.commissions.domain.LoanAgent;
import org.apache.fineract.portfolio.commissions.domain.LoanCommission;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.enumerations.LOAN_COMMISSION_CHARGE_TIME;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.wese.enumerations.CHARGE_CALCULATION_CRITERIA;
import org.apache.fineract.wese.helper.ComparatorUtility;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.fineract.wese.helper.TimeHelper;
import org.joda.time.LocalDate;


public class CommissionsHelper {


    // not sure where this will fit now
    public static void linkToLoan(LoanCommission loanCommission , LOAN_COMMISSION_CHARGE_TIME loanCommissionChargeTime , Loan loan, SavingsAccountDomainService savingsAccountDomainService){

        /// if not same time prolly skip here
        boolean isChargeTime = isChargeTime(loanCommission ,loanCommissionChargeTime);

        if(isChargeTime){

            /// this is charge time now do the honors
            System.err.println("-------------------------------loan commission charge time then deposit to savings account");
            BigDecimal commissionAmount = calculateCommission(loanCommission ,loan);
            LoanAgent loanAgent = loanCommission.getLoanAgent();
            SavingsAccount savingsAccount = loanAgent.getSavingsAccount();
            LocalDate transactionDate = getTransactionDate(loan ,loanCommissionChargeTime);

            // some transaction getter date function here
            savingsAccountDomainService.handleDepositLiteEx(savingsAccount ,transactionDate ,commissionAmount ,"Loan Agent Commission");


            /// update loan commission to mark it as deposited here

        }
    }


    // this will be called where? and when ?
    public static void depositCommission(){


    }

    public static boolean isChargeTime(LoanCommission loanCommission ,LOAN_COMMISSION_CHARGE_TIME chargeTime){

        ChargeTimeType loanCommissionChargeTime = loanCommission.getCommissionCharge().getChargeTimeType();
        boolean equals = ComparatorUtility.areObjectsEqual(loanCommissionChargeTime ,chargeTime);
        return equals ;

    }

    public static BigDecimal calculateCommission(LoanCommission loanCommission , Loan loan){

        CommissionCharge commissionCharge = loanCommission.getCommissionCharge();

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
        }

        ChargeCalculationType chargeCalculationCriteria = commissionCharge.getChargeCalculationType();

        switch (chargeCalculationCriteria){
            case PERCENT_OF_AMOUNT:
                commissionAmount = principal.multiply(valuationAmount);
                break;
            case PERCENT_OF_INTEREST:
                commissionAmount = interest.multiply(valuationAmount);
                break;
            case PERCENT_OF_AMOUNT_AND_INTEREST:
                 commissionAmount = valuationAmount.multiply(interest.add(principal));
                 break;
            case FLAT:
                commissionAmount = valuationAmount;
                break;

        }
        return commissionAmount ;

    }

    public static LocalDate getTransactionDate(Loan loan ,LOAN_COMMISSION_CHARGE_TIME loanCommissionChargeTime){

        LocalDate transactionDate = null ;

        switch (loanCommissionChargeTime){
            case LOAN_CLOSE:
                Date date = loan.getClosedOnDate();
                transactionDate = TimeHelper.javaDateToJodaLocalDate(date);
                break;
            case LOAN_APPLICATION:
                transactionDate = loan.getSubmittedOnDate();
                break;
            case LOAN_DISBURSEMENT:
                transactionDate = loan.getDisbursementDate();
        }

        return transactionDate ;

    }



}
