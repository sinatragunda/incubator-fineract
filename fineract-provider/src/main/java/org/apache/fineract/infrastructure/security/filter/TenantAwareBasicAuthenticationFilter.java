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
package org.apache.fineract.infrastructure.security.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.StopWatch;
import org.apache.fineract.ApiConfig;
import org.apache.fineract.infrastructure.cache.domain.CacheType;
import org.apache.fineract.infrastructure.cache.service.CacheWritePlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.security.data.PlatformRequestLog;
import org.apache.fineract.infrastructure.security.exception.InvalidTenantIdentiferException;
import org.apache.fineract.infrastructure.security.service.BasicAuthTenantDetailsService;
import org.apache.fineract.notification.service.NotificationReadPlatformService;
import org.apache.fineract.portfolio.products.helper.ProductHelper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;

/**
 * A customised version of spring security's {@link BasicAuthenticationFilter}.
 * 
 * This filter is responsible for extracting multi-tenant and basic auth
 * credentials from the request and checking that the details provided are
 * valid.
 * 
 * If multi-tenant and basic auth credentials are valid, the details of the
 * tenant are stored in {@link FineractPlatformTenant} and stored in a
 * {@link ThreadLocal} variable for this request using
 * {@link ThreadLocalContextUtil}.
 * 
 * If multi-tenant and basic auth credentials are invalid, a http error response
 * is returned.
 */
@Service(value = "basicAuthenticationProcessingFilter")
@Profile("basicauth")
public class TenantAwareBasicAuthenticationFilter extends BasicAuthenticationFilter {

    private static boolean firstRequestProcessed = false;
    private final static Logger logger = LoggerFactory.getLogger(TenantAwareBasicAuthenticationFilter.class);

    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;
    private final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer;
    private final ConfigurationDomainService configurationDomainService;
    private final CacheWritePlatformService cacheWritePlatformService;
    private final NotificationReadPlatformService notificationReadPlatformService;
    private final String tenantRequestHeader = "Fineract-Platform-TenantId";
    private final boolean exceptionIfHeaderMissing = true;

    @Autowired
    public TenantAwareBasicAuthenticationFilter(final AuthenticationManager authenticationManager,
            final AuthenticationEntryPoint authenticationEntryPoint, final BasicAuthTenantDetailsService basicAuthTenantDetailsService,
            final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer, final ConfigurationDomainService configurationDomainService,
            final CacheWritePlatformService cacheWritePlatformService,
            final NotificationReadPlatformService notificationReadPlatformService) {
        super(authenticationManager, authenticationEntryPoint);
        this.basicAuthTenantDetailsService = basicAuthTenantDetailsService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.configurationDomainService = configurationDomainService;
        this.cacheWritePlatformService = cacheWritePlatformService;
        this.notificationReadPlatformService = notificationReadPlatformService;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final StopWatch task = new StopWatch();
        task.start();

        try {

            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                // ignore to allow 'preflight' requests from AJAX applications
                // in different origin (domain name)
            } else {

                String tenantIdentifier = request.getHeader(this.tenantRequestHeader);

                if (org.apache.commons.lang.StringUtils.isBlank(tenantIdentifier)) {
                    tenantIdentifier = request.getParameter("tenantIdentifier");
                }

                if (tenantIdentifier == null && this.exceptionIfHeaderMissing) { throw new InvalidTenantIdentiferException(
                        "No tenant identifier found: Add request header of '" + this.tenantRequestHeader
                                + "' or add the parameter 'tenantIdentifier' to query string of request URL."); }

                String pathInfo = request.getRequestURI();

                boolean isReportRequest = false;
                if (pathInfo != null && pathInfo.contains("report")) {
                    isReportRequest = true;
                }

                final FineractPlatformTenant tenant = this.basicAuthTenantDetailsService.loadTenantById(tenantIdentifier, isReportRequest);

                ThreadLocalContextUtil.setTenant(tenant);

                String authToken = request.getHeader("Authorization");

                if (authToken != null && authToken.startsWith("Basic ")) {
                    ThreadLocalContextUtil.setAuthToken(authToken.replaceFirst("Basic ", ""));
                }

                if (!firstRequestProcessed) {

                    final String baseUrl = request.getRequestURL().toString().replace(request.getPathInfo(), "/");
                    System.setProperty("baseUrl", baseUrl);
                    final boolean ehcacheEnabled = this.configurationDomainService.isEhcacheEnabled();
                    if (ehcacheEnabled) {
                        this.cacheWritePlatformService.switchToCache(CacheType.SINGLE_NODE);
                    } else {
                        this.cacheWritePlatformService.switchToCache(CacheType.NO_CACHE);
                    }
                    passwordResetRequest(pathInfo,response);
                    TenantAwareBasicAuthenticationFilter.firstRequestProcessed = true;
                }
            }
            super.doFilter(req, res, chain);
        } catch (final InvalidTenantIdentiferException e) {
            // deal with exception at low level

            SecurityContextHolder.getContext().setAuthentication(null);
            response.addHeader("WWW-Authenticate", "Basic realm=\"" + "Fineract Platform API" + "\"");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            task.stop();
            final PlatformRequestLog log = PlatformRequestLog.from(task, request);
            logger.info(this.toApiJsonSerializer.serialize(log));
        }
    }
    
    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request,
    		HttpServletResponse response, Authentication authResult)
    		throws IOException {
    	super.onSuccessfulAuthentication(request, response, authResult);

    	/// what is happening here man ?
    	AppUser user = (AppUser) authResult.getPrincipal();

        if (notificationReadPlatformService.hasUnreadNotifications(user.getId())) {
            response.addHeader("X-Notification-Refresh", "true");
        } else {
            response.addHeader("X-Notification-Refresh", "false");
        }
		
		String pathURL = request.getRequestURI();

		boolean isSelfServiceRequest[] = {false};

        Optional.ofNullable(pathURL).ifPresent(e->{
            isSelfServiceRequest[0] = (pathURL != null && pathURL.contains("/self/"));
            if(!isSelfServiceRequest[0]){
                // check again if its not selfservic request
                isSelfServiceRequest[0] = pathURL.contains("selfservice");
            }
        });


		// logic here a bit complex to comprehend with all the logical and ors
		boolean notAllowed = ((isSelfServiceRequest[0] && !user.isSelfServiceUser())
				||(!isSelfServiceRequest[0]));


		// Added 19/12/2021
		// handle all request regarding any of the financial services ,if its disabled then stop transaction or residerent it
        // No way to add interceptor other than this thats why
        // Should it return anything ,if product is disabled just throw an error from there
        // substitute this command for later
        // ProductHelper.handleRequest(request ,response);
		
		if(notAllowed){
	            // we removing this step so that we can implement our self service item
            boolean isPasswordRequest = passwordResetRequest(pathURL ,response);

            // easy to bypass isSelfServiceRequest here to just proceed
            if(isPasswordRequest){
                return ;
            }
            /// we had blocked this there other day
            /// toxic logic that needs reworking here
		    ///throw new BadCredentialsException("User not authorised to use the requested resource.");
		}
    }


    public static boolean passwordResetRequest(String url ,HttpServletResponse response){

        boolean isPasswordRequest = url.contains("resetpassword");

        if(isPasswordRequest){
            try{
                System.err.println("------------------redirect this shit ");
                response.sendRedirect("/authenticate/resetpassword");
                // we dont have any user object so trying to find a quick rich way to get clever
                response.setStatus(503);

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return isPasswordRequest;
    }
}