/*

    Created by Sinatra Gunda
    At 12:20 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.commissions.data.LoanAgentData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

@Service
public class LoanAgentReadPlatformServiceImpl implements LoanAgentReadPlatformService{

    private PlatformSecurityContext context ;
    private JdbcTemplate jdbcTemplate ;
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService ;
    private ClientReadPlatformService clientReadPlatformService ;


    @Autowired
    public LoanAgentReadPlatformServiceImpl(final RoutingDataSource routingDataSource ,final PlatformSecurityContext context){
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.context = context ;
    }


    private static final class LoanAgentDataMapper implements RowMapper<LoanAgentData> {

        public String schema() {
            return "la.id as id, "
                    + "la.client_id as clientId ,"
                    + "mc.account_no as clientAccountNo ,"
                    + "mc.display_name as displayName ,"
                    + "la.savings_account_id as savingsAccountId "
                    + "from m_loan_agent la join "
                    + "m_client mc on mc.id = la.client_id";
        }

        @Override
        public LoanAgentData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long clientId = rs.getLong("clientId");
            final Long savingsAccountId = rs.getLong("savingsAccountId");
            final String displayName = rs.getString("displayName");
            final String clientAccountNo = rs.getString("clientAccountNo");

            return LoanAgentData.instance(id, clientId ,savingsAccountId ,clientAccountNo, displayName);
        }
    }

    // always one unique agent per client
    @Override
    public LoanAgentData retrieveOne(Long id){

        this.context.authenticatedUser();
        final LoanAgentDataMapper loanAgentDataMapper = new LoanAgentDataMapper();
        final String sql = "select " + loanAgentDataMapper.schema() + " where la.id=?";
        return this.jdbcTemplate.queryForObject(sql, loanAgentDataMapper, new Object[] { id});
    }

    // always one unique agent per client
    @Override
    public LoanAgentData retrieveOneByClient(Long clientId){

        this.context.authenticatedUser();
        final LoanAgentDataMapper loanAgentDataMapper = new LoanAgentDataMapper();
        final String sql = "select " + loanAgentDataMapper.schema() + " where la.client_id=?";
        return this.jdbcTemplate.queryForObject(sql, loanAgentDataMapper, new Object[] { clientId });
    }

    @Override
    public List<LoanAgentData> retrieveAll(){

        this.context.authenticatedUser();
        final LoanAgentDataMapper loanAgentDataMapper = new LoanAgentDataMapper();
        final String sql = "select " + loanAgentDataMapper.schema();
        return this.jdbcTemplate.query(sql, loanAgentDataMapper);

    }




}
