/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 11:34
 */
package org.apache.fineract.infrastructure.wsplugin.repo;

import org.apache.fineract.infrastructure.wsplugin.domain.WsScriptContainer;
import org.apache.fineract.utility.helper.JpaHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WsScriptContainerRepositoryWrapper {

    private WsScriptContainerRepository repository;

    @Autowired
    public WsScriptContainerRepositoryWrapper(WsScriptContainerRepository repository) {
        this.repository = repository;
    }

    public WsScriptContainer findOneWithNotFoundDetection(Long id){
        WsScriptContainer wsScriptContainer = JpaHelper.findOneWithNotFoundDetection(repository ,id ,null);
        return wsScriptContainer;
    }

    public WsScriptContainer save(WsScriptContainer wsScriptContainer){
        return JpaHelper.save(repository ,wsScriptContainer);
    }
}
