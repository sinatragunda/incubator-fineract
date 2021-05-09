/*
	Added 01/02/2021


*/

package org.apache.fineract.wese.helper ;

import java.util.Random ;
import java.nio.charset.Charset;

public class SessionIDGenerator{


	public static String sessionId(){

		Random random = new Random(System.currentTimeMillis());
		long id = random.nextLong();

		byte b[] = new byte[5];
		new Random().nextBytes(b);
		String randomSalt = new String( b,Charset.forName("UTF-8"));

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(id);

		return stringBuilder.toString();

	}


}