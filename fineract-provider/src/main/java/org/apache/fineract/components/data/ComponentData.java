/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 27 April 2023 at 15:15
 */
package org.apache.fineract.components.data;

import com.wese.component.defaults.data.FieldValidationData;
import com.wese.component.defaults.enumerations.CLASS_LOADER;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.wsplugin.data.WsScriptContainerData;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScriptContainer;
import org.apache.fineract.utility.helper.EnumTemplateHelper;

import java.util.Collection;
import java.util.Set;

public class ComponentData {

    private CLASS_LOADER classLoader ;
    private EnumOptionData classLoaderData ;
    private Collection<WsScriptContainerData> wsScriptContainerData ;
    private Set<FieldValidationData> fieldValidationDataSet;

    private Set<FieldValidationData> localRefsDataSet;

    public ComponentData(CLASS_LOADER classLoader,Collection<WsScriptContainerData> wsScriptContainerData, Set<FieldValidationData> fieldValidationDataSet) {
        this.classLoader = classLoader;
        this.classLoaderData = EnumTemplateHelper.template(this.classLoader);
        this.wsScriptContainerData = wsScriptContainerData;
        this.fieldValidationDataSet = fieldValidationDataSet;
    }


    public ComponentData(CLASS_LOADER classLoader,Collection<WsScriptContainerData> wsScriptContainerData, Set<FieldValidationData> fieldValidationDataSet ,Set<FieldValidationData> localRefsDataSet) {
        this.classLoader = classLoader;
        this.classLoaderData = EnumTemplateHelper.template(this.classLoader);
        this.wsScriptContainerData = wsScriptContainerData;
        this.fieldValidationDataSet = fieldValidationDataSet;
        this.localRefsDataSet  = localRefsDataSet;
    }
}
