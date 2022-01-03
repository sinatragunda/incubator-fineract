/*

    Created by Sinatra Gunda
    At 12:19 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.commissions.data.LoanAgentData;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface LoanAgentReadPlatformService {

    public LoanAgentData retrieveOne(Long id);
    LoanAgentData retrieveOneByClient(Long id);
}

