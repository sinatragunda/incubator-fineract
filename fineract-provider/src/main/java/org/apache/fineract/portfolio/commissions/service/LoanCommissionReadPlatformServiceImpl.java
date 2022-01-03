/*

    Created by Sinatra Gunda
    At 4:54 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.service;


import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.commissions.data.LoanAgentData;
import org.apache.fineract.portfolio.commissions.data.LoanCommissionData;
import org.apache.fineract.portfolio.commissions.domain.LoanCommission;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;


@Service
public class LoanCommissionReadPlatformServiceImpl implements LoanCommissionReadPlatformService {


    private PlatformSecurityContext context ;
    private JdbcTemplate jdbcTemplate ;
    private LoanCommissionDataMapper loanCommissionDataMapper = new LoanCommissionDataMapper();


    @Autowired
    public LoanCommissionReadPlatformServiceImpl(final RoutingDataSource routingDataSource , final PlatformSecurityContext context){
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.context = context ;
    }


    private static final class LoanCommissionDataMapper implements RowMapper<LoanCommissionData> {

        public String schema() {
            return "lc.id as id, "
                    + "lc.loan_id as loanId ,"
                    + "lc.loan_commission_charge_id as loanCommissionChargeId, "
                    + "lc.loan_agent_id as loanAgentId ,"
                    + "lc.is_deposited as isDeposited "
                    + "from m_loan_commission lc ";
        }

        @Override
        public LoanCommissionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long loanId = rs.getLong("loanId");
            final Long loanAgentId = rs.getLong("loanAgentId");
            final Long loanCommissionChargeId = rs.getLong("loanCommissionChargeId");
            final Boolean isDeposited = rs.getBoolean("isDeposited");

            return new LoanCommissionData(id, loanId ,loanAgentId ,loanCommissionChargeId,isDeposited);
        }
    }


    private LoanCommissionData retrieveWhere(String query){
        this.context.authenticatedUser();
        final String sql = "select "+ loanCommissionDataMapper.schema()+" "+query;
        return this.jdbcTemplate.queryForObject(sql, loanCommissionDataMapper);

    }

    // always one unique agent per client
    @Override
    public LoanCommissionData retrieveOne(Long id){

        String where = String.format("where lc.id = %d" ,id);
        return retrieveWhere(where);

    }

    // always one unique agent per client
    @Override
    public LoanCommissionData retrieveOneByLoan(Long loanId){
        final String sql = String.format("where la.loanId=%d",loanId);
        return retrieveWhere(sql);
    }


    @Override
    public List<LoanCommissionData> retrieveAllForAgent(Long agentId){

        this.context.authenticatedUser();
        final String sql = "select "+ loanCommissionDataMapper.schema()+"where lc.loanAgentId =? ";
        return this.jdbcTemplate.query(sql, loanCommissionDataMapper ,agentId);

    }

    @Override
    public List<LoanCommissionData> retrieveAllForCommissionCharge(Long chargeId){

        this.context.authenticatedUser();
        final String sql = "select "+ loanCommissionDataMapper.schema()+"where lc.loanCommissionChargeId =? ";
        return this.jdbcTemplate.query(sql, loanCommissionDataMapper ,chargeId);

    }


}
