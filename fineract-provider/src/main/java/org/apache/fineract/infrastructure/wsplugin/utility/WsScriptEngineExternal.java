/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 03 May 2023 at 09:28
 */
package org.apache.fineract.infrastructure.wsplugin.utility;

import org.apache.fineract.helper.OptionalHelper;

import java.util.Optional;
import java.util.function.Function;

public class WsScriptEngineExternal implements IWsScriptEngine{

    private Function function ;
    private Object args ;

    public WsScriptEngineExternal(Function function ,Object args){
        this.function = function ;
        this.args = args;
    }

    @Override
    public Object execute() {
        System.err.println("------------------------------------exectute function now ");
        Object x = function.apply(this.args);
        System.err.println("-------------object being modified here likely null ? "+ OptionalHelper.isPresent(x));
        System.err.println("-------------return a modified value of wsObject");
        return this.args;
    }
}
