/*

    Created by Sinatra Gunda
    At 12:20 AM on 1/3/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.commissions.data.LoanAgentData;

import java.sql.ResultSet;
import java.sql.SQLException;


@Service
public class LoanAgentReadPlatformServiceImpl implements LoanAgentReadPlatformService{

    private PlatformSecurityContext context ;


    @Autowired


    private static final class LoanAgentDataMapper implements RowMapper<LoanAgentData> {

        public String schema() {
            return "la.id as id, "
                    + "la.client_id as clientId ,"
                    + "la.savings_account_id as savingsAccountId "
                    + "from m_loan_agent la ";
        }

        @Override
        public LoanAgentData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long clientId = rs.getLong("clientId");
            final Long savingsAccountId = rs.getLong("savingsAccountId");

            return new LoanAgentData(id, clientId ,savingsAccountId);
        }
    }


    @Override
    public LoanAgentData retrieveOne(Long id){

        this.context.authenticatedUser();
        final LoanAgentDataMapper loanAgentDataMapper = new LoanAgentDataMapper();
        final String sql = "select " + loanAgentDataMapper.schema() + " where la.id=?";
        return this.jdbcTemplate.queryForObject(sql, loanAgentDataMapper, new Object[] { id, id });
    }

}
