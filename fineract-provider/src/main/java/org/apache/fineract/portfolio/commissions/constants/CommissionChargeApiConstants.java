/*

    Created by Sinatra Gunda
    At 12:29 PM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommissionChargeApiConstants {


    public final static String resourceNameForPermissions = "COMMISSION_CHARGE";


    public final static Set<String> COMMISSION_CHARGE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name", "amount", "currency", "active",
            "chargeAppliesTo", "chargeTimeType", "chargeCalculationType", "chargeCalculationTypeOptions", "chargeAppliesToOptions",
            "chargeTimeTypeOptions", "currencyOptions"));


    public static String chargeAppliesToParam ="chargeAppliesTo";
    public static String currencyCodeParam = "currencyCode";
    public static String chargeCalculationTypeParam	= "chargeCalculationType";
    public static String amountParam = "amount";
    public static String nameParam	= "name";
    public static String chargeTimeTypeParam	 = "chargeTimeType";
    public static String isActiveParam = "active";

}
