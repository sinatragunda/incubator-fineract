/*

    Created by Sinatra Gunda
    At 2:16 PM on 6/15/2022

*/
package org.apache.fineract.portfolio.loanaccount.helper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;

import java.time.Period;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

public class NkwaziLoanAdjustmentsHelper {


    // returns new repayment times remaining
    public static int adjustLoan(String startDateArg ,LocalDate submittedDate ,Integer numberOfRepayments ,String repaymentFrequency){

        System.err.println("--------------------------------this date is -------------"+startDateArg);

        LocalDate startDate = DateUtils.parseLocalDate(startDateArg ,"dd/MM/YYYY");

        //Days diff = Days.between(startDateTime ,submittedDateTime);

        // 52 per 2 weeks right .....should we devide by 2

        System.err.println("-----------initial number of repayments is "+numberOfRepayments+"-----------for "+repaymentFrequency);

        int duration = 0 ;
        int remainingPayments = 0 ;

        switch (repaymentFrequency){

            case "Weeks":
                Weeks weeks = Weeks.weeksBetween(submittedDate ,startDate);
                duration = weeks.getWeeks();
                //System.err.println("--------------------duration is ------------------"+duration);
                remainingPayments = numberOfRepayments - (duration/2) ;
                System.err.println("---------------difference between -------"+submittedDate+"---------------and "+startDate);
                System.err.println("------------remaining payments in weeks is  --------"+remainingPayments);
                break;
            case "Months":
                Months months = Months.monthsBetween(submittedDate ,startDate);
                duration = months.getMonths();
                System.err.println("---------------difference between -------"+submittedDate+"---------------and "+startDate);
                remainingPayments = numberOfRepayments - duration ;
                System.err.println("----------remaining months in payments  ========="+remainingPayments);
                // we need to get
                break;
        }

        if(remainingPayments > numberOfRepayments){
            remainingPayments = 0;
        }

        return remainingPayments;

        //Period period = Period.between(startDateJoda ,submittedDate);
        //period.
        //LocalDate diff = startDateJoda.minus(submittedDate);


    }
}
