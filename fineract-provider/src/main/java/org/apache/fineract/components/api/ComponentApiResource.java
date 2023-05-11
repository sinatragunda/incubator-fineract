/*

    Created by Sinatra Gunda
    At 3:41 AM on 9/12/2022

*/
package org.apache.fineract.components.api;

import com.wese.component.defaults.constants.FieldConstants;
import com.wese.component.defaults.data.FieldValidationData;
import com.wese.component.defaults.enumerations.CLASS_LOADER;
import com.wese.component.defaults.exceptions.BeanLoaderNotFoundException;
import com.wese.component.defaults.helper.FieldReflectionHelper;
import org.apache.fineract.components.data.ComponentData;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.wsplugin.data.WsScriptContainerData;
import org.apache.fineract.infrastructure.wsplugin.service.WsScriptReadPlatformService;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.helper.LocalRefToFieldValidationData;
import org.apache.fineract.portfolio.localref.service.LocalRefReadPlatformService;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.helper.ListHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.QueryParam;

import java.sql.Struct;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.context.ApplicationContext;


import org.springframework.context.annotation.*;

@Path("/component")
@Component
@Scope("singleton")
@ComponentScan(value="com.wese.component.*")
public class ComponentApiResource {

    private final ApplicationContext applicationContext;
    private final PlatformSecurityContext context ;
    private final ApiRequestParameterHelper apiRequestParameterHelper ;
    private ToApiJsonSerializer<FieldValidationData> fieldValidationDataToApiJsonSerializer;
    private WsScriptReadPlatformService wsScriptReadPlatformService ;
    private ToApiJsonSerializer<ComponentData> componentDataToApiJsonSerializer;
    private LocalRefReadPlatformService localRefReadPlatformService;

    @Autowired
    public ComponentApiResource(PlatformSecurityContext context, ApiRequestParameterHelper apiRequestParameterHelper, ToApiJsonSerializer<FieldValidationData> fieldValidationDataToApiJsonSerializer ,ApplicationContext applicationContext ,final WsScriptReadPlatformService wsScriptReadPlatformService ,final ToApiJsonSerializer<ComponentData> componentDataToApiJsonSerializer ,final LocalRefReadPlatformService localRefReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.fieldValidationDataToApiJsonSerializer = fieldValidationDataToApiJsonSerializer;
        this.applicationContext = applicationContext;
        this.wsScriptReadPlatformService = wsScriptReadPlatformService;
        this.componentDataToApiJsonSerializer = componentDataToApiJsonSerializer;
        this.localRefReadPlatformService = localRefReadPlatformService;

    }

    /**
     * Returns field reflection for specific class inserted as param or query parama
     * @param uriInfo
     * @param officeId
     * @return
     */
    @GET
    @Path("field/{class}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @PathParam("class") final String className ,@QueryParam("eagerLoad") Boolean eagerLoading) {

        this.context.authenticatedUser();

        CLASS_LOADER classLoader = CLASS_LOADER.fromString(className);
        boolean hasClassLoader = OptionalHelper.isPresent(classLoader);

        if(!hasClassLoader){
            throw new BeanLoaderNotFoundException(className);
        }
        
        Class cl = null ;

        try {
            cl = classLoader.getCl();
        }
        catch (Exception c){
            c.printStackTrace();
        }

        ComponentData componentData = getComponentData(cl,classLoader ,eagerLoading);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.componentDataToApiJsonSerializer.serialize(settings, componentData);
    }

    private ComponentData getComponentData(Class cl ,CLASS_LOADER classLoader ,Boolean eagerLoading) {
        Set<FieldValidationData> fieldValidationDataList = FieldReflectionHelper.getClassAttributes(cl,applicationContext);
        Collection<WsScriptContainerData> wsScriptContainerDataCollection = wsScriptReadPlatformService.retrieveAll(eagerLoading);
        Set<FieldValidationData> localRefsFieldValidationDataSet = getLocalRefsAsFieldValidationData(classLoader);
        ComponentData componentData = new ComponentData(classLoader ,wsScriptContainerDataCollection ,fieldValidationDataList ,localRefsFieldValidationDataSet);
        return componentData;
    }

    private Set<FieldValidationData> getLocalRefsAsFieldValidationData(CLASS_LOADER classLoader){
        REF_TABLE refTable = REF_TABLE.fromClassLoader(classLoader);
        LocalRefData localRefData = localRefReadPlatformService.template(refTable);
        List<FieldValidationData> fieldValidationDataList = LocalRefToFieldValidationData.convertForTemplate(localRefData);
        return ListHelper.toSet(fieldValidationDataList);
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplateById(@Context final UriInfo uriInfo, @PathParam("id") final Integer id) {

        this.context.authenticatedUser();

        CLASS_LOADER classLoader = (CLASS_LOADER) EnumTemplateHelper.fromInt(CLASS_LOADER.values() ,id);
        Class cl = getaClass(id, classLoader);

        //Set<FieldValidationData> fieldValidationDataList = FieldReflectionHelper.getClassAttributes(cl,applicationContext);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        ComponentData componentData = getComponentData(cl,classLoader,false);
        return this.componentDataToApiJsonSerializer.serialize(settings, componentData);
    }

    private static Class getaClass(Integer id, CLASS_LOADER classLoader) {

        boolean hasClassLoader = OptionalHelper.isPresent(classLoader);
        if(!hasClassLoader){
            throw new BeanLoaderNotFoundException(id.longValue());
        }

        Class cl = null ;

        try {
            cl = classLoader.getCl();
        }
        catch (Exception c){
            c.printStackTrace();
        }
        return cl;
    }


    /**
     * Added 16/04/2023 at 0455
     * Template class for components generally 
     */ 
    @GET
    @Path("template")
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@Context final UriInfo uriInfo){

        this.context.authenticatedUser();
        FieldValidationData fieldValidationData = FieldValidationData.template();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.fieldValidationDataToApiJsonSerializer.serialize(settings, fieldValidationData);
    }
}
