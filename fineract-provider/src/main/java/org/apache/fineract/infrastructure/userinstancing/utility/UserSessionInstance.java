/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 November 2021 at 02:32
 */
package org.apache.fineract.infrastructure.userinstancing.utility;

import org.apache.fineract.useradministration.domain.AppUser;

import java.time.Instant;

public class UserSessionInstance {

    private Long timestamp ;
    private AppUser appUser;

    public UserSessionInstance(AppUser appUser){
        this.appUser = appUser;
        this.timestamp = Instant.now().getEpochSecond();
    }

    public AppUser getAppUser() {
        return appUser;
    }
}
