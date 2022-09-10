/*

    Created by Sinatra Gunda
    At 12:30 PM on 9/7/2022

*/
package org.apache.fineract.accounting.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TransactionCodeConstants {

    public static final String codeParam ="code";
    public static final String nameParam ="name";
    public static final String creditAccountParam ="creditAccountId";
    public static final String debitAccountParam ="debitAccountId";

    public static final Set<String> TRANSACTION_CODE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id","name","code","debitAccountId","debitAccountName","creditAccountId","creditAccountName"));
}
