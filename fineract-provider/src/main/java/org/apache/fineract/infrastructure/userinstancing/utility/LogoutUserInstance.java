/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 November 2021 at 02:42
 */
package org.apache.fineract.infrastructure.userinstancing.utility;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.userinstancing.exceptions.UserAlreadyLoggedInException;
import org.apache.fineract.useradministration.domain.AppUser;

import java.util.Map;

public class LogoutUserInstance implements SingleUserInstance{

    private AppUser appUser ;

    public LogoutUserInstance(AppUser appUser){
        this.appUser = appUser;
    }
    @Override
    public void execute() {

        Long userId = appUser.getId();
        FineractPlatformTenant fineractPlatformTenant = ThreadLocalContextUtil.getTenant();
        Map<Long ,UserSessionInstance> userSessionInstanceMap = fineractPlatformTenant.getUserSessionInstanceMap();

        boolean hasSession = userSessionInstanceMap.containsKey(userId);

        if(hasSession){
            userSessionInstanceMap.remove(userId);
            ThreadLocalContextUtil.setTenant(fineractPlatformTenant);
        }
    }
}
