
package org.apache.fineract.wese.helper ;

import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.service.WeseEmailService;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;

// added 27/08/2021
import javax.mail.SendFailedException;

public class ReportsEmailHelper{
	
	public static void testSend(WeseEmailService weseEmailService ,String path ,String body){

		EmailDetail emailDetail = new EmailDetail("Pdf" ,body ,"treyviis@gmail.com" ,"Nkwazi Test");
		weseEmailService.sendAttached(emailDetail ,path);
	}


	public static SEND_MAIL_MESSAGE_STATUS sendClientReport(WeseEmailService weseEmailService ,EmailDetail emailDetail ,String path){
		
		SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = weseEmailService.sendAttached(emailDetail ,path);
		return sendMailMessageStatus ;

	}
}