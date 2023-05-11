/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 May 2023 at 14:07
 */
package org.apache.fineract.portfolio.loanaccount.loanschedule.helper;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.service.CustomLoanSchedulerAssembler;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LoanRepaymentScheduleCheatSheet {

    public static void cheat(LoanRepaymentScheduleInstallmentRepository loanRepaymentScheduleInstallmentRepository, Loan loan , JsonCommand jsonCommand){

        CustomLoanSchedulerAssembler customLoanSchedulerAssembler = new CustomLoanSchedulerAssembler();

        List<LoanRepaymentScheduleInstallment> loanRepaymentScheduleInstallmentList = customLoanSchedulerAssembler.assembleFromJson(loan ,jsonCommand);

        boolean isEmpty  = loanRepaymentScheduleInstallmentList.isEmpty();

        if(isEmpty){
            return ; 
        }

        List<LoanRepaymentScheduleInstallment> installments = loan.getRepaymentScheduleInstallments();

        System.err.println("-------------this loan has these streams  "+installments.size());

        System.err.println("----------inserted custom schedule has "+loanRepaymentScheduleInstallmentList.size());

        Consumer<LoanRepaymentScheduleInstallment> updateConsumer = (e)-> {

            Integer period = e.getInstallmentNumber();

            System.err.println("----------installement for loan with " + e.getLoan().getId() + "--------for installment number " + period);

            Predicate<LoanRepaymentScheduleInstallment> atPeriod = (p) -> p.getInstallmentNumber().equals(period);

            Optional<LoanRepaymentScheduleInstallment> installmentOptional = loanRepaymentScheduleInstallmentList.stream().filter(atPeriod).findFirst();

            boolean has = installmentOptional.isPresent();

            System.err.println("-------------has an installment for period ?"+period+" and has -----"+has);

            MonetaryCurrency monetaryCurrency = loan.getCurrency();
            if(has){
                System.err.println("-------------update cheat sheet ,we have an installment --------");
                LoanRepaymentScheduleInstallment installment = installmentOptional.get();
                System.err.println("-------------current end is ");
                e.updateFromAnotherInstallment(installment ,monetaryCurrency);
            }
        };

        Consumer<LoanRepaymentScheduleInstallment> saveConsumer = (e)->{
            System.err.println("----------------save and flush ,does it have id  ?"+e.getId());
            loanRepaymentScheduleInstallmentRepository.saveAndFlush(e);

            System.err.println("---------------after flushing what do we have ?   ?"+e.getId());
        };

        installments.stream().forEach(updateConsumer.andThen(saveConsumer));

    }


}
