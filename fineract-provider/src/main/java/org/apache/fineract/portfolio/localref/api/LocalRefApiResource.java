/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 01:37
 */
package org.apache.fineract.portfolio.localref.api;

/*

    Created by Sinatra Gunda
    At 3:06 AM on 9/6/2022

*/
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.service.LocalRefReadPlatformService;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.Collection;


@Path("/localref")
@Component
@Scope("singleton")
public class LocalRefApiResource {

    private final PlatformSecurityContext platformSecurityContext;
    private final FromJsonHelper fromJsonHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ToApiJsonSerializer<LocalRef> localRefToApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final LocalRefReadPlatformService localRefReadPlatformService;

    @Autowired
    public LocalRefApiResource(PlatformSecurityContext platformSecurityContext, FromJsonHelper fromJsonHelper, PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, ToApiJsonSerializer<LocalRef> localRefToApiJsonSerializer, ApiRequestParameterHelper apiRequestParameterHelper, LocalRefReadPlatformService localRefReadPlatformService) {
        this.platformSecurityContext = platformSecurityContext;
        this.fromJsonHelper = fromJsonHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.localRefToApiJsonSerializer = localRefToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.localRefReadPlatformService = localRefReadPlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createLocalRef() //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.localRefToApiJsonSerializer.serialize(result);
    }

    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @QueryParam("officeId") final Long officeId) {

        //this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final LocalRefData localRefData = this.localRefReadPlatformService.template(REF_TABLE.UNSPECIFIED);
        //final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.localRefToApiJsonSerializer.serialize(localRefData);
    }


    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("officeId") final Long officeId) {

        //this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        final Collection<LocalRefData> localRefDataCollection = this.localRefReadPlatformService.retrieveAll(officeId);
        //final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.localRefToApiJsonSerializer.serialize(localRefDataCollection);
    }

}
