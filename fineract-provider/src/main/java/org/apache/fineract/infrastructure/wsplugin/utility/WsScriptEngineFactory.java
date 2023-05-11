/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 03 May 2023 at 09:33
 */
package org.apache.fineract.infrastructure.wsplugin.utility;

import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.enumerations.SCRIPT_TYPE;

import java.util.function.Function;

public class WsScriptEngineFactory {

    public static IWsScriptEngine wsScriptEngineFactory(Function function ,Object args , SCRIPT_TYPE scriptType){
        IWsScriptEngine wsScriptEngine = null ;
        switch (scriptType){
            case INTERNAL:
                wsScriptEngine = new WsScriptEngineExternal(function ,args);
                break;
            case EXTERNAL:
                wsScriptEngine = new WsScriptEngineExternal(function ,args);
                break;
        }
        return wsScriptEngine;
    }

    public static IWsScriptEngine wsScriptEngineFactory(Function function ,Object arg ,WsScript wsScript){
        SCRIPT_TYPE scriptType = wsScript.getScriptType();
        return wsScriptEngineFactory(function ,arg ,scriptType);
    }

    // back to back we should be face to face song 
}
