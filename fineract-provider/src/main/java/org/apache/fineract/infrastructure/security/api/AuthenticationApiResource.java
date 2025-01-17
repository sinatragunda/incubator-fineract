/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.security.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.constants.ResetPasswordConstants;
import org.apache.fineract.infrastructure.security.constants.TwoFactorConstants;
import org.apache.fineract.infrastructure.security.data.AuthenticatedUserData;
import org.apache.fineract.infrastructure.security.domain.ResetPasswordService;
import org.apache.fineract.infrastructure.security.helper.ResetPasswordHelper;
import org.apache.fineract.infrastructure.security.service.SpringSecurityPlatformSecurityContext;
import org.apache.fineract.infrastructure.security.service.TwoFactorUtils;
import org.apache.fineract.useradministration.data.RoleData;
import org.apache.fineract.useradministration.domain.*;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.service.WeseEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.util.Base64;

@Path("/authentication")
@Component
@Profile("basicauth")
@Scope("singleton")
public class AuthenticationApiResource {

    private final DaoAuthenticationProvider customAuthenticationProvider;
    private final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService;
    private final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext;
    private final TwoFactorUtils twoFactorUtils;

    // added 01/03/2022
    private final ResetPasswordService resetPasswordService;
    private final FromJsonHelper fromJsonHelper ;

    @Autowired
    public AuthenticationApiResource(
            @Qualifier("customAuthenticationProvider") final DaoAuthenticationProvider customAuthenticationProvider,
            final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService,
            final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext, TwoFactorUtils twoFactorUtils ,ResetPasswordService resetPasswordService ,FromJsonHelper fromJsonHelper) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.springSecurityPlatformSecurityContext = springSecurityPlatformSecurityContext;
        this.twoFactorUtils = twoFactorUtils;

        // added 01/03/2022
        this.resetPasswordService = resetPasswordService;
        this.fromJsonHelper = fromJsonHelper ;
    }

    // added 01/03/2022
    @POST
    @Path("/resetpassword")
    public String resetPassword(String payload){

        System.err.println("------------------reset password son with paylod -------------"+payload);

        JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);
        String emailAddress = null ;

        if(jsonCommand.hasParameter(ResetPasswordConstants.emailAddressParam)){
            emailAddress = jsonCommand.stringValueOfParameterNamed(ResetPasswordConstants.emailAddressParam);
        }

        String response = resetPasswordService.resetPassword(emailAddress);
        //String response = ResetPasswordHelper.resetPassword(weseEmailService ,userDomainService ,appUserRepositoryWrapper ,emailAddress);
        return response;
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public String authenticate(@QueryParam("username") final String username, @QueryParam("password") final String password) {

        final Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication authenticationCheck = this.customAuthenticationProvider.authenticate(authentication);

        final Collection<String> permissions = new ArrayList<>();
        AuthenticatedUserData authenticatedUserData = new AuthenticatedUserData(username, permissions);

        if (authenticationCheck.isAuthenticated()) {
            final Collection<GrantedAuthority> authorities = new ArrayList<>(authenticationCheck.getAuthorities());
            for (final GrantedAuthority grantedAuthority : authorities) {
                permissions.add(grantedAuthority.getAuthority());
            }

            final byte[] base64EncodedAuthenticationKey = Base64.encode(username + ":" + password);

            final AppUser principal = (AppUser) authenticationCheck.getPrincipal();
            final Collection<RoleData> roles = new ArrayList<>();
            final Set<Role> userRoles = principal.getRoles();
            for (final Role role : userRoles) {
                roles.add(role.toData());
            }

            final Long officeId = principal.getOffice().getId();
            final String officeName = principal.getOffice().getName();

            final Long staffId = principal.getStaffId();
            final String staffDisplayName = principal.getStaffDisplayName();

            final EnumOptionData organisationalRole = principal.organisationalRoleData();

            boolean isTwoFactorRequired = twoFactorUtils.isTwoFactorAuthEnabled() && !
                    principal.hasSpecificPermissionTo(TwoFactorConstants.BYPASS_TWO_FACTOR_PERMISSION);
            if (this.springSecurityPlatformSecurityContext.doesPasswordHasToBeRenewed(principal)) {
                authenticatedUserData = new AuthenticatedUserData(username, principal.getId(),
                        new String(base64EncodedAuthenticationKey), isTwoFactorRequired);
            } else {

                authenticatedUserData = new AuthenticatedUserData(username, officeId, officeName, staffId, staffDisplayName,
                        organisationalRole, roles, permissions, principal.getId(),
                        new String(base64EncodedAuthenticationKey), isTwoFactorRequired);
            }

        }

        return this.apiJsonSerializerService.serialize(authenticatedUserData);
    }
}