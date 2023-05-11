/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 25 April 2023 at 09:09
 */
package org.apache.fineract.infrastructure.wsplugin.helper;

import com.wese.component.defaults.annotations.WsValue;
import com.wese.component.wsscripts.helper.AnnotationSupportHelper;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScriptContainer;
import org.apache.fineract.infrastructure.wsplugin.exceptions.JarFileFailedToReadException;
import org.apache.fineract.utility.helper.FileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List ;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class WsValueFunctionFinderHelper {

    public static List<WsScript> findFunctionsFromJarFile(InputStream inputStream, ClassLoader parentClassLoader){

        boolean has = OptionalHelper.isPresent(inputStream);
        if(!has){
            throw new JarFileFailedToReadException();
        }
        File file = FileHelper.fromInputStream(inputStream ,"jar");
        //ClassLoader classLoader1 = WsValueFunctionFinderHelper.class.getClassLoader();

        return AnnotationSupportHelper.classesSupportedByAnnotation(null ,file , WsValue.class ,parentClassLoader);
    }

    public static Class loadClassFromJar(WsScript wsScript){

        WsScriptContainer container = wsScript.getWsScriptContainer();
        Document document = container.getDocument();
        String filename = document.getFileName();
        String location = document.getLocation();

        File file = new File(location);
        return null ;

    }
}
