/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 April 2023 at 10:18
 */
package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionEnumData;
import org.apache.fineract.utility.helper.EnumeratedDataHelper;
import org.apache.fineract.utility.service.EnumeratedData;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.joda.time.LocalDate;


@Service
public class SavingsAccountTransactionReadPlatformServiceImpl implements SavingsAccountTransactionReadPlatformService {

    private SavingsAccountTransactionsMapper savingsAccountTransactionsMapper = new SavingsAccountTransactionsMapper();
    private final JdbcTemplate jdbcTemplate ;

    @Autowired
    public SavingsAccountTransactionReadPlatformServiceImpl(final RoutingDataSource routingDataSource){
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }


    @Override
    public Collection<SavingsAccountTransactionData> retrieveAll(){

        String sql = String.format("select %s",savingsAccountTransactionsMapper.schema());
        return  jdbcTemplate.query(sql ,savingsAccountTransactionsMapper);
    }

    @Override
    public List<EnumOptionData> getDropdownData() {
        Collection collection = retrieveAll();
        return EnumeratedDataHelper.enumeratedData(collection);
    }

    @Override
    public Collection<? extends EnumeratedData> retrieveUsingQuery(String whereSql) {

        String sql = String.format("select %s where %s" ,savingsAccountTransactionsMapper.schema() ,whereSql);
        return this.jdbcTemplate.query(sql ,savingsAccountTransactionsMapper);
    }


    private static final class SavingsAccountTransactionsMapper implements RowMapper<SavingsAccountTransactionData> {
        private final String schemaSql;

        public SavingsAccountTransactionsMapper() {

            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("tr.id as transactionId, tr.transaction_type_enum as transactionType, ");
            sqlBuilder.append("tr.transaction_date as transactionDate, tr.amount as transactionAmount,");
            sqlBuilder.append("nt.note as transactionNote, ") ;
            sqlBuilder.append("sa.currency_code as currency,");
            sqlBuilder.append("mc.display_name as clientName, ");
            sqlBuilder.append("tr.running_balance_derived as runningBalance, tr.is_reversed as reversed,");
            sqlBuilder.append("sa.id as savingsId, sa.account_no as accountNo,");
            sqlBuilder.append("tr.is_manual as postInterestAsOn ");
            sqlBuilder.append("from m_savings_account_transaction tr ");
            sqlBuilder.append("join m_savings_account sa on tr.savings_account_id = sa.id ");
            sqlBuilder.append("join m_client mc on mc.id = sa.client_id ");
            sqlBuilder.append(" left join m_note nt ON nt.savings_account_transaction_id=tr.id ") ;
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("transactionId");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final SavingsAccountTransactionEnumData transactionType = SavingsEnumerations.transactionType(transactionTypeInt);

            final String clientName = rs.getString("clientName");

            final LocalDate date = JdbcSupport.getLocalDate(rs, "transactionDate");
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "transactionAmount");
            final BigDecimal runningBalance = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "runningBalance");
            final boolean reversed = rs.getBoolean("reversed");

            final Long savingsId = rs.getLong("savingsId");
            final String accountNo = rs.getString("accountNo");

            final String currencyCode = rs.getString("currency");

            final String note = rs.getString("transactionNote") ;
            return new SavingsAccountTransactionData(id, transactionType, savingsId, accountNo, date, currencyCode,
                    amount, runningBalance, reversed, note ,clientName);
        }
    }


}
