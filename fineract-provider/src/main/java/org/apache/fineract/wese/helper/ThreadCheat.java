/*

    Created by Sinatra Gunda
    At 11:19 AM on 9/6/2021

*/
package org.apache.fineract.wese.helper;

import java.util.Map;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

public class ThreadCheat {

    public static boolean raise = false ;
    private static Map<String ,AppUser> currentUser = new HashMap<>();


    public static synchronized void raise(boolean shouldRaise){
        raise = shouldRaise ;
        //System.err.println("-------------------------steal thread now---------");
        if(shouldRaise){
            final SecurityContext context = SecurityContextHolder.getContext();
            if (context != null){
                final Authentication auth = context.getAuthentication();
                if (auth != null) {
                    AppUser currentUser = (AppUser) auth.getPrincipal();
                    stealUser(currentUser);
                   // System.err.println("--------stealing user-----------");
                }
            }

        }
    }

    public static synchronized boolean isRaise(){
        return raise ;
    }

    public static void stealUser(AppUser appUser){
        String tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        //System.err.println("--------------get tenant ------------------"+tenant);
        currentUser.put(tenant ,appUser);
    }

    public static AppUser getStolenUser(){
        String tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        //System.err.println("--------------------we have a tenant --------------------"+tenant);

        return currentUser.get(tenant);
    }
}
