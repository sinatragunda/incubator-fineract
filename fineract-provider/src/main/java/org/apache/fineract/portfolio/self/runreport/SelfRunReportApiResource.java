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
package org.apache.fineract.portfolio.self.runreport;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.dataqueries.api.RunreportsApiResource;
import org.apache.fineract.infrastructure.dataqueries.data.ReportData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.service.PermissionReadPlatformService;
import org.apache.fineract.useradministration.service.RoleReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Path("/self/runreports")
@Component
@Scope("singleton")
public class SelfRunReportApiResource {

    private final PlatformSecurityContext context;
    private final RunreportsApiResource runreportsApiResource;
    private final RoleReadPlatformService roleReadPlatformService;
    private final PermissionReadPlatformService permissionReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<ReportData> toApiJsonSerializer;


    @Autowired
    public SelfRunReportApiResource(final PlatformSecurityContext context, final RunreportsApiResource runreportsApiResource ,final  RoleReadPlatformService roleReadPlatformService ,final PermissionReadPlatformService permissionReadPlatformService ,final ApiRequestParameterHelper apiRequestParameterHelper ,final ToApiJsonSerializer<ReportData> toApiJsonSerializer){
        this.context = context;
        this.runreportsApiResource = runreportsApiResource;
        this.roleReadPlatformService = roleReadPlatformService ;
        this.permissionReadPlatformService = permissionReadPlatformService ;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer ;

    }

    // Get All reports the user is authorized to view now
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getAllReports(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        System.err.println("-----------------get list of reports ----------------------------");

        Collection<ReportData> reportDataCollection = PermissionsReportHelper.selfServiceReports(permissionReadPlatformService , roleReadPlatformService ,context);

        System.err.println("----------------we have these reports now -------------------------"+reportDataCollection.size());

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, reportDataCollection, ReportResponseParameters.RESPONSE_DATA_PARAMETERS);
        //return reportDataCollection.toString();
    }

    @GET
    @Path("{reportName}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON, "text/csv", "application/vnd.ms-excel", "application/pdf", "text/html" })
    public Response runReport(@PathParam("reportName") final String reportName, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser();
        return this.runreportsApiResource.runReport(reportName, uriInfo);
    }

}
