/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 12 April 2023 at 06:53
 */
package org.apache.fineract.portfolio.loanaccount.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.utility.helper.JodaDateHelper;
import org.apache.fineract.wese.helper.JsonHelper;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;

public class LoanTransactionHelper {

    public static String buildReverseTransactionJson(LoanTransaction loanTransaction){

        LocalDate localDate = loanTransaction.getTransactionDate();
        String transactionDate = JodaDateHelper.localDateToDefaultFormat(localDate ,"dd MMMM yyyy");

        Map<String ,Object> map = new HashMap<>();
        map.put("transactionAmount" ,0);
        map.put("locale","en");
        map.put("dateFormat","dd MMMM yyyy");
        map.put("transactionDate" ,transactionDate);
        String json =  JsonHelper.serializeMapToJson(map);
        System.err.println("-------------------------serialized string is "+json);
        return json;
    }
}
