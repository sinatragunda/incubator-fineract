/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 May 2023 at 12:14
 */
package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanInterestRecalcualtionAdditionalDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.utility.helper.FunctionsHelper;
import org.apache.fineract.utility.helper.NonNullableList;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;

import java.math.BigDecimal;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.JsonArray;
public class CustomLoanSchedulerAssembler {

    private FromJsonHelper fromJsonHelper = new FromJsonHelper();

    public List<LoanRepaymentScheduleInstallment> assembleFromJson(Loan loan , JsonCommand command){

        boolean has = command.parameterExists(LoanApiConstants.scheduleParam);
        NonNullableList<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallments = new NonNullableList<>();

        if(has){
            JsonArray schedule =  command.arrayOfParameterNamed(LoanApiConstants.scheduleParam);
         
            for(JsonElement element : schedule){
                LoanRepaymentScheduleInstallment installment = asseembleFromJson(loan ,element);
                loanRepaymentScheduleInstallments.add(installment);
            }
        }
        return loanRepaymentScheduleInstallments;
    }

    public LoanRepaymentScheduleInstallment asseembleFromJson(Loan loan ,JsonElement element){

        LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = null ;

        Locale locale = Locale.UK;
        boolean has = fromJsonHelper.parameterExists(LoanApiConstants.principalDueParam ,element);

        System.err.println("-----------has principal due element ? "+has);

        if(has) {
            Integer installmentNumber = fromJsonHelper.extractIntegerNamed(LoanApiConstants.installmentNumberParam ,element ,locale);
            BigDecimal principal = fromJsonHelper.extractBigDecimalNamed(LoanApiConstants.principalDueParam, element ,locale);
            BigDecimal interest = fromJsonHelper.extractBigDecimalNamed(LoanApiConstants.interestDueParam, element ,locale);
            BigDecimal penaltyCharges = fromJsonHelper.extractBigDecimalNamed(LoanApiConstants.penaltyChargesParam, element ,locale);
            BigDecimal feesCharges = fromJsonHelper.extractBigDecimalNamed(LoanApiConstants.feesChargesParam, element ,locale);

            //LocalDate fromDate = fromJsonHelper.extractLocalDateNamed(LoanApiConstants.fromDateParam ,element) ;
            //LocalDate toDate = fromJsonHelper.extractLocalDateNamed(LoanApiConstants.toDateParam ,element) ;

            System.err.println("------------------new principal is ------"+principal);

            Set<LoanInterestRecalcualtionAdditionalDetails> loanInterestRecalcualtionAdditionalDetails = null;
            loanRepaymentScheduleInstallment = new LoanRepaymentScheduleInstallment(loan, installmentNumber, null, null, principal, interest, feesCharges, penaltyCharges, false, loanInterestRecalcualtionAdditionalDetails);

        }
        return loanRepaymentScheduleInstallment ;
    }
}
