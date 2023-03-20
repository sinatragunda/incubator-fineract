/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 March 2023 at 10:30
 */
package org.apache.fineract.components.screen.api;


/*

    Created by Sinatra Gunda
    At 3:41 AM on 9/12/2022

*/

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.campaigns.email.data.ScheduledEmailEnumerations;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sound.sampled.Port;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.*;

import org.springframework.data.jpa.repository.config.*;

@Path("/screen")
@Component
@Scope("singleton")
public class ScreenApiResource {

    private final PlatformSecurityContext context ;
    private final ApiRequestParameterHelper apiRequestParameterHelper ;
    private ToApiJsonSerializer toApiJsonSerializer;
    private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;


    @Autowired
    public ScreenApiResource(PlatformSecurityContext context, ApiRequestParameterHelper apiRequestParameterHelper, ToApiJsonSerializer toApiJsonSerializer , final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;

    }

    /**
     * Returns field reflection for specific class inserted as param or query parama
     * @param uriInfo
     * @param officeId
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createScreen(@Context final UriInfo uriInfo,String payload) {

        this.context.authenticatedUser();

        CommandWrapper commandWrapper = new CommandWrapperBuilder().createScreen().withJson(payload).build();
        CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandWrapper);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings ,result);

    }
}

