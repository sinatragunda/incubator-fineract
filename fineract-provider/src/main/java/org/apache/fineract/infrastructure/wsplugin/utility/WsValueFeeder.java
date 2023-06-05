/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 03 May 2023 at 09:17
 */
package org.apache.fineract.infrastructure.wsplugin.utility;

import com.wese.component.defaults.annotations.WsValue;
import com.wese.component.defaults.domain.WsObject;
import com.wese.component.defaults.enumerations.HOOK_POINT;
import com.wese.component.defaults.helper.AnnotationHelper;
import com.wese.component.wsscripts.helper.ClassLoaderHelper;
import com.wese.component.wsscripts.helper.MethodHelper;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.wsplugin.api.WsScriptConstants;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScriptContainer;
import org.apache.fineract.infrastructure.wsplugin.enumerations.RETURN_TYPE;
import org.apache.fineract.presentation.screen.api.VersionApi;
import org.apache.fineract.presentation.screen.domain.ScreenElement;

import java.io.File;
import java.util.function.Function;

public class WsValueFeeder {

    public static ScreenElement feedValue(Function validationFunction ,ScreenElement screenElement ){

        WsScript wsScript = screenElement.getWsScript();

        Object wsObjectValue = screenElement.getParameter();

        WsObject wsObject = new WsObject(wsObjectValue);

        System.err.println("----------------------------set value for wsObject now ------------"+ OptionalHelper.isPresent(wsObject));

        WsScriptContainer wsScriptContainer = wsScript.getWsScriptContainer();
        
        Document document = wsScriptContainer.getDocument();

        String filename = document.getLocation();
        System.err.println("---------------------filename location is "+filename);

        File file = new File(filename);

        String className = wsScript.getQualifiedClassName();
        String methodName = wsScript.getMethodName();

        System.err.println("------------------class name is "+className);

        ClassLoader classLoader = VersionApi.classLoader;

        Class cl = ClassLoaderHelper.loadClass(classLoader ,file ,className);

        System.err.println("---------------method name is "+methodName);

        System.err.println("-----------------do we have the class now ? "+OptionalHelper.isPresent(cl)+"---------and name is "+cl.getName());

        HOOK_POINT hookPoint = (HOOK_POINT) AnnotationHelper.getMethodAnnotation(cl, WsValue.class , WsScriptConstants.hookAnnotationParam,methodName);

        System.err.println("-------------declation function now at hook point ? "+hookPoint);

        System.err.println("----------------------system classloader is "+classLoader.getClass().getName());

        Function scriptFunction = MethodHelper.wsValueMethod(classLoader ,file ,wsScript);

        System.err.println("----------------we now have scriptFunction now ---------------");

        IWsScriptEngine wsScriptEngine = WsScriptEngineFactory.wsScriptEngineFactory(scriptFunction ,wsObject ,wsScript);

        switch (hookPoint){
            case AT_START:
                // execute wsscript before validation function
                wsObject = (WsObject)wsScriptEngine.execute();
                screenElement.updateFromWsObject(wsObject);
                validationFunction.apply(screenElement);
                break;
            case AT_EXIT:
                // execute function first then validation
                System.err.println("------------validate at exit point----- ");
                validationFunction.apply(screenElement);
                wsObject = (WsObject) wsScriptEngine.execute();
                screenElement.updateFromWsObject(wsObject);
                System.err.println("-----------screen element outside update scope "+screenElement);
                break;
        }

        return screenElement;
    }
}
