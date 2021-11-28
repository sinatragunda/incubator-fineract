/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 November 2021 at 02:33
 */
package org.apache.fineract.infrastructure.userinstancing.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.fineract.useradministration.domain.AppUser;

public class UserAlreadyLoggedInException extends AbstractPlatformDomainRuleException {

    public UserAlreadyLoggedInException(AppUser appUser){
        super(String.format("User % already logged in .Try logout existing instance and try again" ,appUser.getUsername()) ,String.format("User % already logged in .Try logout existing instance and try again" ,appUser.getUsername()));
    }
}
