/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 13:01
 */
package org.apache.fineract.presentation.menu.api;


import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.presentation.menu.data.MenuData;
import org.apache.fineract.presentation.menu.data.MenuItemData;
import org.apache.fineract.presentation.menu.service.MenuItemReadPlatformService;
import org.apache.fineract.presentation.menu.service.MenuReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

@Path("/menu")
@Component
@Scope("singleton")
public class MenuApi {

    private PlatformSecurityContext context;
    private ApiRequestParameterHelper apiRequestParameterHelper;
    private MenuItemReadPlatformService menuItemReadPlatformService;
    private ToApiJsonSerializer<MenuItemData> menuItemDataToApiJsonSerializer;
    private ToApiJsonSerializer<MenuData> menuDataToApiJsonSerializer;
    private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
    private MenuReadPlatformService menuReadPlatformService;


    @Autowired
    public MenuApi(PlatformSecurityContext context, ApiRequestParameterHelper apiRequestParameterHelper, MenuItemReadPlatformService menuItemReadPlatformService, ToApiJsonSerializer<MenuItemData> menuItemDataToApiJsonSerializer ,final  PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService ,final MenuReadPlatformService menuReadPlatformService ,final ToApiJsonSerializer<MenuData> menuDataToApiJsonSerializer) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.menuItemReadPlatformService = menuItemReadPlatformService;
        this.menuItemDataToApiJsonSerializer = menuItemDataToApiJsonSerializer;
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
        this.menuReadPlatformService = menuReadPlatformService;
        this.menuDataToApiJsonSerializer = menuDataToApiJsonSerializer;
    }

    @Path("/template")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String menuTemplate(@Context final UriInfo uriInfo){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        final MenuData menuData = menuReadPlatformService.template();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.menuDataToApiJsonSerializer.serialize(settings, menuData);
    }


    @Path("/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveMenu(@PathParam("id")Long id ,@Context final UriInfo uriInfo){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        final MenuData menuData = (MenuData) menuReadPlatformService.retrieveOne(id);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.menuDataToApiJsonSerializer.serialize(settings, menuData);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllMenus(@Context final UriInfo uriInfo){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        final Collection<MenuData> menuDataCollection = (Collection<MenuData>) menuReadPlatformService.retrieveAll();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.menuDataToApiJsonSerializer.serialize(settings, menuDataCollection);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createMenu(String payload){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        final CommandWrapper commandWrapper = new CommandWrapperBuilder().createMenu().withJson(payload).build();
        final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandWrapper);
        return this.menuItemDataToApiJsonSerializer.serialize(result);
    }

    @Path("/menuitem/template")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String menuItemTemplate(@Context final UriInfo uriInfo){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        final MenuItemData menuItemData = menuItemReadPlatformService.template();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.menuItemDataToApiJsonSerializer.serialize(settings, menuItemData);
    }

    @Path("/menuitem/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveMenuItem(@PathParam("id")Long id ,@Context final UriInfo uriInfo){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        final MenuItemData menuItemData = menuItemReadPlatformService.retrieveOne(id);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.menuItemDataToApiJsonSerializer.serialize(settings, menuItemData);
    }

    @Path("/menuitem")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllMenuItems(@Context final UriInfo uriInfo){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        final Collection<MenuItemData> menuItemData = (Collection<MenuItemData>) menuItemReadPlatformService.retrieveAll();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.menuItemDataToApiJsonSerializer.serialize(settings, menuItemData);
    }

    @Path("/menuitem")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createMenuItem(String payload){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        final CommandWrapper commandWrapper = new CommandWrapperBuilder().createMenuItem().withJson(payload).build();
        final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandWrapper);
        return this.menuItemDataToApiJsonSerializer.serialize(result);
    }
}
