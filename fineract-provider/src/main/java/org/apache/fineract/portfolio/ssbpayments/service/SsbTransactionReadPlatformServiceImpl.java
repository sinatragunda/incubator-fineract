/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 31 May 2023 at 07:21
 */
package org.apache.fineract.portfolio.ssbpayments.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.ssbpayments.data.SsbTransactionData;
import org.apache.fineract.portfolio.ssbpayments.data.SsbTransactionRecordData;
import org.apache.fineract.portfolio.ssbpayments.domain.SsbTransaction;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.portfolio.ssbpayments.domain.SsbTransactionRecord;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.taat.wese.weseaddons.ssb.enumerations.PORTFOLIO_TYPE;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.taat.wese.weseaddons.ssb.enumerations.PROCESS_STATUS;

@Service
public class SsbTransactionReadPlatformServiceImpl implements SsbTransactionReadPlatformService{

    private SsbTransactionRecordDataMapper ssbTransactionRecordDataMapper = new SsbTransactionRecordDataMapper();
    private SsbTransactionDataMapper ssbTransactionDataMapper = new SsbTransactionDataMapper();
    private JdbcTemplate jdbcTemplate ;

    @Autowired
    public SsbTransactionReadPlatformServiceImpl(final RoutingDataSource routingDataSource){
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }

    private Consumer<SsbTransactionRecordData> attachTransactions = (e)->{


        final Long id = e.getId();

        final String sql = String.format("select %s where ssb.ssb_transaction_record_id = ? ",ssbTransactionDataMapper.schema());

        Collection<SsbTransactionData> ssbTransactionDataCollection = this.jdbcTemplate.query(sql, this.ssbTransactionDataMapper ,new Object[]{id});

        System.err.println("------------results are "+ssbTransactionDataCollection.size());

        e.setSsbTransactionDataCollection(ssbTransactionDataCollection);

    };

    @Override
    public SsbTransactionRecordData retrieveOne(Long id){

        final String sql = String.format("select %s where ssb.id = ? ",ssbTransactionRecordDataMapper.schema());

        SsbTransactionRecordData ssbTransactionRecordData = this.jdbcTemplate.queryForObject(sql, this.ssbTransactionRecordDataMapper ,new Object[]{id});
        attachTransactions.accept(ssbTransactionRecordData);
        return ssbTransactionRecordData;

    }

    @Override
    public Collection<SsbTransactionRecordData> retrieveAll() {

        final String sql = String.format("select %s ",ssbTransactionRecordDataMapper.schema());

        Collection<SsbTransactionRecordData> ssbTransactionRecordDataCollection = this.jdbcTemplate.query(sql, this.ssbTransactionRecordDataMapper);
        return ssbTransactionRecordDataCollection;
    }


    private static final class SsbTransactionDataMapper implements RowMapper<SsbTransactionData> {

        private final String schema;

        public SsbTransactionDataMapper() {

            final StringBuilder builder = new StringBuilder(400);

            builder.append("ssb.id as id, ");
            builder.append("ssb.portfolio_type as portfolioType,");
            builder.append("ssb.amount as amount ,");
            builder.append("ssb.transaction_id as transactionId ,");
            builder.append("mc.display_name as clientName ");
            builder.append("from m_ssb_transaction ssb ");
            builder.append("join m_client mc on mc.id = ssb.client_id");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public SsbTransactionData mapRow(ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String clientName = rs.getString("clientName");
            final BigDecimal amount = rs.getBigDecimal("amount");

            final Integer portfolioTypeInt = rs.getInt("portfolioType");
            final PORTFOLIO_TYPE portfolioType = PORTFOLIO_TYPE.fromInt(portfolioTypeInt);

            final Long transactionId = rs.getLong("transactionId");

            SsbTransactionData ssbTransactionData = new SsbTransactionData(id ,null ,transactionId ,clientName ,amount ,portfolioType);
            return ssbTransactionData;

        }
    }



    private static final class SsbTransactionRecordDataMapper implements RowMapper<SsbTransactionRecordData> {

        private final String schema;

        public SsbTransactionRecordDataMapper() {

            final StringBuilder builder = new StringBuilder(400);

            builder.append("ssb.id as id, ");
            builder.append("ssb.filename as filename,");
            builder.append("ssb.process_status as processStatus ,");
            builder.append("ssb.process_details as processDetails, ");
            builder.append("a.firstname as createdBy ");
            builder.append("from m_ssb_transaction_record ssb ");
            builder.append("join m_appuser a on a.id = ssb.created_by");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public SsbTransactionRecordData mapRow(ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String filename = rs.getString("filename");
            final String processDetails = rs.getString("processDetails");
            final String createdBy = rs.getString("createdBy");

            final Integer processStatusInt = rs.getInt("processStatus");
            final PROCESS_STATUS processStatus = (PROCESS_STATUS) EnumTemplateHelper.fromInt(PROCESS_STATUS.values() ,processStatusInt);

            SsbTransactionRecordData ssbTransactionRecordData = new SsbTransactionRecordData(id ,filename ,createdBy ,null ,processDetails ,processStatus);
            return ssbTransactionRecordData;

        }
    }

}
