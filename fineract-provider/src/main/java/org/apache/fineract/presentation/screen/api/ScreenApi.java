/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 13:01
 */
package org.apache.fineract.presentation.screen.api;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.presentation.screen.data.ScreenData;
import org.apache.fineract.presentation.screen.service.ScreenCommandReciever;
import org.apache.fineract.presentation.screen.service.ScreenInvoker;
import org.apache.fineract.presentation.screen.utility.CreateScreenCommand;
import org.apache.fineract.presentation.screen.utility.ListScreensCommand;
import org.apache.fineract.presentation.screen.utility.ViewScreenCommand;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;

@Path("/screen")
@Component
@Scope("singleton")
public class ScreenApi {

    private PlatformSecurityContext context;
    private ScreenCommandReciever screenCommandReciever;
    private final FromJsonHelper fromJsonHelper;
    private final ToApiJsonSerializer toApiJsonSerializer;

    private ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public ScreenApi(PlatformSecurityContext context,final ScreenCommandReciever screenCommandReciever ,final FromJsonHelper fromJsonHelper ,final ToApiJsonSerializer toApiJsonSerializer ,final ApiRequestParameterHelper apiRequestParameterHelper) {
        this.context = context;
        this.screenCommandReciever = screenCommandReciever;
        this.fromJsonHelper = fromJsonHelper ;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
    }

//    @Path("/template")
//    @GET
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String menuTemplate(@Context final UriInfo uriInfo){
//        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
//        final MenuData menuData = menuReadPlatformService.template();
//        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
//        return this.menuDataToApiJsonSerializer.serialize(settings, menuData);
//    }


    @Path("/{id}")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(Long id ,@Context final UriInfo uriInfo){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        ViewScreenCommand viewScreenCommand = new ViewScreenCommand(screenCommandReciever ,id);
        ScreenInvoker.invoke(viewScreenCommand);
        final ScreenData screenData = viewScreenCommand.getScreenData();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, screenData);

    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo){
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        ListScreensCommand listScreensCommand = new ListScreensCommand(screenCommandReciever);
        ScreenInvoker.invoke(listScreensCommand);
        final Collection<ScreenData> screenDataCollection = listScreensCommand.getScreenDataCollection();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, screenDataCollection);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createScreen(String payload){

        System.err.println("------------------do we even start here or ?");
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        JsonCommand command = JsonCommandHelper.jsonCommand(fromJsonHelper ,payload);
        CreateScreenCommand createScreenCommand = new CreateScreenCommand(screenCommandReciever ,command);
        ScreenInvoker.invoke(createScreenCommand);
        CommandProcessingResult result = createScreenCommand.getResult();
        return toApiJsonSerializer.serialize(result);
    }

//    @Path("/menuitem/template")
//    @GET
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String menuItemTemplate(@Context final UriInfo uriInfo){
//        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
//        final MenuItemData menuItemData = menuItemReadPlatformService.template();
//
//        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
//        return this.menuItemDataToApiJsonSerializer.serialize(settings, menuItemData);
//    }

//    @Path("/menuitem/{id}")
//    @GET
//    public String retrieveMenuItem(@PathParam("id")Long id , final UriInfo uriInfo){
//        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
//        final MenuItemData menuItemData = menuItemReadPlatformService.retrieveOne(id);
//        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
//        return this.menuItemDataToApiJsonSerializer.serialize(settings, menuItemData);
//    }
//
//    @Path("/menuitem")
//    @GET
//    @Produces({ MediaType.APPLICATION_JSON })
//    public String retrieveAllMenuItems(@Context final UriInfo uriInfo){
//        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
//        final Collection<MenuItemData> menuItemData = (Collection<MenuItemData>) menuItemReadPlatformService.retrieveAll();
//        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
//        return this.menuItemDataToApiJsonSerializer.serialize(settings, menuItemData);
//    }

}
