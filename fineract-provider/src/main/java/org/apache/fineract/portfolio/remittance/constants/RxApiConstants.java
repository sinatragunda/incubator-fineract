package org.apache.fineract.portfolio.remittance.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RxApiConstants {

    public static String permission = "RX";
    private static String providerParam = "provider";
    private static String identificationTypeParam = "identificationType";
    private static String savingsAccountsParam = "savingsAccount";
    private static String rxData = "rxData";

    public static final Set<String> SAVINGS_PRODUCT_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList(providerParam ,identificationTypeParam ,savingsAccountsParam ,rxData));

}
