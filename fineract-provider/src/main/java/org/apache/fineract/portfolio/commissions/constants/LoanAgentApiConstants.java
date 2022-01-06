/*

    Created by Sinatra Gunda
    At 3:06 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoanAgentApiConstants {

    public final static String resourceNameForPermissions = "LOAN_AGENT";

    public static String clientIdParam ="clientId";

    public final static Set<String> LOAN_AGENT_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "displayName","clientAccountNo","clientId","savingsAccountNo"));




}
