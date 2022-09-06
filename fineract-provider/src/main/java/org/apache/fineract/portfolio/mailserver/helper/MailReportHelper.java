/*

    Created by Sinatra Gunda
    At 3:12 AM on 9/6/2022

*/
package org.apache.fineract.portfolio.mailserver.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.mifosplatform.infrastructure.report.service.PentahoReportingProcessServiceImpl;

import java.util.List;
import java.util.Map;

public class MailReportHelper {


    public static List<SEND_MAIL_MESSAGE_STATUS> generateAndSendReport(PentahoReportingProcessServiceImpl pentahoReportingProcessService , JsonCommand jsonCommand){

        Map<String,String> reportParams = jsonCommand.mapValueOfParameterNamed("reportParams");

        System.err.println("------------------------param size is "+reportParams.size());

        return null ;

    }
}
