/*

    Created by Sinatra Gunda
    At 3:37 AM on 8/28/2021

*/
package org.apache.fineract.wese.service;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.wese.pojo.EmailSendStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map ;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EmailSession {

    private static EmailSession instance = null;
    private String tenantIdentifier ;
    private List<EmailSendStatus> emailSendStatusList;
    private Map<String ,List<EmailSendStatus>> emailSendListMap ;


    public static EmailSession getInstance(){
        return Optional.ofNullable(instance).orElseGet(EmailSession::new);
    }

    public EmailSession(){

        emailSendListMap = new HashMap<>();
        instance = this;
        System.err.println("------------we have initialized new object son");

    }

    public void add(EmailSendStatus emailSendStatus){

        String tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();

    }

    public List<EmailSendStatus> getEmailSendStatusList(String tenantIdentifier){

        return null ;

    }
}
