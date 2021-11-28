/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 November 2021 at 02:22
 */
package org.apache.fineract.infrastructure.userinstancing.enumerations;

import org.apache.fineract.infrastructure.userinstancing.utility.LoginUserInstance;
import org.apache.fineract.infrastructure.userinstancing.utility.LogoutUserInstance;
import org.apache.fineract.infrastructure.userinstancing.utility.SingleUserInstance;
import org.apache.fineract.useradministration.domain.AppUser;

public enum USER_INSTANCE_ACTION {
    LOGIN,
    LOGOUT,
    TIMEOUT;


    public void execute(AppUser appUser){

        SingleUserInstance singleUserInstance = null ;
        switch (this){
            case LOGIN:
                singleUserInstance = new LoginUserInstance(appUser);
                break;
            case LOGOUT:
                singleUserInstance = new LogoutUserInstance(appUser);
                break;
            /**
             * Added 28/11/2022 at 0251
             * No action defined for timeouts now
             */
            case TIMEOUT:
                break;
        }

        singleUserInstance.execute();
    }
}
