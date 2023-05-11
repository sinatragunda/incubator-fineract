/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 03 May 2023 at 04:12
 */
package org.apache.fineract.infrastructure.wsplugin.utility;

import org.apache.fineract.presentation.screen.domain.ScreenElement;

import java.util.function.Function;

public class WsObjectInjector {

    /**
     * Added 03/05/2023 at 1001
     * Function is the entrance into the injection framework for WsObjects
     * Funcion is called by ValidationFactory
     */
    public static ScreenElement injectAndValidate(Function validationFunction , ScreenElement screenElement){
        return WsValueFeeder.feedValue(validationFunction ,screenElement);
    }
}
