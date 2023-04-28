/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 11:30
 */
package org.apache.fineract.infrastructure.wsplugin.repo;

import org.apache.fineract.infrastructure.wsplugin.domain.WsScript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WsScriptRepository extends JpaRepository<WsScript, Long>, JpaSpecificationExecutor<WsScript> {
}
