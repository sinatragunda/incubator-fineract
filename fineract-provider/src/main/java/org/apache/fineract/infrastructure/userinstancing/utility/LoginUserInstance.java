/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 28 November 2021 at 02:26
 */
package org.apache.fineract.infrastructure.userinstancing.utility;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.userinstancing.exceptions.UserAlreadyLoggedInException;
import org.apache.fineract.useradministration.domain.AppUser;

import java.util.Map;

public class LoginUserInstance implements SingleUserInstance{

    private AppUser appUser ;

    public LoginUserInstance(AppUser appUser){
        this.appUser = appUser;
    }

    @Override
    public void execute() {

        Long userId = appUser.getId();
        FineractPlatformTenant fineractPlatformTenant = ThreadLocalContextUtil.getTenant();
        String tenant = fineractPlatformTenant.getTenantIdentifier();

        Map<String ,Map<Long ,UserSessionInstance>> tenantSessionMap = fineractPlatformTenant.getUserSessionInstanceMap();

        //System.err.println("---------------teant session map initial size is "+tenantSessionMap.size());

        Map<Long ,UserSessionInstance> sessionInstanceMap = tenantSessionMap.get(tenant);

        boolean hasSession = sessionInstanceMap.containsKey(userId);

        //System.err.println("--------------user has session ? "+hasSession);

        if(hasSession){
           throw new UserAlreadyLoggedInException(appUser);
        }

        UserSessionInstance userSessionInstance = new UserSessionInstance(appUser);
        sessionInstanceMap.put(userId ,userSessionInstance);
        tenantSessionMap.replace(tenant ,sessionInstanceMap);
        //ThreadLocalContextUtil.setTenant(fineractPlatformTenant);
        //System.err.println("-------------session instance record update "+sessionInstanceMap.size());
    }
}
