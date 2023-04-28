/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 08:34
 */
package org.apache.fineract.infrastructure.wsplugin.helper;

import com.wese.component.wsscripts.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.data.WsScriptData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WsScriptHelper {


    public static List<WsScriptData> wsScriptToScriptDataList(List<WsScript> wsScriptList){

        List<WsScriptData> wsScriptDataList = new ArrayList<>();

        Consumer<WsScript> convertWsScriptToDataConsumer = (e)->{
            WsScriptData wsScriptData = new WsScriptData(e);
            wsScriptDataList.add(wsScriptData);
        };

        wsScriptList.stream().forEach(convertWsScriptToDataConsumer);
        return wsScriptDataList;
    }
}
