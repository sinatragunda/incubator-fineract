/*

    Created by Sinatra Gunda
    At 4:54 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.service;


import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.commissions.data.LoansFromAgentsData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;


@Service
public class LoansFromAgentsReadPlatformServiceImpl implements LoansFromAgentsReadPlatformService {


    private PlatformSecurityContext context ;
    private JdbcTemplate jdbcTemplate ;
    private LoansFromAgentsDataMapper loansFromAgentsDataMapper = new LoansFromAgentsDataMapper();
    private LoanRepository loanRepository ;


    @Autowired
    public LoansFromAgentsReadPlatformServiceImpl(final RoutingDataSource routingDataSource , final PlatformSecurityContext context ,final LoanRepository loanRepository){
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.context = context ;
        this.loanRepository = loanRepository ;
    }


    private static final class LoansFromAgentsDataMapper implements RowMapper<LoansFromAgentsData> {

        public String schema() {
            return "lfa.id as id, "
                    + "lfa.loan_id as loanId ,"
                    + "lfa.loan_agent_id as loanAgentId"
                    + "from m_loans_from_agents lfa ";
        }

        @Override
        public LoansFromAgentsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long loanId = rs.getLong("loanId");
            final Long loanAgentId = rs.getLong("loanAgentId");
            final Long loanCommissionChargeId = rs.getLong("loanCommissionChargeId");
            final Boolean isDeposited = rs.getBoolean("isDeposited");

            return new LoansFromAgentsData(id, loanId ,loanAgentId ,loanCommissionChargeId,isDeposited);
        }
    }


    private LoansFromAgentsData retrieveWhere(String query){
        this.context.authenticatedUser();
        final String sql = "select "+ loansFromAgentsDataMapper.schema()+" "+query;
        return this.jdbcTemplate.queryForObject(sql, loansFromAgentsDataMapper);

    }

    // always one unique agent per client
    @Override
    public LoansFromAgentsData retrieveOne(Long id){

        String where = String.format("where lfa.id = %d" ,id);
        return retrieveWhere(where);

    }

    // always one unique agent per client
    @Override
    public LoansFromAgentsData retrieveOneByLoan(Long loanId){
        final String sql = String.format("where lfa.loan_id=%d",loanId);
        return retrieveWhere(sql);
    }


    // use client id to get loan agent id or ?
    @Override
    public List<Loan> retrieveAllLoansForAgent(Long agentId){

        this.context.authenticatedUser();
        final String sql = "select "+ loansFromAgentsDataMapper.schema()+"where lfa.loan_agent_id =? ";
        List<LoansFromAgentsData> loansFromAgentsData =  this.jdbcTemplate.query(sql, loansFromAgentsDataMapper ,new Object[] { agentId});
        // should we build these loans all or
        List<Loan> loanList = new ArrayList<>();
        loansFromAgentsData.stream().forEach(e->{
            Long loanId = e.getLoanId();
            Loan loan = loanRepository.findOne(loanId);
            loanList.add(loan);
        });

        System.err.println("----------------loans list is ---------------"+loanList.size());

        return loanList;

    }

    @Override
    public List<LoansFromAgentsData> retrieveAllForCommissionCharge(Long chargeId){

        this.context.authenticatedUser();
        final String sql = "select "+ loansFromAgentsDataMapper.schema()+"where lc.loanCommissionChargeId =? ";
        return this.jdbcTemplate.query(sql, loansFromAgentsDataMapper ,chargeId);

    }


}
