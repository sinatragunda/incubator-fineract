
package org.apache.fineract.wese.helper ;

import org.apache.fineract.wese.service.WeseEmailService;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;


public class ReportsEmailHelper{
	
	public static void testSend(WeseEmailService weseEmailService ,String path){

		EmailDetail emailDetail = new EmailDetail("Pdf" ,"Test" ,"treyviis@gmail.com" ,"Nkwazi Test");
		weseEmailService.sendAttached(emailDetail ,path ,"Testing Pdf Attachments");

	}
}