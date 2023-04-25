/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 25 April 2023 at 08:50
 */
package org.apache.fineract.infrastructure.wsplugin.api;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import com.wese.component.wsscripts.domain.WsScript;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.wsplugin.helper.WsValueFunctionFinderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Cieyou on 2/27/14.
 */
@Path("/wsscripts")
@Component
@Scope("singleton")
public class WsPluginApi {

    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public WsPluginApi(final DefaultToApiJsonSerializer toApiJsonSerializer, final PlatformSecurityContext context, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.context = context;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @POST
    @Path("verify")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String verifyFunctionEntries(@FormDataParam("file") InputStream uploadedInputStream,
                                   @FormDataParam("file") FormDataContentDisposition fileDetail,
                                   @FormDataParam("locale") final String locale, @FormDataParam("dateFormat") final String dateFormat){

        ClassLoader classLoader = super.getClass().getClassLoader();
        System.err.println("--------------has input stream at load ? "+OptionalHelper.isPresent(uploadedInputStream));
        List<WsScript> wsScriptList = WsValueFunctionFinderHelper.findFunctionsFromJarFile(uploadedInputStream ,classLoader);
        return this.toApiJsonSerializer.serialize(wsScriptList);

    }



}
