/*

    Created by Sinatra Gunda
    At 7:15 AM on 7/18/2022

*/
package org.apache.fineract.notification.service;


import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.notification.constants.EventSubscriptionConstants;
import org.apache.fineract.notification.data.EventMailListData;
import org.apache.fineract.notification.data.EventSubscriptionData;
import org.apache.fineract.notification.data.MailRecipientsKeyData;
import org.apache.fineract.notification.domain.EventMailList;
import org.apache.fineract.notification.enumerations.NOTIFICATION_BROADCAST_TYPE;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Service
public class EventMailListReadPlatformServiceImpl implements EventMailListReadPlatformService{

    private PlatformSecurityContext context ;
    private final JdbcTemplate jdbcTemplate;
    private EventMailListMapper eventMailListMapper = new EventMailListMapper();



    @Autowired
    public EventMailListReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<EventMailListData> retrieveWhereEvent(Long officeId , BUSINESS_EVENTS businessEvents){

        //this.context.authenticatedUser().validateHasPermissionTo(EventSubscriptionConstants.readPermission);

        final String sql = String.format("select %s where es.business_event = ? and es.office_id = ?" ,this.eventMailListMapper.schema());

        System.err.println("================sql =============="+sql);

        return this.jdbcTemplate.query(sql, this.eventMailListMapper,
                new Object[] { businessEvents ,officeId });

    }


    public List<EventMailListData> retrieveAll(){

        //this.context.authenticatedUser().validateHasPermissionTo(EventSubscriptionConstants.readPermission);

        String table ="m_event_mail_list";
        final String sql = String.format("select %s " ,this.eventMailListMapper.schema());

        System.err.println("================sql =============="+sql);

        return this.jdbcTemplate.query(sql, this.eventMailListMapper);

    }

    private static final class EventMailListMapper implements RowMapper<EventMailListData> {

        private final String schema;

        public EventMailListMapper(){

            final StringBuilder sqlBuilder = new StringBuilder(200);

            sqlBuilder.append("es.office_id as officeId, ");
            sqlBuilder.append("es.name as name, ");
            sqlBuilder.append("es.business_event as businessEvent, ");
            sqlBuilder.append("es.notification_broadcast_type as notificationType, ");
            sqlBuilder.append("es.message as message, ");
            sqlBuilder.append("es.id as eventSubscriptionId, ");
            sqlBuilder.append("eml.id as id, ");
            sqlBuilder.append("rk.name as recipientKeyName, ");
            sqlBuilder.append("rk.count as count, ");
            sqlBuilder.append("rk.select_all_mode as selectAllMode, ");
            sqlBuilder.append("o.name as officeName  ");

            sqlBuilder.append("from m_event_mail_list eml ");
            sqlBuilder.append("join m_event_subscription es on es.id = eml.event_subscription_id ");
            sqlBuilder.append("join m_mail_recipients_key rk on rk.id = eml.mail_recipient_key_id ");
            sqlBuilder.append("join m_office o on o.id= es.office_id ");
            this.schema = sqlBuilder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public EventMailListData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs ,"id");
            final Long eventSubscriptionId = JdbcSupport.getLong(rs,"eventSubscriptionId");
            final String name = rs.getString("name");

            final Integer businesEventEnum = JdbcSupport.getInteger(rs, "businessEvent");
            final BUSINESS_EVENTS businessEvents = BUSINESS_EVENTS.fromInt(businesEventEnum);

            final Long officeId = JdbcSupport.getLong(rs, "officeId");
            final String message = rs.getString("message");

            final String recipientKey = rs.getString("recipientKeyName");
            final Integer count = rs.getInt("count");
            final String officeName = rs.getString("officeName");
            final Boolean selectAllMode = rs.getBoolean("selectAllMode");

            final Integer notificationBroadcastTypeInt = JdbcSupport.getInteger(rs ,"notificationType");
            final NOTIFICATION_BROADCAST_TYPE notificationBroadcastType = NOTIFICATION_BROADCAST_TYPE.fromInt(notificationBroadcastTypeInt);

            final EventSubscriptionData eventSubscriptionData = new EventSubscriptionData(eventSubscriptionId ,name ,businessEvents ,notificationBroadcastType , officeId ,message);

            final MailRecipientsKeyData mailRecipientKeyData = new MailRecipientsKeyData(id ,recipientKey,officeId,officeName ,count ,selectAllMode);

            return new EventMailListData(id ,eventSubscriptionData ,mailRecipientKeyData);
        }
    }





}
