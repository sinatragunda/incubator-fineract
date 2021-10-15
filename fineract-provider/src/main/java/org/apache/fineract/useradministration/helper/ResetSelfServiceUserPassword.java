/*

    Created by Sinatra Gunda
    At 6:18 PM on 9/30/2021

*/
package org.apache.fineract.useradministration.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.GmailBackedPlatformEmailService;
import org.apache.fineract.portfolio.shareproducts.data.ShareProductData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepository;
import org.apache.fineract.useradministration.exception.UserNotFoundException;
import org.apache.fineract.useradministration.service.AppUserConstants;
import org.apache.fineract.useradministration.service.AppUserWritePlatformService;
import org.apache.fineract.wese.helper.ObjectNodeHelper;

import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class ResetSelfServiceUserPassword {

    public static boolean reset(AppUserWritePlatformService appUserWritePlatformService , GmailBackedPlatformEmailService gmailBackedPlatformEmailService , FromJsonHelper fromJsonHelper , AppUser appUser){

        Long userId = appUser.getId();
        String displayName = String.format("%s %s" ,appUser.getFirstname() ,appUser.getLastname());
        String emailAddress = appUser.getEmail();
        String newPassword = PasswordHelper.randomAuthorizationTokenGeneration();
        Long officeId = appUser.getOffice().getId();
        String username = appUser.getUsername();
        //appUser.getR


        ObjectNode node = ObjectNodeHelper.objectNode();
        node.put("password" ,newPassword);
        node.put("repeatPassword",newPassword);
        node.put("sendPasswordToEmail",true);
        //node.put("username",username);
        //node.put("officeId",officeId);
        //node.put("email" ,emailAddress);
        node.put(AppUserConstants.IS_SELF_SERVICE_USER ,true);


        //ObjectArray rolesArray = ObjectNodeHelper.arrayNode();
        //node.put("roles",);

        System.err.println("-------new password is ------------------"+newPassword);

        Gson gson = new Gson();

        String jsonBody = node.toString();

        JsonElement jsonElement = gson.fromJson(jsonBody ,JsonElement.class);


        JsonCommand jsonCommand = JsonCommand.fromJsonElement(new Long(1) ,jsonElement ,fromJsonHelper);

        System.err.println("-------------------------------json command initialized now ------");
        
        appUserWritePlatformService.updateUser(userId ,jsonCommand);


        System.err.println("----------------update client data ---------------");

        // send email notification now son
        String subject ="Password Reset";
        String body = String.format("You have requested a password reset .Your new password is now %s\n .You can change it anytime when you have logged in. ",newPassword);

        EmailDetail emailDetail = new EmailDetail(subject ,body ,emailAddress ,displayName);
        try{
            gmailBackedPlatformEmailService.sendDefinedEmail(emailDetail);
        }
        catch (Exception e){
            System.err.println("--------------failed to send mail exception ");
            return false ;
        }


        System.err.println("-------------------sending password to client now son ,we should do lol------------");

        return true ;
    }


    public static String resetPassword(AppUserRepository appUserRepository, AppUserWritePlatformService appUserWritePlatformService , GmailBackedPlatformEmailService gmailBackedPlatformEmailService ,FromJsonHelper fromJsonHelper ,String username){
        
        // find this username and send reset link there
        // this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions, userId);

        AppUser appUser = appUserRepository.findAppUserByName(username);

        boolean isPresent = Optional.ofNullable(appUser).isPresent();

        if(!isPresent){
            throw new UserNotFoundException(username);
        }

        System.err.println("-------------------lets reset password son-----------");

        ResetSelfServiceUserPassword.reset(appUserWritePlatformService , gmailBackedPlatformEmailService ,fromJsonHelper, appUser);
        // to return something here
        return null;
        
    }


}
