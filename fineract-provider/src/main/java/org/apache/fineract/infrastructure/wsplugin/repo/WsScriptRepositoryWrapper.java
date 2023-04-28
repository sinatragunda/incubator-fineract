/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 11:34
 */
package org.apache.fineract.infrastructure.wsplugin.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.apache.fineract.infrastructure.wsplugin.exceptions.WsScriptNotFoundException;
import org.apache.fineract.utility.helper.JpaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WsScriptRepositoryWrapper {

    private WsScriptRepository wsScriptRepository ;

    @Autowired
    public WsScriptRepositoryWrapper(WsScriptRepository wsScriptRepository) {
        this.wsScriptRepository = wsScriptRepository;
    }

    public WsScript findOneWithNotFoundDetection(Long id){
        WsScript wsScript = JpaHelper.findOneWithNotFoundDetection(wsScriptRepository ,id ,new WsScriptNotFoundException(id));
        return wsScript;
    }
}
