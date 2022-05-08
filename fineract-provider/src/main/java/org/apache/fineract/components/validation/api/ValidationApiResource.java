/*

    Created by Sinatra Gunda
    At 5:36 AM on 5/2/2022

*/
package main.java.org.apache.fineract.components.validation.api;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.wese.component.defaults.data.ValidationData;
import com.wese.component.defaults.service.ValidationReadPlatformService;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.components.ComponentsApiConstants;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path(ComponentsApiConstants.validationApiUrl)
@Component
@Scope("singleton")
public class ValidationApiResource {

    private final org.apache.fineract.infrastructure.security.service.PlatformSecurityContext context;
    private final ToApiJsonSerializer<ValidationData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private static final Set<String> VALIDATION_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("validationType","fieldType","name"));

    private final ValidationReadPlatformService validationReadPlatformService ;

    @Autowired
    public ValidationApiResource(final org.apache.fineract.infrastructure.security.service.PlatformSecurityContext context,
                                           final ToApiJsonSerializer<ValidationData> toApiJsonSerializer,
                                           final ApiRequestParameterHelper apiRequestParameterHelper,
                                           final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService ,final ValidationReadPlatformService validationReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.validationReadPlatformService = validationReadPlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(ComponentsApiConstants.ENTITY_NAME);

        ValidationData validationData = this.validationReadPlatformService.template();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings,validationData ,
                VALIDATION_RESPONSE_DATA_PARAMETERS);
    }
//
//    @GET
//    @Consumes({ MediaType.APPLICATION_JSON })
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String retrieveAll(@Context final UriInfo uriInfo) {
//
//        this.context.authenticatedUser().validateHasReadPermission(org.apache.fineract.infrastructure.accountnumberformat.service.AccountNumberFormatConstants.ENTITY_NAME);
//
//        final List<org.apache.fineract.infrastructure.accountnumberformat.data.AccountNumberFormatData> accountNumberFormatData = this.accountNumberFormatReadPlatformService
//                .getAllAccountNumberFormats();
//
//        final org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
//        return this.toApiJsonSerializer.serialize(settings, accountNumberFormatData,
//                ACCOUNT_NUMBER_FORMAT_RESPONSE_DATA_PARAMETERS);
//    }
//
//    @GET
//    @Path("{accountNumberFormatId}")
//    @Consumes({ MediaType.APPLICATION_JSON })
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String retrieveOne(@Context final UriInfo uriInfo, @PathParam("accountNumberFormatId") final Long accountNumberFormatId) {
//
//        this.context.authenticatedUser().validateHasReadPermission(org.apache.fineract.infrastructure.accountnumberformat.service.AccountNumberFormatConstants.ENTITY_NAME);
//
//        final org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
//
//        org.apache.fineract.infrastructure.accountnumberformat.data.AccountNumberFormatData accountNumberFormatData = this.accountNumberFormatReadPlatformService
//                .getAccountNumberFormat(accountNumberFormatId);
//        if (settings.isTemplate()) {
//            final org.apache.fineract.infrastructure.accountnumberformat.data.AccountNumberFormatData templateData = this.accountNumberFormatReadPlatformService.retrieveTemplate(org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType
//                    .fromInt(accountNumberFormatData.getAccountType().getId().intValue()));
//            accountNumberFormatData.templateOnTop(templateData.getAccountTypeOptions(), templateData.getPrefixTypeOptions());
//        }
//
//        return this.toApiJsonSerializer.serialize(settings, accountNumberFormatData,
//                ACCOUNT_NUMBER_FORMAT_RESPONSE_DATA_PARAMETERS);
//    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createValidation() //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("id") final Long id, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateValidation(id) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final org.apache.fineract.infrastructure.core.data.CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("id") final Long Id) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteValidation(Id) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

}

