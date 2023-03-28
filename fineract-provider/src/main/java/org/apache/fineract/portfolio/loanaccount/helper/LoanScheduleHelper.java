/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 23 March 2023 at 17:35
 */
package org.apache.fineract.portfolio.loanaccount.helper;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.enumerations.LOAN_TRANSITION;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.joda.time.Days;
import org.joda.time.LocalDate;

@Service
public class LoanScheduleHelper {

    private final LoanRepaymentScheduleInstallmentRepository loanRepaymentScheduleInstallmentRepository;

    @Autowired
    public LoanScheduleHelper(LoanRepaymentScheduleInstallmentRepository loanRepaymentScheduleInstallmentRepository) {
        this.loanRepaymentScheduleInstallmentRepository = loanRepaymentScheduleInstallmentRepository;
    }

    public LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment(Loan loan , Date transactionDate , PAYMENT_CODE paymentCode){

        Long loanId = loan.getId();

        Collection<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallmentList = loanRepaymentScheduleInstallmentRepository.findAllByLoanId(loanId);

        Comparator<LoanRepaymentScheduleInstallment> periodComparator = (l , r)-> l.getInstallmentNumber().compareTo(r.getInstallmentNumber()) ;

        loanRepaymentScheduleInstallmentList.stream().sorted(periodComparator);

        LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = null;

        boolean hasSchedule = OptionalHelper.isPresent(loanRepaymentScheduleInstallment);

        if(!hasSchedule){
            return null;
        }

        /**
         * Added 19/09/2022
         * Allow early repayment on loans .If false on loans whose payment date is after current date will be processed
         */

        switch (paymentCode){
            case LOAN_EARLY_REPAYMENT:
                loanRepaymentScheduleInstallment = activeSchedule(loanRepaymentScheduleInstallmentList ,transactionDate ,true);
                break;
            case LOAN_DUE_REPAYMENT:
                loanRepaymentScheduleInstallment = activeSchedule(loanRepaymentScheduleInstallmentList ,transactionDate ,false);
                break;

        }
        return loanRepaymentScheduleInstallment;
    }


    public LoanRepaymentScheduleInstallment activeSchedule(Collection<LoanRepaymentScheduleInstallment> loanRepaymentScheduleList , Date transactionDate , boolean allowEarlyRepayments){

        for(LoanRepaymentScheduleInstallment loanRepaymentSchedule : loanRepaymentScheduleList){

            if(!loanRepaymentSchedule.isObligationsMet()){
                if(allowEarlyRepayments) {
                    return loanRepaymentSchedule;
                }
                LocalDate dueDate = loanRepaymentSchedule.getDueDate();
                boolean isFutureTransaction = transactionDate.before(dueDate.toDate());
                if(!isFutureTransaction){
                    return loanRepaymentSchedule;
                }
            }
            return loanRepaymentSchedule;
        }
        return null ;
    }




    public LoanRepaymentScheduleInstallment dueSchedule(Collection<LoanRepaymentScheduleInstallment> loanRepaymentScheduleList ,LocalDate transactionDate){
        for(LoanRepaymentScheduleInstallment loanRepaymentSchedule : loanRepaymentScheduleList){
            if(loanRepaymentSchedule.isOverdueOn(transactionDate)){
                return loanRepaymentSchedule;
            }
        }
        return null ;
    }
}
