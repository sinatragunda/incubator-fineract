/*

    Created by Sinatra Gunda
    At 9:18 AM on 3/1/2022

*/
package org.apache.fineract.infrastructure.security.domain;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.helper.ResetPasswordHelper;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.apache.fineract.useradministration.domain.UserDomainService;
import org.apache.fineract.useradministration.helper.ResetSelfServiceUserPassword;
import org.apache.fineract.wese.service.WeseEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {


    private WeseEmailService weseEmailService;
    private UserDomainService userDomainService;
    private AppUserRepositoryWrapper appUserRepositoryWrapper;
    private FromJsonHelper fromJsonHelper ;

    @Autowired
    public ResetPasswordServiceImpl(WeseEmailService weseEmailService, UserDomainService userDomainService, AppUserRepositoryWrapper appUserRepositoryWrapper, FromJsonHelper fromJsonHelper) {
        this.weseEmailService = weseEmailService;
        this.userDomainService = userDomainService;
        this.appUserRepositoryWrapper = appUserRepositoryWrapper;
        this.fromJsonHelper = fromJsonHelper;
    }

    @Override
    public String resetPassword(String emailAddress){
        return ResetPasswordHelper.resetPassword(weseEmailService ,userDomainService ,appUserRepositoryWrapper ,emailAddress);
    }

}
