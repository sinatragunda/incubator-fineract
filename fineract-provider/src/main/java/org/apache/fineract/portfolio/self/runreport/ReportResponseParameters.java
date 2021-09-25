/*

    Created by Sinatra Gunda
    At 8:42 AM on 9/25/2021

*/
package org.apache.fineract.portfolio.self.runreport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReportResponseParameters {

    public static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "reportName", "reportType", "reportSubType",
            "reportCategory", "description", "reportSql", "coreReport", "useReport", "reportParameters"));


}
