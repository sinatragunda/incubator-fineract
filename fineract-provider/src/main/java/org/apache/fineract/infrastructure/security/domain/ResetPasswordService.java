/*

    Created by Sinatra Gunda
    At 9:17 AM on 3/1/2022

*/
package org.apache.fineract.infrastructure.security.domain;

public interface ResetPasswordService {
    String resetPassword(String emailAddress);
}
