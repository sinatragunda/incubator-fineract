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
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.infrastructure.dataqueries.data.GenericResultsetData;
import org.apache.fineract.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.enumerations.APPLICATION_ACTION;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.helper.ApplicationTableHelper;
import org.apache.fineract.portfolio.localref.service.LocalRefReadPlatformService;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;

import org.apache.fineract.presentation.menu.domain.ShortcutEntry;
import org.apache.fineract.presentation.menu.helper.CommandShortcutHelper;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
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
    private final ToApiJsonSerializer<GenericResultsetData> toApiJsonSerializer;
    private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
    private final CommandShortcutHelper commandShortcutHelper;

    @Autowired
    public LocalRefApiResource(PlatformSecurityContext platformSecurityContext, FromJsonHelper fromJsonHelper, PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, ToApiJsonSerializer<LocalRef> localRefToApiJsonSerializer, ApiRequestParameterHelper apiRequestParameterHelper, LocalRefReadPlatformService localRefReadPlatformService ,ToApiJsonSerializer toApiJsonSerializer ,ReadWriteNonCoreDataService readWriteNonCoreDataService ,final CommandShortcutHelper commandShortcutHelper) {
        this.platformSecurityContext = platformSecurityContext;
        this.fromJsonHelper = fromJsonHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.localRefToApiJsonSerializer = localRefToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.localRefReadPlatformService = localRefReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer ;
        this.readWriteNonCoreDataService = readWriteNonCoreDataService;
        this.commandShortcutHelper = commandShortcutHelper;
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

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(@QueryParam("id")Long id , final String apiRequestBodyAsJson) {

        System.err.println("=============================edit this shit now =========");

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateLocalRef(id) //
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
        final LocalRefData localRefData = this.localRefReadPlatformService.template(REF_TABLE.HYBRID);
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

    /**
     * Added 19/02/2023 at 1103
     */ 

    @GET
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("id") final Long id) {

        //this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
        LocalRefData localRefData = this.localRefReadPlatformService.retrieveOne(id);
        return this.localRefToApiJsonSerializer.serialize(localRefData);
    }


    /**
     * Added 02/02/2023 at 1154
     * Returns application table template and data
     * If application is view ,we expert an id etc if list we list all data right .If create new we return template 
     * Modified 31/03/2023 at 0256
     * Function should also handle shortcut .
     * Function will handle shortcut firsts then fall to handle application table entries if shortcut link not found
     * If command is a shortcut command then route to corresponding link else throw error  
     */ 
    @Path("application")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String application(@Context final UriInfo uriInfo, @QueryParam("table") String tableOrShortcut ,@QueryParam("action") String action) {

        System.err.println("------------------------------------table name is "+tableOrShortcut+"-----------and action is "+action);
        //this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final ShortcutEntry shortcutEntry = this.commandShortcutHelper.shortcutEntry(tableOrShortcut);
        boolean isShortcut = OptionalHelper.isPresent(shortcutEntry);

        /**
         * Added 31/03/2023 at 0320
         * Function to be modified to remove excess ifs 
         */
        final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
        if(isShortcut){
            return this.toApiJsonSerializer.serializePretty(prettyPrint ,shortcutEntry);
        }

        APPLICATION_ACTION applicationAction = (APPLICATION_ACTION) EnumTemplateHelper.fromString(APPLICATION_ACTION.values(),action);
        DatatableData result = ApplicationTableHelper.getTable(readWriteNonCoreDataService , tableOrShortcut ,applicationAction);

        return this.toApiJsonSerializer.serializePretty(prettyPrint, result);
    
    }

}
