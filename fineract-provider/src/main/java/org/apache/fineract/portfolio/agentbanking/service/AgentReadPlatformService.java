/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 15 February 2023 at 06:59
 */
package org.apache.fineract.portfolio.agentbanking.service;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.agentbanking.data.AgentData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;

public interface AgentReadPlatformService {

    public AgentData retrieveOne(StaffData staffData);
    public AgentData retrieveOne(Long staffId);

}
