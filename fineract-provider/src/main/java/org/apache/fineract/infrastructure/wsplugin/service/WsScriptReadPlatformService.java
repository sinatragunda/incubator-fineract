/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 27 April 2023 at 08:00
 */
package org.apache.fineract.infrastructure.wsplugin.service;

import org.apache.fineract.infrastructure.wsplugin.data.WsScriptContainerData;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScriptContainer;

import java.util.Collection;

public interface WsScriptReadPlatformService {

    public WsScriptContainerData retrieveOne(Long id);
    public Collection<WsScriptContainerData> retrieveAll(Boolean isEagerLoading);



}
