
package org.apache.fineract.wese.helper ;

import org.apache.fineract.wese.service.WeseEmailService;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;


public class ReportsEmailHelper{
	
	public static void testSend(WeseEmailService weseEmailService ,String path ,String body){

		EmailDetail emailDetail = new EmailDetail("Pdf" ,body ,"treyviis@gmail.com" ,"Nkwazi Test");
		weseEmailService.sendAttached(emailDetail ,path ,"Testing Pdf Attachments");
	}


	public static boolean sendClientReport(WeseEmailService weseEmailService ,EmailDetail emailDetail ,String path ,String description){
		
		boolean hasSent = false;
		try{
			weseEmailService.sendAttached(emailDetail ,path ,description);
			hasSent = true;
		}
		catch(Exception n){
			hasSent = false;
			System.err.println("------------------failed to send email for -------"+emailDetail.getContactName());
			n.printStackTrace();
		}
		return hasSent ;
	}
}