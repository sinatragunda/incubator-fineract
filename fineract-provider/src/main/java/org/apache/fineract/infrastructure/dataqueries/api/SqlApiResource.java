/*

    Created by Sinatra Gunda
    At 12:22 PM on 9/30/2022

*/
package org.apache.fineract.infrastructure.dataqueries.api;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;


import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.dataqueries.data.GenericResultsetData;
import org.apache.fineract.infrastructure.dataqueries.data.ReportData;
import org.apache.fineract.infrastructure.dataqueries.service.ReadReportingService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/sqlrun")
@Component
@Scope("singleton")
public class SqlApiResource {

    private String resourceNameForPermissions = "READ_SQL";
    private final PlatformSecurityContext context ;
    private final JdbcTemplate jdbcTemplate;
    private final ToApiJsonSerializer<ReportData> toApiJsonSerializer ;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>();
    private final ReadReportingService readReportingService ;
    private final FromJsonHelper fromJsonHelper;

    @Autowired
    public SqlApiResource(PlatformSecurityContext context, final RoutingDataSource routingDataSource, ToApiJsonSerializer toApiJsonSerializer, ApiRequestParameterHelper apiRequestParameterHelper,final ReadReportingService readReportingService ,final FromJsonHelper fromJsonHelper) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.readReportingService = readReportingService;
        this.fromJsonHelper = fromJsonHelper;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response retrieveReportList(@Context final UriInfo uriInfo ,final String sqlQuery) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

        JsonElement jsonElement = fromJsonHelper.parse(sqlQuery);
        String sql = fromJsonHelper.extractStringNamed("sql",jsonElement);

        final GenericResultsetData result = this.readReportingService.retrieveGenericResultset(sql);

        String json = this.toApiJsonSerializer.serializePretty(prettyPrint, result);
        return Response.ok().entity(json).type(MediaType.APPLICATION_JSON).build();

    }
}
