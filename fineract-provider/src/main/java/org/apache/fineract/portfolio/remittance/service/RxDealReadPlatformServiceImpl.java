package org.apache.fineract.portfolio.remittance.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.remittance.data.RxData;
import org.apache.fineract.portfolio.remittance.data.RxDealData;
import org.apache.fineract.portfolio.remittance.enumerations.IDENTIFICATION_TYPE;
import org.apache.fineract.portfolio.remittance.enumerations.RX_DEAL_STATUS;
import org.apache.fineract.portfolio.remittance.enumerations.RX_PROVIDER;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.products.enumerations.ACCOUNT_TYPE;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Service;
import org.joda.time.LocalDate;


@Service
public class RxDealReadPlatformServiceImpl implements RxDealReadPlatformService {

    private PlatformSecurityContext context ;
    private SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private JdbcTemplate jdbcTemplate ;

    @Autowired
    public RxDealReadPlatformServiceImpl(final PlatformSecurityContext context , final RoutingDataSource routingDataSource,  final SavingsAccountReadPlatformService savingsAccountReadPlatformService) {
        this.context = context;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }

    public RxData template(){

        Collection<EnumOptionData> rxDealStatusOptions = RX_DEAL_STATUS.template();
        Collection<EnumOptionData> providerOptions = RX_PROVIDER.template();
        Collection<EnumOptionData> identificationTypeOptions = IDENTIFICATION_TYPE.template();
        Collection<SavingsAccountData> savingsAccounts = savingsAccountReadPlatformService.retrieveAllByAccountType(ACCOUNT_TYPE.SETTLEMENT);

        return new RxData(providerOptions ,identificationTypeOptions ,rxDealStatusOptions ,savingsAccounts);
    }


    private static final class RxDealDataMapper implements RowMapper<RxDealData> {

        public String schema() {

            return "SELECT \n" +
                    "rx.id as id ,\n"+
                    "rx.rx_key as rxKey ,\n"+
                    "mc.id AS clientId ,\n" +
                    "mc.mobile_no as phoneNumber, \n"+
                    "mc.display_name AS clientName,\n" +
                    "mc.email_address AS emailAddress,\n" +
                    "rx.transaction_date AS transactionDate,\n" +
                    "rx.provider AS provider ,\n" +
                    "rx.amount AS amount ,\n" +
                    "rx.status AS status ,\n" +
                    "rx.currency_code as currencyCode ,\n"+
                    "ifnull(rxr.name ,rx.receiver_name) AS receiverName,\n" +
                    "ifnull(rxr.phone_number ,rx.receiver_phone_number) as receiverPhoneNumber, \n"+
                    "rxr.transaction_date AS collectionDate,\n" +
                    "mo.name AS officeName,\n" +
                    "mo.id AS officeId ,\n" +
                    "mst.id AS savingsAccountTransactionId,\n" +
                    "mst.savings_account_id AS payinAccountId\n" +
                    "FROM m_rx_deal rx\n" +
                    "JOIN m_client mc ON mc.id = rx.client_id\n" +
                    "JOIN m_office mo ON mo.id = rx.office_id\n" +
                    "JOIN m_savings_account_transaction mst ON mst.id = rx.savings_account_transaction_id\n" +
                    "left JOIN m_rx_deal_receive rxr ON rxr.rx_deal_id = rx.id\n" +
                    "\n" +
                    " ";
        }
        @Override
        public RxDealData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientName = rs.getString ("clientName");
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs ,"amount");
            final Long savingsAccountTransactionId = JdbcSupport.getLong(rs, "savingsAccountTransactionId");
            final Integer providerInt = JdbcSupport.getInteger(rs, "provider");
            final RX_PROVIDER rxProvider = RX_PROVIDER.fromInt(providerInt);
            final String phoneNumber = rs.getString("phoneNumber");
            final LocalDate transactionDate =JdbcSupport.getLocalDate(rs ,"transactionDate");

            final Integer statusInt = JdbcSupport.getInteger(rs ,"status");
            final RX_DEAL_STATUS rxDealStatus = RX_DEAL_STATUS.fromInt(statusInt);
            final String emailAddress = rs.getString("emailAddress");

            final String receiverName = rs.getString("receiverName");
            final String receiverPhoneNumber = rs.getString("receiverPhoneNumber");
            //final String receiverEmailAddress = rs.getString("receiverEmailAddress");

            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final String currencyCode = rs.getString("currencyCode");
            final Long payinAccountId = rs.getLong("payinAccountId");

            final String key = rs.getString("rxKey");

            final RxDealData rxDealData = new RxDealData(id,emailAddress ,clientName ,phoneNumber ,receiverName ,receiverPhoneNumber ,rxProvider ,transactionDate ,officeId ,amount ,payinAccountId ,clientId ,currencyCode ,officeName ,savingsAccountTransactionId ,rxDealStatus ,key);

            return rxDealData ;
        }
    }

    @Override
    public RxDealData retreiveOne(final String key) {

        final String condition = " where rx.rx_key = ?";
        final Object[] params = new Object[]{key};
        return findWhere(condition ,params);

    }

    /**
     * Added 06/11/2022 at 2218
     */
    @Override
    public RxDealData retreiveOne(final Long id){

        final String condition = " where rx.id = ?";
        final Object[] params = new Object[]{id};
        return findWhere(condition ,params);
    }
    @Override
    public Collection<RxDealData> retreiveAll(){
        try {
            final RxDealDataMapper rxDealDataMapper = new RxDealDataMapper();
            final String sql = rxDealDataMapper.schema() ;
            return this.jdbcTemplate.query(sql, rxDealDataMapper);
        } catch (final EmptyResultDataAccessException e) {
            //throw new In(noteId, resourceId, noteType.name().toLowerCase());
        }
        return null ;
    }

    private RxDealData findWhere(String condition ,Object[] params){

        try {
            final RxDealDataMapper rxDealDataMapper = new RxDealDataMapper();

            final StringBuilder sqlBuilder = new StringBuilder() ;
            sqlBuilder.append(rxDealDataMapper.schema());
            sqlBuilder.append(condition);

            final String sql = sqlBuilder.toString();

            return this.jdbcTemplate.queryForObject(sql, rxDealDataMapper ,params);
        } catch (final EmptyResultDataAccessException e) {
            //throw new In(noteId, resourceId, noteType.name().toLowerCase());
        }
        return null ;

    }





}
