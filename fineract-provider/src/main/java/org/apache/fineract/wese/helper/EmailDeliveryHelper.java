/*

    Created by Sinatra Gunda
    At 3:37 AM on 8/28/2021

*/
package org.apache.fineract.wese.helper;

import org.apache.fineract.wese.pojo.EmailSendStatus;
import org.apache.fineract.wese.service.EmailSession;

public class EmailDeliveryHelper {

    public static void add(EmailSendStatus emailSendStatus){
        EmailSession.getInstance().add(emailSendStatus);
    }

}
