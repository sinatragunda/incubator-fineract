/*

    Created by Sinatra Gunda
    At 3:41 AM on 9/12/2022

*/
package org.apache.fineract.components.api;

import com.google.common.base.Optional;
import com.wese.component.defaults.constants.FieldConstants;
import com.wese.component.defaults.data.FieldValidationData;
import com.wese.component.defaults.enumerations.CLASS_LOADER;
import com.wese.component.defaults.helper.FieldReflectionHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.sql.Struct;
import java.util.List;

@Path("/component")
@Component
@Scope("singleton")
public class ComponentApiResource {

    private final PlatformSecurityContext context ;
    private final ApiRequestParameterHelper apiRequestParameterHelper ;
    private ToApiJsonSerializer<FieldValidationData> fieldValidationDataToApiJsonSerializer;

    @Autowired
    public ComponentApiResource(PlatformSecurityContext context, ApiRequestParameterHelper apiRequestParameterHelper, ToApiJsonSerializer<FieldValidationData> fieldValidationDataToApiJsonSerializer) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.fieldValidationDataToApiJsonSerializer = fieldValidationDataToApiJsonSerializer;

        System.err.println("--------------------are we initialized here ? -------------");
    }

    /**
     * Returns field reflection for specific class inserted as param or query parama
     * @param uriInfo
     * @param officeId
     * @return
     */
    @GET
    @Path("field")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @QueryParam("class") final String className) {

        this.context.authenticatedUser();

        System.err.println("-------------------------class name is "+className);

        CLASS_LOADER classLoader = CLASS_LOADER.fromString(className);

        String url = classLoader.getUrl();

        Class cl = ClassLoader.getSystemResource(url).getClass();

        System.err.println("-----------classloader found value ? "+ Optional.ofNullable(cl).isPresent());
        String ret = FieldReflectionHelper.getClassAttributes(cl).toString();


        System.err.println("--------------return value is ------------"+ret);

        List<FieldValidationData> fieldValidationDataList = FieldReflectionHelper.getClassAttributes(cl);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.fieldValidationDataToApiJsonSerializer.serialize(settings, fieldValidationDataList, FieldConstants.allowedParams);

    }
}
