/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 07 May 2023 at 16:43
 */
package org.apache.fineract.infrastructure.wsplugin.helper;

import org.apache.fineract.infrastructure.wsplugin.data.WsScriptContainerData;
import org.apache.fineract.infrastructure.wsplugin.data.WsScriptData;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.enumerations.RETURN_TYPE;
import org.apache.fineract.infrastructure.wsplugin.enumerations.SCRIPT_TYPE;
import org.apache.fineract.infrastructure.wsplugin.enumerations.WS_INTERNAL_SCRIPTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List ;

public class WsInternalScriptsLoader {

    public static List<WsScript> iterator(){

        WS_INTERNAL_SCRIPTS[] wsInternalScripts = WS_INTERNAL_SCRIPTS.values();
        List<WsScript> wsScriptList = new ArrayList<>();

        for (WS_INTERNAL_SCRIPTS ws : wsInternalScripts) {
            WsScript wsScript = getWsScript(ws);
            wsScriptList.add(wsScript);
        }

        return wsScriptList;
    }


    public static WsScriptContainerData loadAsContainerData(){
        WS_INTERNAL_SCRIPTS[] wsInternalScripts = WS_INTERNAL_SCRIPTS.values();
        Collection<WsScriptData> wsScriptDataList = new ArrayList<>();

        for(WS_INTERNAL_SCRIPTS ws : wsInternalScripts) {
            WsScript wsScript = getWsScript(ws);
            WsScriptData wsScriptData = new WsScriptData(wsScript);
            wsScriptDataList.add(wsScriptData);
            wsScriptData.setId(0L);
        }

        WsScriptContainerData wsScriptContainerData = new WsScriptContainerData("Internal Scripts" ,wsScriptDataList);
        return wsScriptContainerData;
    }

    private static WsScript getWsScript(WS_INTERNAL_SCRIPTS ws) {
        String className = ws.getCode();
        String methodName = ws.getCode();
        RETURN_TYPE returnType = RETURN_TYPE.OBJECT;
        WsScript wsScript = new WsScript(className ,methodName ,returnType , SCRIPT_TYPE.INTERNAL);
        return wsScript;
    }
}
