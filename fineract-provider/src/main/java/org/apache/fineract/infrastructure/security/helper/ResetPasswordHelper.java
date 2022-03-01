/*

    Created by Sinatra Gunda
    At 8:20 AM on 3/1/2022

*/
package org.apache.fineract.infrastructure.security.helper;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.apache.fineract.useradministration.domain.UserDomainService;
import org.apache.fineract.useradministration.helper.PasswordHelper;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.helper.ObjectNodeHelper;
import org.apache.fineract.wese.service.WeseEmailService;

import java.util.Optional;

public class ResetPasswordHelper{

    public static String resetPassword(WeseEmailService weseEmailService, UserDomainService userDomainService ,AppUserRepositoryWrapper appUserRepositoryWrapper , String emailAddress){

        boolean isEmailPresent = Optional.ofNullable(emailAddress).isPresent();

        SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus[] = {SEND_MAIL_MESSAGE_STATUS.INVALID_ADDRESS};

        if(!isEmailPresent){
            return ObjectNodeHelper.statusNode(false).put("message","Invalid email address").toString();
        }

        AppUser appUser = appUserRepositoryWrapper.fetchByEmail(emailAddress);

        Optional.ofNullable(appUser).ifPresent(e->{

            userDomainService.create(appUser ,false);

            String unencodedPassword = userDomainService.unencodedPassword();

            System.err.println("-------------------new password is ----------------"+unencodedPassword);

            String contact = String.format("%s %s",appUser.getFirstname() ,appUser.getFirstname());
            String message = String.format("You have requested for a password request ,your new instant token is %\n .When logged in you can then change your own password again",unencodedPassword);
            String subject = "Password Auto Change and Reset";

            EmailDetail emailDetail = new EmailDetail(subject ,message ,emailAddress ,contact);
            sendMailMessageStatus[0] = weseEmailService.send(emailDetail);

        });

        return ObjectNodeHelper.statusNode(true).put("status",sendMailMessageStatus[0].name()).toString();
    }

}
