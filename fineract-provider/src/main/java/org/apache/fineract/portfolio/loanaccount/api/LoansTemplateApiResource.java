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

/* Change log 
    23/02/2021
        changed calculateLoanSchedule function to disable param validation please see line 680 - 670


*/
package org.apache.fineract.portfolio.loanaccount.api;

import static org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations.interestType;

import java.io.InputStream;
import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.template.NkwaziLoanTemplate;
import org.apache.fineract.portfolio.loanaccount.template.service.NkwaziLoanTemplateReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.gson.JsonElement;

@Path("/loans/templates")
@Component
@Scope("singleton")
public class LoansTemplateApiResource {

    private final Set<String> LOAN_TEMPLATE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("maxAllowable", "balanceAllowable","loanFactor","isSet"));
    
    private final String resourceNameForPermissions = "LOAN";

    private final PlatformSecurityContext context;
    private final NkwaziLoanTemplateReadPlatformService nkwaziLoanTemplateReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<NkwaziLoanTemplate> nkwaziLoanTemplateDefaultToApiJsonSerializer ;

    @Autowired
    public LoansTemplateApiResource(final PlatformSecurityContext context, final NkwaziLoanTemplateReadPlatformService nkwaziLoanTemplateReadPlatformService,final DefaultToApiJsonSerializer nkwaziLoanTemplateDefaultToApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper){
        this.context = context;
        this.nkwaziLoanTemplateReadPlatformService = nkwaziLoanTemplateReadPlatformService;
        this.nkwaziLoanTemplateDefaultToApiJsonSerializer = nkwaziLoanTemplateDefaultToApiJsonSerializer ;
        this.apiRequestParameterHelper = apiRequestParameterHelper ;
    }

    /*
     To be edited to include templates for more tenants now only for Nkwazi
     */

    @GET
    @Path("{clientId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@PathParam("clientId") final Long clientId,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        NkwaziLoanTemplate nkwaziLoanTemplate = nkwaziLoanTemplateReadPlatformService.retrieveOne(clientId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.nkwaziLoanTemplateDefaultToApiJsonSerializer.serialize(settings, nkwaziLoanTemplate, this.LOAN_TEMPLATE_DATA_PARAMETERS);

    }

}