/*

    Created by Sinatra Gunda
    At 5:27 AM on 9/1/2021

*/
package org.apache.fineract.spm.repository;

import org.apache.fineract.portfolio.mailserver.domain.MailServerSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface MailServerSettingsRepository extends JpaRepository<MailServerSettings,Long> ,JpaSpecificationExecutor<MailServerSettings>{

    public MailServerSettings findOne(Long id);

}
