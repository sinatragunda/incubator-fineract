/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 08:23
 */
package org.apache.fineract.infrastructure.wsplugin.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.enumerations.EXECUTION_LEVEL;
import org.apache.fineract.infrastructure.wsplugin.enumerations.RETURN_TYPE;
import org.apache.fineract.infrastructure.wsplugin.enumerations.SCRIPT_TYPE;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WsScriptData implements EnumeratedData {

    private Long id ;
    private String methodName ;
    private String qualifiedClassName ;
    private RETURN_TYPE returnType;
    private SCRIPT_TYPE scriptType ;
    private EnumOptionData scriptTypeData ;
    private EnumOptionData returnTypeData;

    private EXECUTION_LEVEL executionLevel;
    private EnumOptionData executionLevelData ;

    public WsScriptData(Long id, String methodName, String qualifiedClassName, RETURN_TYPE returnType, SCRIPT_TYPE scriptType ,EXECUTION_LEVEL executionLevel) {
        this.id = id;
        this.methodName = methodName;
        this.qualifiedClassName = qualifiedClassName;
        this.returnType = returnType;
        this.scriptType = scriptType;
        this.scriptTypeData = EnumTemplateHelper.template(scriptType);
        this.returnTypeData = EnumTemplateHelper.template(returnType);
        this.executionLevel = executionLevel;
        this.executionLevelData = EnumTemplateHelper.template(executionLevel);
    }

    public WsScriptData(Long id, String methodName, String qualifiedClassName, String returnTypeStr , SCRIPT_TYPE scriptType) {
        this.id = id;
        this.methodName = methodName;
        this.qualifiedClassName = qualifiedClassName;
        this.scriptType = scriptType;
        this.scriptTypeData = EnumTemplateHelper.template(scriptType);
        this.returnType = (RETURN_TYPE) EnumTemplateHelper.fromStringEx(RETURN_TYPE.values() ,returnTypeStr);
        this.returnTypeData = EnumTemplateHelper.template(this.returnType);
    }

    public WsScriptData(WsScript wsScript){
        this.methodName = wsScript.getMethodName();
        this.qualifiedClassName = wsScript.getQualifiedClassName();
        this.scriptType = wsScript.getScriptType();
        this.scriptTypeData = EnumTemplateHelper.template(scriptType);

        String returnTypeString = wsScript.getReturnType().getCode();
        //this.returnType = EnumTemplateHelper.template()
        this.returnType = (RETURN_TYPE) EnumTemplateHelper.fromStringEx(RETURN_TYPE.values() ,returnTypeString);
        this.returnTypeData = EnumTemplateHelper.templateWithValue(this.returnType);
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.methodName;
    }

    public EXECUTION_LEVEL getExecutionLevel() {
        return executionLevel;
    }
}
