/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 25 April 2023 at 09:09
 */
package org.apache.fineract.infrastructure.wsplugin.helper;

import com.wese.component.defaults.annotations.WsValue;
import com.wese.component.wsscripts.domain.WsScript;
import com.wese.component.wsscripts.helper.AnnotationSupportHelper;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.wsplugin.exceptions.JarFileFailedToReadException;
import org.apache.fineract.utility.helper.FileHelper;

import java.io.File;
import java.io.InputStream;
import java.util.List ;

public class WsValueFunctionFinderHelper {

    public static List<WsScript> findFunctionsFromJarFile(InputStream inputStream,ClassLoader classLoader){

        boolean has = OptionalHelper.isPresent(inputStream);
        if(!has){
            throw new JarFileFailedToReadException();
        }
        File file = FileHelper.fromInputStream(inputStream ,"jar");

        return AnnotationSupportHelper.classesSupportedByAnnotation(null ,file , WsValue.class ,null);
    }
}
