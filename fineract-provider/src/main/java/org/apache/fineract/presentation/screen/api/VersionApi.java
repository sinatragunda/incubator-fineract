/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 07:48
 */
package org.apache.fineract.presentation.screen.api;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
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

import java.util.Collection;

@Path("/version")
@Component
@Scope("singleton")
public class VersionApi {

    private PlatformSecurityContext context;
    private ScreenCommandReciever screenCommandReciever;
    private final FromJsonHelper fromJsonHelper;
    private final ToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;

    private ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public VersionApi(PlatformSecurityContext context,final ScreenCommandReciever screenCommandReciever ,final FromJsonHelper fromJsonHelper ,final ToApiJsonSerializer<ScreenData> toApiJsonSerializer ,final ApiRequestParameterHelper apiRequestParameterHelper ,final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService) {
        this.context = context;
        this.screenCommandReciever = screenCommandReciever;
        this.fromJsonHelper = fromJsonHelper ;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
    }

    @Path("/{name}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneByName(@PathParam("name")String name ,@Context final UriInfo uriInfo){

        System.err.println("---------------------are we even reaching first command by name ? "+name);

        ViewScreenCommand viewScreenCommand = new ViewScreenCommand(screenCommandReciever ,name);

        ScreenInvoker.invoke(viewScreenCommand);

        final ScreenData screenData = viewScreenCommand.getScreenData();

        System.err.println("-----------------------------do we get screen data anyway ? ");
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, screenData);

    }

    @GET
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
    public String postVersionRecord(String payload){

        System.err.println("------------------post version record  ?");
        //context.getAuthenticatedUserIfPresent().validateHasReadPermission();
        CommandWrapper commandWrapper = new CommandWrapperBuilder().createVersionRecord().withJson(payload).build();
        CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandWrapper);
        return toApiJsonSerializer.serialize(result);
    }
}

