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
package org.apache.fineract.infrastructure.dataqueries.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

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
import javax.ws.rs.QueryParam ;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.dataqueries.data.ReportData;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReportRepository;
import org.apache.fineract.infrastructure.dataqueries.helper.ScheduledReportHelper;
import org.apache.fineract.infrastructure.dataqueries.service.ReadReportingService;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;
import org.apache.fineract.infrastructure.jobs.service.SchedularWritePlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.spm.repository.EmailSendStatusRepository;
import org.apache.fineract.spm.repository.ScheduledMailSessionRepository;
import org.apache.fineract.wese.helper.ObjectNodeHelper;
import org.apache.fineract.wese.portfolio.excel.service.ExcelExporter;
import org.apache.fineract.wese.portfolio.scheduledreports.domain.EmailSendStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import org.springframework.beans.factory.annotation.Autowired;

// Added 05/9/2021
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

// Added 06/05/2021
import org.apache.fineract.wese.portfolio.scheduledreports.domain.ScheduledMailSession;


@Path("/reports")
@Component
@Scope("singleton")
public class ReportsApiResource {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "reportName", "reportType", "reportSubType",
            "reportCategory", "description", "reportSql", "coreReport", "useReport", "reportParameters"));

    private final String resourceNameForPermissions = "REPORT";
    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<ReportData> toApiJsonSerializer;
    private final ReadReportingService readReportingService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final SchedularWritePlatformService schedularWritePlatformService ;
    private final ScheduledReportRepository scheduledReportRepository ;
    private final ScheduledMailSessionRepository scheduledMailSessionRepository ;
    private final EmailSendStatusRepository emailSendStatusRepository ;

    @Autowired
    public ReportsApiResource(final PlatformSecurityContext context, final ReadReportingService readReportingService,
                              final ToApiJsonSerializer<ReportData> toApiJsonSerializer,
                              final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                              final ApiRequestParameterHelper apiRequestParameterHelper ,final SchedularWritePlatformService schedularWritePlatformService ,final ScheduledReportRepository scheduledReportRepository ,final ScheduledMailSessionRepository scheduledMailSessionRepository ,final EmailSendStatusRepository emailSendStatusRepository) {
        this.context = context;
        this.readReportingService = readReportingService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.schedularWritePlatformService = schedularWritePlatformService ;
        this.scheduledReportRepository = scheduledReportRepository;
        this.emailSendStatusRepository = emailSendStatusRepository ;
        this.scheduledMailSessionRepository = scheduledMailSessionRepository ;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveReportList(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<ReportData> result = this.readReportingService.retrieveReportList();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, result, this.RESPONSE_DATA_PARAMETERS);
    
    }

    @GET
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveReport(@PathParam("id") final Long id, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ReportData result = this.readReportingService.retrieveReport(id);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (settings.isTemplate()) {
            result.appendedTemplate(this.readReportingService.getAllowedParameters(), this.readReportingService.getAllowedReportTypes());
        }
        return this.toApiJsonSerializer.serialize(settings, result, this.RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOfficeTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ReportData result = new ReportData();
        result.appendedTemplate(this.readReportingService.getAllowedParameters(), this.readReportingService.getAllowedReportTypes());

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, result, this.RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createReport(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createReport().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    // Added 04/08/2021  Monthly Report Schedule Creater

    @POST
    @Path("/schedule")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createScheduledReport(final String apiRequestBodyAsJson) {

        Long id = schedularWritePlatformService.createScheduledReport(apiRequestBodyAsJson);
        return ObjectNodeHelper.statusNode(true).put("id" ,id).toString();
    }

    // Added 02/09/2021 .To get a list of all those scheduled jobs
    @GET
    @Path("/schedule")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<ScheduledReport> getScheduledReports() {

        List<ScheduledReport> scheduledReportList = scheduledReportRepository.findAll();

        Consumer<ScheduledReport> linkJob  = (e)->{
            Long jobId = e.getJobId();
            ScheduledJobDetail scheduledJobDetail = schedularWritePlatformService.findByJobId(jobId);
            Optional.ofNullable(scheduledJobDetail).ifPresent((a)->{
                e.setScheduledJobDetail(scheduledJobDetail);
            });
        };

        Consumer<List<ScheduledReport>> listConsumer = (e)->{
            e.stream().forEach(linkJob);
        };

        Optional.ofNullable(scheduledReportList).ifPresent(listConsumer);
        return scheduledReportList;
    }


    // Added 02/09/2021 .To get a list of all those scheduled jobs
    @GET
    @Path("/schedule/{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ScheduledMailSession viewScheduledReportSessions(@PathParam("id") Long scheduledReportId){

        System.err.println("-------------------report id is --------"+scheduledReportId);

        ScheduledReport scheduledReport = scheduledReportRepository.findOne(scheduledReportId);
        return getScheduledMailSession(scheduledReport);
    }


    private ScheduledMailSession getScheduledMailSession(ScheduledReport scheduledReport) {
        Long jobId = scheduledReport.getJobId();
        ScheduledJobDetail scheduledJobDetail = schedularWritePlatformService.findByJobId(jobId);
        scheduledReport.setScheduledJobDetail(scheduledJobDetail);
        ScheduledMailSession scheduledMailSession = ScheduledReportHelper.scheduledMailSessionResults(scheduledMailSessionRepository ,emailSendStatusRepository, scheduledReport);

        Optional.ofNullable(scheduledMailSession).ifPresent((e)->{
            scheduledMailSession.setScheduledReport(scheduledReport);
        });
        return scheduledMailSession;
    }

    // Added 05/09/2021 .New function to export excel results out
    @GET
    @Path("/schedule/{id}/{report}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response exportScheduledReportResults(@PathParam("id") Long scheduledReportId ,@PathParam("report")String reportSession){

        ScheduledReport scheduledReport = scheduledReportRepository.findOne(scheduledReportId);
        String filename = reportSession + DateUtils.getLocalDateOfTenant().toString() + ".xls";

        ScheduledMailSession scheduledMailSession = getScheduledMailSession(scheduledReport);

        Consumer<ScheduledMailSession> exportResultsConsumer = (e)->{
            List<EmailSendStatus> emailSendStatusList = new ArrayList<>();
            switch (reportSession){
                case "previous":
                    emailSendStatusList = e.getPreviousEmailSendStatusList();
                    e.setActiveEmailSendStatusList(emailSendStatusList);
                    break;
            }
        };


        // set if user wish to export previous results or active ones
        Optional.ofNullable(scheduledMailSession).ifPresent(exportResultsConsumer);
        List<EmailSendStatus> emailSendStatusList = scheduledMailSession.getActiveEmailSendStatusList();
        final ByteArrayOutputStream baos = ExcelExporter.export(emailSendStatusList ,new EmailSendStatus());

        final ResponseBuilder response = Response.ok(baos.toByteArray());
        response.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.header("Content-Type", "application/vnd.ms-excel");
        return response.build();

    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateReport(@PathParam("id") final Long id, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateReport(id).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteReport(@PathParam("id") final Long id) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteReport(id).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

}