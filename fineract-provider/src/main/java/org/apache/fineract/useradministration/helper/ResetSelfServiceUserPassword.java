/*

    Created by Sinatra Gunda
    At 6:18 PM on 9/30/2021

*/
package org.apache.fineract.useradministration.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.core.service.GmailBackedPlatformEmailService;
import org.apache.fineract.portfolio.shareproducts.data.ShareProductData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.service.AppUserWritePlatformService;
import org.apache.fineract.wese.helper.ObjectNodeHelper;

import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class ResetSelfServiceUserPassword {

    public static void reset(AppUserWritePlatformService appUserWritePlatformService , GmailBackedPlatformEmailService gmailBackedPlatformEmailService , AppUser appUser){

        Long userId = appUser.getId();
        String displayName = String.format("%s %s" ,appUser.getFirstname() ,appUser.getLastname());
        String emailAddress = appUser.getEmail();
        String newPassword = PasswordHelper.randomAuthorizationTokenGeneration();

        ObjectNode node = ObjectNodeHelper.objectNode();
        node.put("password" ,newPassword);

        Gson gson = new Gson();
        String jsonBody = node.toString();
        JsonElement jsonElement = gson.fromJson(jsonBody ,JsonElement.class);

        JsonCommand jsonCommand = JsonCommand.fromJsonElement(new Long(1) ,jsonElement);
        appUserWritePlatformService.updateUser(userId ,jsonCommand);

        // send email notification now son
        String subject ="Password Reset";
        String body = String.format("You have requested a password reset .Your new password is now %s\n .You can change it anytime when you have logged in. ",newPassword);

        EmailDetail emailDetail = new EmailDetail(subject ,body ,emailAddress ,displayName);
        gmailBackedPlatformEmailService.sendDefinedEmail(emailDetail);
    }
}
