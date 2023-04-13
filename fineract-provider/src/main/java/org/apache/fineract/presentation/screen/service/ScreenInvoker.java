/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:02
 */
package org.apache.fineract.presentation.screen.service;


import org.apache.fineract.presentation.screen.utility.IScreenCommand;

public class ScreenInvoker {



    public static void invoke(IScreenCommand iScreenCommand){
        iScreenCommand.invoke();
    }
}
