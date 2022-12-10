/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 16:36
 */
package org.apache.fineract.portfolio.localref.service;


import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.data.ClientNonPersonData;
import org.apache.fineract.portfolio.client.data.ClientTimelineData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.enumerations.REF_VALUE_TYPE;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.Permission;
import org.apache.fineract.useradministration.service.PermissionReadPlatformServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
public class LocalRefReadPlatformServiceImpl implements LocalRefReadPlatformService{

    private final PermissionReadPlatformServiceImpl permissionReadPlatformService;
    private final CodeReadPlatformService codeReadPlatformService;
    private final String localRefTable = "m_local_ref";
    private final LocalRefMapper localRefMapper = new LocalRefMapper();
    private final JdbcTemplate jdbcTemplate ;

    @Autowired
    public LocalRefReadPlatformServiceImpl(PermissionReadPlatformServiceImpl permissionReadPlatformService ,CodeReadPlatformService codeReadPlatformService ,RoutingDataSource routingDataSource){
        this.permissionReadPlatformService = permissionReadPlatformService;
        this.codeReadPlatformService = codeReadPlatformService ;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);

    }

    @Override
    public LocalRefData template(REF_TABLE refTable) {

        Collection codeDataCollection = codeReadPlatformService.retrieveAllCodes();
        LocalRefData localRefData = LocalRefData.template();
        localRefData.setCodeData(codeDataCollection);

        Collection existingRefs = retrieveEmptyLocalRefs(refTable);
        localRefData.setExistingLocalRefs(existingRefs);
        return localRefData;

    }

    @Override
    public Collection<LocalRefData> retrieveAll(Long officeId) {

        final String sql = String.format("select %s where lr.office_id = ? ",localRefMapper.schema());
        Collection<LocalRefData> localRefDataCollection = this.jdbcTemplate.query(sql, this.localRefMapper, new Object[] {officeId});
        return localRefDataCollection ;
    }

    /**
     * Added 09/12/2022 at 0040
     * Just retrieve empty local refs to aid in templating ie filling data values
     */
    private Collection<LocalRefData> retrieveEmptyLocalRefs(final REF_TABLE refTable) {

        final String sql = String.format("select %s where lr.ref_table = ?",localRefMapper.schema());

        return this.jdbcTemplate.query(sql, this.localRefMapper, new Object[] { refTable});
    }

    private static final class LocalRefMapper implements RowMapper<LocalRefData> {

        private final String schema;

        public LocalRefMapper() {

            final StringBuilder builder = new StringBuilder(400);

            builder.append("lr.id as id, ");
            builder.append("lr.name as name, ");
            builder.append("lr.description as description, ");
            builder.append("lr.code_id as codeId, ");
            builder.append("mc.code_name as codeName, ");
            builder.append("o.name as officeName, ");
            builder.append("lr.ref_value_type as valueType, ");
            builder.append("lr.ref_table as refTable ");
            builder.append("from m_local_ref lr ");
            builder.append("left join m_office o on o.id = lr.office_id ");
            builder.append("left join m_code mc on mc.id = lr.code_id ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public LocalRefData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final Long id = rs.getLong("id");
            final Long codeId = rs.getLong("codeId");
            final String codeName = rs.getString("codeName");
            final String officeName = rs.getString("officeName");

            final Integer valueTypeInt = rs.getInt("valueType");
            final REF_VALUE_TYPE refValueType = REF_VALUE_TYPE.fromInt(valueTypeInt);

            final Integer refTableInt = rs.getInt("refTable");
            final REF_TABLE refTable = REF_TABLE.fromInt(refTableInt);

            LocalRefData localRefData = new LocalRefData(id ,name ,description,refTable ,refValueType,codeName ,officeName);
            return localRefData;
        }
    }

}
