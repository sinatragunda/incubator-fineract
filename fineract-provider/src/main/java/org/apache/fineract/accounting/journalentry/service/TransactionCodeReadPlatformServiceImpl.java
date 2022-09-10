/*

    Created by Sinatra Gunda
    At 12:42 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.journalentry.data.TransactionCodeData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class TransactionCodeReadPlatformServiceImpl implements TransactionCodeReadPlatformService {


    private final JdbcTemplate jdbcTemplate;
    private final TransactionCodeMapper transactionCodeMapper = new TransactionCodeMapper();

    @Autowired
    public TransactionCodeReadPlatformServiceImpl(final RoutingDataSource routingDataSource){
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }

    @Override
    public TransactionCodeData retrieveOne(Long id) {

        List<Object> paramList = new ArrayList<>(Arrays.asList(id));
        final String sql = transactionCodeMapper.schema() + " where tc.id= ? ";

        TransactionCodeData transactionCodeData = this.jdbcTemplate.queryForObject(sql, transactionCodeMapper, paramList.toArray());
        return transactionCodeData ;

    }

    @Override
    public List<TransactionCodeData> retrieveAll() {
        final String sql = transactionCodeMapper.schema();
        List<TransactionCodeData> transactionCodeDataList = this.jdbcTemplate.query(sql, transactionCodeMapper);
        return transactionCodeDataList ;
    }


    private static final class TransactionCodeMapper implements RowMapper<TransactionCodeData> {

        public String schema() {
            return " select tc.id as id, tc.code as code, tc.name as name ,"
                    + "tc.debit_account_id as debitAccountId, tc.credit_account_id as creditAccountId,"
                    +" d.name as debitAccountName ,c.name as creditAccountName "
                    + " from m_transaction_code tc left join acc_gl_account d on d.id=tc.debit_account_id "
                    + "left join acc_gl_account c on c.id=tc.credit_account_id ";
        }

        @Override
        public TransactionCodeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long debitAccountId = JdbcSupport.getLong(rs, "debitAccountId");
            final Long creditAccountId = JdbcSupport.getLong(rs, "creditAccountId");
            final Long code = JdbcSupport.getLong(rs, "code");
            final String name = rs.getString("name");
            final String debitAccountName = rs.getString("debitAccountName");
            final String creditAccountName = rs.getString("creditAccountName");

            return new TransactionCodeData(id, code, name, debitAccountId, creditAccountId, debitAccountName, creditAccountName);
        }
    }

}
