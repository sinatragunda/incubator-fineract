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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment(Loan loan , LocalDate transactionDate , PAYMENT_CODE paymentCode){

        Long loanId = loan.getId();

        Collection<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallmentList = loanRepaymentScheduleInstallmentRepository.findAllByLoanId(loanId);

        Comparator<LoanRepaymentScheduleInstallment> periodComparator = (l , r)-> l.getInstallmentNumber().compareTo(r.getInstallmentNumber()) ;

        Predicate<LoanRepaymentScheduleInstallment> obligationsMet = (e)-> e.isObligationsMet();

        loanRepaymentScheduleInstallmentList = loanRepaymentScheduleInstallmentList.stream().filter(obligationsMet.negate()).sorted(periodComparator).collect(Collectors.toList());

        LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = null ;
        /**
         * Added 19/09/2022
         * Allow early repayment on loans .If false on loans whose payment date is after current date will be processed
         */
        switch (paymentCode){
            case LOAN_REPAYMENT:
                loanRepaymentScheduleInstallment = activeSchedule(loanRepaymentScheduleInstallmentList ,transactionDate.toDate() ,false);
                break;
            case LOAN_EARLY_REPAYMENT:
                loanRepaymentScheduleInstallment = activeSchedule(loanRepaymentScheduleInstallmentList ,transactionDate.toDate() ,true);
                break;

        }
        return loanRepaymentScheduleInstallment;
    }


    private LoanRepaymentScheduleInstallment activeSchedule(Collection<LoanRepaymentScheduleInstallment> loanRepaymentScheduleList , Date transactionDate , boolean allowEarlyRepayments){

        for(LoanRepaymentScheduleInstallment loanRepaymentSchedule : loanRepaymentScheduleList){

            if(!loanRepaymentSchedule.isObligationsMet()){

                boolean isPartlyPaid = loanRepaymentSchedule.isPartlyPaid();

                if(isPartlyPaid){
                    System.err.println("-------------is partly paid ------------");
                }

                if(allowEarlyRepayments) {
                    return loanRepaymentSchedule;
                }

                LocalDate dueDate = loanRepaymentSchedule.getDueDate();
                boolean isFutureTransaction = transactionDate.before(dueDate.toDate());

                if(!isFutureTransaction){
                    return loanRepaymentSchedule;
                }
            }
        }
        return null ;
    }

    private LoanRepaymentScheduleInstallment dueSchedule(Collection<LoanRepaymentScheduleInstallment> loanRepaymentScheduleList ,LocalDate transactionDate){

        for(LoanRepaymentScheduleInstallment loanRepaymentSchedule : loanRepaymentScheduleList){
            if(loanRepaymentSchedule.isOverdueOn(transactionDate)){
                return loanRepaymentSchedule;
            }
        }
        return null ;
    }
}
