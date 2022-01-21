/*

    Created by Sinatra Gunda
    At 4:53 PM on 1/6/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.commissions.data.AttachedCommissionChargesData;
import org.apache.fineract.portfolio.commissions.domain.AttachedCommissionCharges;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.LocalDate;

@Service
public class AttachedCommissionChargesReadPlatformServiceImpl implements AttachedCommissionChargesReadPlatformService {


    private JdbcTemplate jdbcTemplate ;
    private AttachedCommissionChargesDataMapper attachedCommissionChargesDataMapper = new AttachedCommissionChargesDataMapper();

    @Autowired
    public AttachedCommissionChargesReadPlatformServiceImpl(final RoutingDataSource routingDataSource){
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }


    private static final class AttachedCommissionChargesDataMapper implements RowMapper<AttachedCommissionChargesData> {

        public String schema() {
            return "acc.id as id ,"
                    + "acc.loan_from_agent_id as loansFromAgentsId ,"
                    + "acc.loan_commission_charge_id as commissionChargeId ,"
                    + "acc.is_deposited as isDeposited ,"
                    + "acc.amount as amount ,"
                    + "cc.charge_time_type as chargeTimeType ,"
                    + "lfa.loan_agent_id as loanAgentId ,"
                    + "lfa.loan_id as loanId ,"
                    + "la.client_id as clientId ,"
                    + "la.savings_account_id as savingsAccountId "
                    + "from m_attached_commission_charges acc "
                    + "join m_loans_from_agents lfa on acc.loan_from_agent_id = lfa.id "
                    + "join m_loan_commission_charge cc on acc.loan_commission_charge_id = cc.id "
                    + "join m_loan_agent la on lfa.loan_agent_id = la.id ";
        }

        @Override
        public AttachedCommissionChargesData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long loansFromAgentsId = rs.getLong("loansFromAgentsId");
            final Long commissionChargeId = rs.getLong("commissionChargeId");
            final Boolean isDeposited = rs.getBoolean("isDeposited");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final Integer chargeTimeTypeInt = rs.getInt("chargeTimeType");
            final Long loanAgentId = rs.getLong("loanAgentId");
            final Long clientId = rs.getLong("clientId");
            final Long savingsAccountId = rs.getLong("savingsAccountId");
            
            return AttachedCommissionChargesData.fullLoad(id, loansFromAgentsId,commissionChargeId ,amount,isDeposited ,chargeTimeTypeInt ,loanAgentId ,clientId ,savingsAccountId);
        }
    }


    @Override
    public List<AttachedCommissionChargesData> retrieveAllByLoanAgent(Long loanAgentId){

        String sql = "select "+attachedCommissionChargesDataMapper.schema() + "where acc.loan_from_agent_id = ?";
        return this.jdbcTemplate.query(sql ,attachedCommissionChargesDataMapper ,new Object[]{loanAgentId});

    }


    @Override
    public List<AttachedCommissionChargesData> retrieveAllByLoan(Long loanId){

        String sql = "select "+attachedCommissionChargesDataMapper.schema() + "where lfa.loan_id = ?";
        return this.jdbcTemplate.query(sql ,attachedCommissionChargesDataMapper ,new Object[]{loanId});

    }

}
