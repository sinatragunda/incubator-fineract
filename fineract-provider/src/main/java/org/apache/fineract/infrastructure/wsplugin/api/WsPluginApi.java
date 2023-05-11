/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 25 April 2023 at 08:50
 */
package org.apache.fineract.infrastructure.wsplugin.api;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.repo.DocumentRepositoryWrapper;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformServiceJpaRepositoryImpl;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.wsplugin.data.WsScriptContainerData;
import org.apache.fineract.infrastructure.wsplugin.data.WsScriptData;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.helper.WsScriptHelper;
import org.apache.fineract.infrastructure.wsplugin.helper.WsValueFunctionFinderHelper;
import org.apache.fineract.infrastructure.wsplugin.service.WsScriptReadPlatformService;
import org.apache.fineract.infrastructure.wsplugin.service.WsScriptWritePlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import io.swagger.annotations.*;

@Path("/wsscripts")
@Component
@Scope("singleton")
public class WsPluginApi {

    private final DefaultToApiJsonSerializer<WsScriptData> toApiJsonSerializer;
    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DocumentWritePlatformService documentWritePlatformService;
    private final DocumentRepositoryWrapper documentRepositoryWrapper;
    private final WsScriptWritePlatformService wsScriptWritePlatformService;
    private final FromJsonHelper fromJsonHelper;
    private final WsScriptReadPlatformService wsScriptReadPlatformService;

    @Autowired
    public WsPluginApi(final DefaultToApiJsonSerializer<WsScriptData> toApiJsonSerializer, final PlatformSecurityContext context, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final DocumentWritePlatformService documentWritePlatformService, final DocumentRepositoryWrapper documentRepositoryWrapper, final WsScriptWritePlatformService wsScriptWritePlatformService, final FromJsonHelper fromJsonHelper ,final WsScriptReadPlatformService wsScriptReadPlatformService) {
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.context = context;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.documentRepositoryWrapper = documentRepositoryWrapper;
        this.documentWritePlatformService = documentWritePlatformService;
        this.wsScriptWritePlatformService = wsScriptWritePlatformService;
        this.fromJsonHelper = fromJsonHelper;
        this.wsScriptReadPlatformService = wsScriptReadPlatformService;
    }

    @POST
    @Path("verify")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String verifyFunctionEntries(@FormDataParam("file") InputStream uploadedInputStream,
                                        @FormDataParam("file") FormDataContentDisposition fileDetail,
                                        @FormDataParam("locale") final String locale, @FormDataParam("dateFormat") final String dateFormat) {

        ClassLoader classLoader = super.getClass().getClassLoader();

        System.err.println("--------------has input stream at load ? " + OptionalHelper.isPresent(uploadedInputStream));
        List<WsScript> wsScriptList = WsValueFunctionFinderHelper.findFunctionsFromJarFile(uploadedInputStream, classLoader);
        List<WsScriptData> wsScriptDataList = WsScriptHelper.wsScriptToScriptDataList(wsScriptList);

        return this.toApiJsonSerializer.serializePretty(true, wsScriptDataList);

    }

    @GET
    @Path("{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String retrieveOne(@PathParam("id") Long id) {

        WsScriptContainerData wsScriptContainerData = this.wsScriptReadPlatformService.retrieveOne(id);
        return this.toApiJsonSerializer.serializePretty(true,wsScriptContainerData);

    }


    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String retrieveAll() {

        Collection<WsScriptContainerData> wsScriptContainerDataCollection = this.wsScriptReadPlatformService.retrieveAll(false);
        return this.toApiJsonSerializer.serializePretty(true,wsScriptContainerDataCollection);

    }


    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON})
    public String createWsScriptFromJarFile(@HeaderParam("Content-Length") @ApiParam(value = "Content-Length") final Long fileSize, @FormDataParam("file") @ApiParam(value = "file") final InputStream inputStream,
                                            @FormDataParam("file") final @ApiParam(value = "file") FormDataContentDisposition fileDetails, @FormDataParam("file") @ApiParam(value = "file") final FormDataBodyPart bodyPart,
                                            @FormDataParam("name") @ApiParam(value = "name") final String name, @FormDataParam("description") @ApiParam(value = "description") final String description,@FormDataParam("payload") @ApiParam(value = "payload") final String payload) {

        System.err.println("--------------------------------------json -----------" + payload);

        Long entityId = 1L ;
        String entityType = DocumentWritePlatformServiceJpaRepositoryImpl.DOCUMENT_MANAGEMENT_ENTITY.SCRIPT.toString();

        final DocumentCommand documentCommand = new DocumentCommand(null, null, entityType, entityId, name, fileDetails.getFileName(),
                fileSize, bodyPart.getMediaType().toString(), description, null);

        final Long documentId = this.documentWritePlatformService.createDocument(documentCommand, inputStream);
        final Document document = this.documentRepositoryWrapper.findOneWithNotFoundDetection(documentId);

        final JsonCommand command = JsonCommandHelper.jsonCommand(fromJsonHelper, payload);
        final CommandProcessingResult result = wsScriptWritePlatformService.create(document, command);

        return toApiJsonSerializer.serializePretty(true, result);
    }
}