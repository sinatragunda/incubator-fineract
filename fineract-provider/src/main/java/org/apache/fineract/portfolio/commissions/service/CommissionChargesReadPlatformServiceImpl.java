/*

    Created by Sinatra Gunda
    At 11:19 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.commissions.data.CommissionChargeData;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

@Service
public class CommissionChargesReadPlatformServiceImpl implements CommissionChargesReadPlatformService {

    private PlatformSecurityContext context ;
    private CurrencyReadPlatformService currencyReadPlatformService ;
    private CommissionChargeDropdownReadPlatformService commissionChargeDropdownReadPlatformService;
    private RoutingDataSource routingDataSource ;
    private JdbcTemplate jdbcTemplate ;
    private CommissionChargeDataMapper commissionChargeDataMapper = new CommissionChargeDataMapper();

    @Autowired
    public CommissionChargesReadPlatformServiceImpl(PlatformSecurityContext context, CurrencyReadPlatformService currencyReadPlatformService, CommissionChargeDropdownReadPlatformService commissionChargeDropdownReadPlatformService ,RoutingDataSource routingDataSource) {
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.commissionChargeDropdownReadPlatformService = commissionChargeDropdownReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.context = context ;
    }


    private static final class CommissionChargeDataMapper implements RowMapper<CommissionChargeData> {

        public String schema() {
            return "cc.id as id, "
                    + "cc.name as name ,"
                    + "cc.amount as amount ,"
                    + "cc.charge_applies_to as chargeAppliesTo ,"
                    + "cc.charge_calculation_type as chargeCalculationType ,"
                    + "cc.currency_code as currencyCode, "
                    + "cc.charge_time_type as chargeTimeType ,"
                    + "cc.is_active as isActive "
                    + "from m_loan_commission_charge cc ";
        }

        @Override
        public CommissionChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String currencyCode = rs.getString("currencyCode");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final Integer chargeAppliesToInt = rs.getInt("chargeAppliesTo");
            final Integer chargeCalculationTypeInt = rs.getInt("chargeCalculationType");
            final Integer chargeTimeTypeInt = rs.getInt("chargeTimeType");

            final EnumOptionData chargeAppliesTo = ChargeEnumerations.chargeAppliesTo(chargeAppliesToInt);
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculationTypeInt);
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTimeTypeInt);

            final Boolean isActive = rs.getBoolean("isActive");

            return CommissionChargeData.instance(id, name ,currencyCode ,amount,chargeAppliesTo ,chargeCalculationType ,chargeTimeType ,isActive);
        }
    }


    @Override
    public CommissionChargeData retrieveNewCommissionChargeTemplate() {

            final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
            final List<EnumOptionData> allowedCalculationTypeOptions = this.commissionChargeDropdownReadPlatformService.retrieveCalculationTypes();
            final List<EnumOptionData> allowedAppliesToOptions = this.commissionChargeDropdownReadPlatformService.retrieveApplicableToTypes();
            final List<EnumOptionData> allowedCollectionTimeOptions = this.commissionChargeDropdownReadPlatformService.retrieveCollectionTimeTypes();

            return CommissionChargeData.template(currencyOptions, allowedCalculationTypeOptions, allowedAppliesToOptions,
                    allowedCollectionTimeOptions);

    }

    @Override
    public List<CommissionChargeData> retrieveAll(){

        this.context.authenticatedUser();
        final String sql = "select "+ commissionChargeDataMapper.schema();
        return this.jdbcTemplate.query(sql, commissionChargeDataMapper);

    }

    @Override
    public CommissionChargeData retrieveOne(Long id){

        this.context.authenticatedUser();
        final String sql = "select "+ commissionChargeDataMapper.schema()+" where cc.id = ?";
        return this.jdbcTemplate.queryForObject(sql, commissionChargeDataMapper ,new Object[]{id});

    }


}
