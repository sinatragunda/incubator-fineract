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
// created 27/03/2022
package org.apache.fineract.portfolio.shareaccounts.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountDividendData;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountDividendReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountTransactionWritePlatformService;
import org.apache.fineract.portfolio.shareproducts.data.ShareProductDividendPayOutData;
import org.apache.fineract.portfolio.shareproducts.service.ShareProductDividendReadPlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/shareaccounttransaction/{transactionId}")
@Component
@Scope("singleton")
public class ShareAccountTransactionApiResource {

    private final DefaultToApiJsonSerializer<ShareProductDividendPayOutData> toApiJsonSerializer;
    private final PlatformSecurityContext platformSecurityContext;

    private final ShareAccountTransactionWritePlatformService shareAccountTransactionWritePlatformService;
    private final FromJsonHelper fromJsonHelper ;
    private final String resourceNameForPermissions = "REVERSE_SHAREACCOUNTTRANSACTION";

    @Autowired
    public ShareAccountTransactionApiResource(final DefaultToApiJsonSerializer<ShareProductDividendPayOutData> toApiJsonSerializer,
            final PlatformSecurityContext platformSecurityContext,
            final DefaultToApiJsonSerializer<ShareAccountDividendData> toApiDividendsJsonSerializer,
                                    final FromJsonHelper fromJsonHelper ,final ShareAccountTransactionWritePlatformService shareAccountTransactionWritePlatformService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.platformSecurityContext = platformSecurityContext;
        this.fromJsonHelper = fromJsonHelper ;
        this.shareAccountTransactionWritePlatformService = shareAccountTransactionWritePlatformService ;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String reverseTransaction(@PathParam("transactionId") final Long transactionId, @QueryParam("command") final String command,final String payload) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        JsonCommand jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);
        CommandProcessingResult commandProcessingResult = shareAccountTransactionWritePlatformService.updateShareAccountTransaction(transactionId ,command ,jsonCommand);
        return this.toApiJsonSerializer.serialize(commandProcessingResult);
    }


}
