/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 20 March 2023 at 12:25
 */
package org.apache.fineract.portfolio.paymentrules.service;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.paymentrules.data.PaymentRuleData;

import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.paymentrules.data.PaymentSequenceData;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentRulesReadServiceImpl implements PaymentRulesReadService{

    private JdbcTemplate jdbcTemplate ;
    private PaymentRuleMapper paymentRuleMapper = new PaymentRuleMapper();
    private PaymentSequenceMapper paymentSequenceMapper = new PaymentSequenceMapper();

    private Consumer<PaymentRuleData> consumeRules = (e)->{

        boolean isPresent = OptionalHelper.isPresent(e);
        if(isPresent){
            Long id = e.getId();
            String sqlEx = "select "+paymentSequenceMapper.schema()+" where ps.payment_rule_id = ? ";
            List<PaymentSequenceData> paymentSequenceDataCollection = this.jdbcTemplate.query(sqlEx ,paymentSequenceMapper ,new Object[]{id});
            e.setPaymentSequenceDataList(paymentSequenceDataCollection);
        }
    };

    @Autowired
    public PaymentRulesReadServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public PaymentRuleData template() {
        return PaymentRuleData.template();
    }

    @Override
    public PaymentRuleData retrieveOne(Long id) {

        final String sql = "select " + paymentRuleMapper.schema() + "where pr.id = ?";
        PaymentRuleData paymentRuleData = this.jdbcTemplate.queryForObject(sql, paymentRuleMapper, new Object[] { id });
        consumeRules.accept(paymentRuleData);
        return paymentRuleData;
    }

    @Override
    public List<PaymentRuleData> retrieveAll(Long officeId ,boolean hasOffice) {
        
        Object[] params = {};
        String sql = "select " + paymentRuleMapper.schema();

        if(hasOffice){
            sql = sql+" where pr.office_id = ?";
            params = new Object[]{officeId};
        }
        
        List<PaymentRuleData> paymentRuleDataList = this.jdbcTemplate.query(sql, paymentRuleMapper, params);
        paymentRuleDataList.stream().forEach(consumeRules);
        return paymentRuleDataList;
    }

    private static final class PaymentRuleMapper implements RowMapper<PaymentRuleData> {

        public String schema() {
            String sql = " pr.id as id, " +
                    "pr.name as name, pr.office_id as officeId," +
                    "o.name as officeName "+
                    "from m_payment_rule pr "+
                    "left join m_office o on o.id = pr.office_id ";

            return sql ;
        }

        @Override
        public PaymentRuleData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");

            return new PaymentRuleData(id, name, officeName ,officeId);
        }
    }

    private static final class PaymentSequenceMapper implements RowMapper<PaymentSequenceData> {

        public String schema() {
            String sql = " ps.id as id, " +
                    "ps.value as value, ps.ref_table as refTable," +
                    "ps.sequence_number as sequenceNumber, "+
                    "ps.payment_code as paymentCode "+
                    "from m_payment_rule_sequence ps ";

            return sql ;
        }

        @Override
        public PaymentSequenceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String value = rs.getString("value");
            final Integer sequenceNumber = rs.getInt("sequenceNumber");
            final Integer refTableInt = rs.getInt("refTable");
            final Integer paymentCodeInt = rs.getInt("paymentCode");

            return new PaymentSequenceData(id,sequenceNumber ,value, REF_TABLE.fromInt(refTableInt) , PAYMENT_CODE.fromInt(paymentCodeInt));
        }
    }
}
