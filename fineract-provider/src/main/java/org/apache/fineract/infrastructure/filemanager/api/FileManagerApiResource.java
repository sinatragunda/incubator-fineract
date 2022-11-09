/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 November 2022 at 02:33
 */
package org.apache.fineract.infrastructure.filemanager.api;

import java.util.Collection;
import java.util.HashMap;

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

import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.filemanager.data.FolderData;
import org.apache.fineract.infrastructure.filemanager.helper.FolderIteratorHelper;
import org.apache.fineract.infrastructure.gcm.api.DeviceRegistrationApiConstants;
import org.apache.fineract.infrastructure.gcm.domain.DeviceRegistration;
import org.apache.fineract.infrastructure.gcm.domain.DeviceRegistrationData;
import org.apache.fineract.infrastructure.gcm.service.DeviceRegistrationReadPlatformService;
import org.apache.fineract.infrastructure.gcm.service.DeviceRegistrationWritePlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Path("/filemanager")
@Component
@Scope("singleton")
public class FileManagerApiResource {

    private final PlatformSecurityContext context;
    private final DeviceRegistrationWritePlatformService deviceRegistrationWritePlatformService;
    private final DefaultToApiJsonSerializer<DeviceRegistrationData> toApiJsonSerializer;

    private final DefaultToApiJsonSerializer<FolderData> folderDataDefaultToApiJsonSerializer ;
    private final DeviceRegistrationReadPlatformService deviceRegistrationReadPlatformService;

    @Autowired
    public FileManagerApiResource(PlatformSecurityContext context,
                                         final DefaultToApiJsonSerializer<DeviceRegistrationData> toApiJsonSerializer,
                                         final DeviceRegistrationReadPlatformService deviceRegistrationReadPlatformService,
                                         final DeviceRegistrationWritePlatformService deviceRegistrationWritePlatformService ,final DefaultToApiJsonSerializer<FolderData> folderDataDefaultToApiJsonSerializer) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.deviceRegistrationReadPlatformService = deviceRegistrationReadPlatformService;
        this.deviceRegistrationWritePlatformService = deviceRegistrationWritePlatformService;
        this.folderDataDefaultToApiJsonSerializer = folderDataDefaultToApiJsonSerializer;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String registerDevice(final String apiRequestBodyAsJson) {
        this.context.authenticatedUser();
        Gson gson = new Gson();
        JsonObject json = new Gson().fromJson(apiRequestBodyAsJson, JsonObject.class);
        Long clientId = json.get(DeviceRegistrationApiConstants.clientIdParamName).getAsLong();
        String registrationId = json.get(DeviceRegistrationApiConstants.registrationIdParamName).getAsString();
        DeviceRegistration deviceRegistration = this.deviceRegistrationWritePlatformService.registerDevice(clientId, registrationId);
        String response = gson.toJson(deviceRegistration.getId());
        return response;
    }

    @GET
    @Path("/folderstructure")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String folderStructure(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser();
        Collection<FolderData> folderDataCollection = FolderIteratorHelper.files();
        return this.folderDataDefaultToApiJsonSerializer.serialize(folderDataCollection);

    }

    @GET
    @Path("client/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveDeviceRegistrationByClientId(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        DeviceRegistrationData deviceRegistrationData = this.deviceRegistrationReadPlatformService
                .retrieveDeviceRegiistrationByClientId(clientId);

        return this.toApiJsonSerializer.serialize(deviceRegistrationData);
    }

    @GET
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveDeviceRegiistration(@PathParam("id") final Long id, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        DeviceRegistrationData deviceRegistrationData = this.deviceRegistrationReadPlatformService.retrieveDeviceRegiistration(id);

        return this.toApiJsonSerializer.serialize(deviceRegistrationData);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateDeviceRegistration(@PathParam("id") final Long id, final String apiRequestBodyAsJson) {

        this.context.authenticatedUser();

        Gson gson = new Gson();
        JsonObject json = new Gson().fromJson(apiRequestBodyAsJson, JsonObject.class);
        Long clientId = json.get(DeviceRegistrationApiConstants.clientIdParamName).getAsLong();
        String registrationId = json.get(DeviceRegistrationApiConstants.registrationIdParamName).getAsString();
        DeviceRegistration deviceRegistration = this.deviceRegistrationWritePlatformService.updateDeviceRegistration(id, clientId,
                registrationId);
        String response = gson.toJson(deviceRegistration.getId());
        return response;
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(@PathParam("id") final Long id) {

        this.context.authenticatedUser();
        this.deviceRegistrationWritePlatformService.deleteDeviceRegistration(id);
        return responseMap(id);

    }
    public String responseMap(Long id){
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("resource", id);
        return new Gson().toJson(responseMap);
    }

}

