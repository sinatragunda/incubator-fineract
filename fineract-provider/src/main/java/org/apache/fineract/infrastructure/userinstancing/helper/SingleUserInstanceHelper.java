/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 November 2021 at 02:48
 */
package org.apache.fineract.infrastructure.userinstancing.helper;

import org.apache.fineract.infrastructure.userinstancing.enumerations.USER_INSTANCE_ACTION;
import org.apache.fineract.useradministration.domain.AppUser;

public class SingleUserInstanceHelper {

    public void execute(AppUser appUser , USER_INSTANCE_ACTION userInstanceAction){

        userInstanceAction.execute(appUser);

    }
}
