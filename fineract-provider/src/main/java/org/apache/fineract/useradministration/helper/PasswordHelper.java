/*

    Created by Sinatra Gunda
    At 6:26 PM on 9/30/2021

*/
package org.apache.fineract.useradministration.helper;

public class PasswordHelper {

    public static String randomAuthorizationTokenGeneration() {
        Integer randomPIN = (int) (Math.random() * 9000) + 1000;
        return randomPIN.toString();
    }

}
