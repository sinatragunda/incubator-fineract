/*

    Created by Sinatra Gunda
    At 7:52 AM on 7/25/2022

*/
package org.apache.fineract.portfolio.savings.helper;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalDate ;

public class SavingsAccountTransactionHelper {


    public static BigDecimal openingBalanceAtSpecificDate(SavingsAccount savingsAccount ,Date startDate){

        /// get balance ,if you want balance before you will have to subtract your current amount

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(startDate);
//        int lastDay = calendar.getActualMinimum(Calendar.DATE);

        // no need for this since start day is always startday .....
        Long epoch = startDate.getTime();

        LocalDate localDate = new LocalDate(epoch);

        BigDecimal openingBalance = savingsAccount.findOpeningBalanceLastTransaction(localDate);

        return openingBalance;
    }
}
