/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 23 March 2023 at 10:17
 */
package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.paymentrules.data.PaymentRuleData;
import org.apache.fineract.portfolio.paymentrules.service.PaymentRulesReadService;
import org.apache.fineract.portfolio.paymentrules.service.PaymentRulesReadServiceImpl;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.data.SavingsProductPropertiesData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SavingsProductPropertiesReadPlatformServiceImpl implements SavingsProductPropertiesReadPlatformService {

    private JdbcTemplate jdbcTemplate;
    private PaymentRulesReadService paymentRulesReadService ;
    private SavingProductPropertiesMapper savingProductPropertiesMapper = new SavingProductPropertiesMapper();

    @Autowired
    public SavingsProductPropertiesReadPlatformServiceImpl(final RoutingDataSource routingDataSource ,PaymentRulesReadService paymentRulesReadService) {
        this.paymentRulesReadService = paymentRulesReadService;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }

    @Override
    public SavingsProductPropertiesData retrieveOne(Long productId) {

        final String sql = String.format("select %s where spp.savings_product_id = ?" ,savingProductPropertiesMapper.schema());
        final SavingsProductPropertiesData savingsProductPropertiesData = this.jdbcTemplate.queryForObject(sql ,savingProductPropertiesMapper ,new Object[]{productId});
        return savingsProductPropertiesData;
    }

    @Override
    public SavingsProductPropertiesData template(Long officeId) {

        List<PaymentRuleData> paymentRuleDataList = paymentRulesReadService.retrieveAll(officeId ,false);
        return new SavingsProductPropertiesData(paymentRuleDataList);
    }


    private static final class SavingProductPropertiesMapper implements RowMapper<SavingsProductPropertiesData> {

        public String schema() {

            return " spp.id as id ," +
                    "pr.id as paymentRuleId ,"+
                    "pr.name as paymentRuleName "+
                    "from m_savings_product_properties spp "+
                    "left join m_payment_rule pr on pr.id = spp.payment_rule_id ";
        }

        @Override
        public SavingsProductPropertiesData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum){
            try {
                final Long id = rs.getLong("id");
                final String paymentRuleName = rs.getString("paymentRuleName");
                final Long paymentRuleId = rs.getLong("paymentRuleId");
                return new SavingsProductPropertiesData(id, paymentRuleId, paymentRuleName);
            }
            catch (SQLException s){
                s.printStackTrace();
            }
            return null ;
        }
    }
}
