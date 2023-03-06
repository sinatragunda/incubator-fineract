/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 16:36
 */
package org.apache.fineract.portfolio.localref.service;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.domain.Code;
import org.apache.fineract.infrastructure.codes.service.CodeReadPlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.data.ClientNonPersonData;
import org.apache.fineract.portfolio.client.data.ClientTimelineData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.data.LocalRefValueData;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.domain.LocalRefValue;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.enumerations.REF_VALUE_TYPE;
import org.apache.fineract.portfolio.localref.repo.LocalRefValueRepository;
import org.apache.fineract.portfolio.localref.repo.LocalRefValueRepositoryWrapper;
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
import java.util.*;
import java.util.function.Consumer;

@Service
public class LocalRefReadPlatformServiceImpl implements LocalRefReadPlatformService{

    private final PermissionReadPlatformServiceImpl permissionReadPlatformService;
    private final CodeReadPlatformService codeReadPlatformService;
    private final String localRefTable = "m_local_ref";
    private final LocalRefMapper localRefMapper = new LocalRefMapper();
    private final LocalRefValueMapper localRefValueMapper = new LocalRefValueMapper();
    private final JdbcTemplate jdbcTemplate ;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final LocalRefValueRepositoryWrapper localRefValueRepositoryWrapper;

    @Autowired
    public LocalRefReadPlatformServiceImpl(PermissionReadPlatformServiceImpl permissionReadPlatformService ,CodeReadPlatformService codeReadPlatformService ,RoutingDataSource routingDataSource ,CodeValueReadPlatformService codeValueReadPlatformService ,LocalRefValueRepositoryWrapper localRefValueRepositoryWrapper){
        this.permissionReadPlatformService = permissionReadPlatformService;
        this.codeReadPlatformService = codeReadPlatformService ;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.localRefValueRepositoryWrapper = localRefValueRepositoryWrapper;

    }

    @Override
    public LocalRefData template(REF_TABLE refTable) {

        Collection codeDataCollection = codeReadPlatformService.retrieveAllCodes();
        LocalRefData localRefData = LocalRefData.template();
        localRefData.setCodeData(codeDataCollection);

        Collection existingRefs = retrieveEmptyLocalRefs(refTable);

        //System.err.println("--------------------existing local refs "+existingRefs.size());

        localRefData.setExistingLocalRefs(existingRefs);
        return localRefData;

    }

    /**
     * Added 24/02/2023 at 1146
     * Retrieve record from LocalRefValues 
     */  
    @Override
    public Collection<LocalRefValueData> retrieveRecord(REF_TABLE refTable, Long recordId){

        String sqlTemp  = localRefValueMapper.schema();
        
        final String sql = String.format("select %s where lr.ref_table = ? and lrv.record_id = ? ",sqlTemp);
        
        //System.err.println("-====================record sql is ==========="+sql);
        
        Collection<LocalRefValueData> localRefValueDataCollection = this.jdbcTemplate.query(sql, this.localRefValueMapper, new Object[] {refTable.ordinal() ,recordId ,refTable.ordinal() ,recordId});

        //System.err.println("=======================before data filtering duplicates "+localRefValueDataCollection.size());

        localRefValueDataCollection = filterDuplicate(localRefValueDataCollection);

        //System.err.println("=======================total records found are ,after filtering duplicates "+localRefValueDataCollection.size());
        return localRefValueDataCollection;
    }

    /**
     * Added 24/02/2023 at 1253
     * Nees to be removed here and put into seperate function
     */
    private Collection<LocalRefValueData> filterDuplicate(Collection<LocalRefValueData> localRefValueDataCollection){

        Set<Long> idsSet = new HashSet<>();
        Map<Long ,LocalRefValueData> longLocalRefValueDataMap = new HashMap<>();

        Consumer<LocalRefValueData> duplicateCheckConsumer = (e)->{
            Long id = e.getId();
            boolean hasInserted = idsSet.add(id);

            //System.err.println("==================has inserted value ============="+hasInserted);
            if(!hasInserted){
                // this is a duplicate check where
                boolean hasCodeValue = OptionalHelper.isPresent(e.getCodeValue());
                if(hasCodeValue){
                    //System.err.println("------------------=============== has code value ,replace with that with actual value ");
                    longLocalRefValueDataMap.replace(id ,e);
                }
            }
            else{
                longLocalRefValueDataMap.put(id ,e);
            }
        };

        localRefValueDataCollection.stream().forEach(duplicateCheckConsumer);
        return longLocalRefValueDataMap.values();
    }


    @Override
    public LocalRefData retrieveOne(Long id) {

        final String sql = String.format("select %s where lr.id = ?",localRefMapper.schema());
        return this.jdbcTemplate.queryForObject(sql, this.localRefMapper, new Object[] {id});

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
        Collection<LocalRefData> localRefDataCollection = this.jdbcTemplate.query(sql, this.localRefMapper, new Object[] { refTable.ordinal()});

        Consumer<LocalRefData> consumer = (e)->{
            REF_VALUE_TYPE refValueType = REF_VALUE_TYPE.fromInt(Math.toIntExact(e.getRefValueType().getId()));
            if(refValueType == REF_VALUE_TYPE.CODE_VALUE){
                Long codeId = e.getCodeId();
                CodeData codeData = codeReadPlatformService.retrieveCode(codeId);
                Collection<CodeValueData> codeValueDataCollection = codeValueReadPlatformService.retrieveAllCodeValues(codeId);
                //e.setCodeData(Arrays.asList(codeData));
                codeData.setCodeValueData(codeValueDataCollection);
                e.setCodeData(Arrays.asList(codeData));
            }
        };

        localRefDataCollection.forEach(consumer);
        return localRefDataCollection;
    }

    private static final class LocalRefMapper implements RowMapper<LocalRefData> {

        private final String schema;

        public LocalRefMapper() {

            final StringBuilder builder = new StringBuilder(400);

            builder.append("lr.id as id, ");
            builder.append("lr.name as name, ");
            builder.append("lr.description as description, ");
            builder.append("lr.code_id as codeId, ");
            builder.append("lr.is_mandatory as isMandatory, ");
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
            final Boolean isMandatory = rs.getBoolean("isMandatory");

            final Integer valueTypeInt = rs.getInt("valueType");
            final REF_VALUE_TYPE refValueType = REF_VALUE_TYPE.fromInt(valueTypeInt);

            final Integer refTableInt = rs.getInt("refTable");
            final REF_TABLE refTable = REF_TABLE.fromInt(refTableInt);

            LocalRefData localRefData = new LocalRefData(id ,name ,description,refTable ,refValueType,codeName ,officeName ,codeId ,isMandatory);
            return localRefData;
        }
    }

    private static final class LocalRefValueMapper implements RowMapper<LocalRefValueData> {

        private final String schema;

        public LocalRefValueMapper() {

            final StringBuilder builder = new StringBuilder(400);

            builder.append("lrv.id as id, ");
            builder.append("lr.name as columnName, ");
            builder.append("lrv.value as value, ");
            builder.append("null as codeValue, ");
            builder.append("lrv.record_id as recordId ");
            builder.append("from m_local_ref_value lrv ");
            builder.append("join m_local_ref lr on lr.id = lrv.local_ref_id ");
            builder.append("where lr.ref_table = ? and lrv.record_id = ? ");
            builder.append("union ");
            builder.append("select ");
            builder.append("lrv.id as id, ");
            builder.append("lr.name as columnName, ");
            builder.append("mcv.code_value as value, ");
            builder.append("mcv.code_value as codeValue, ");
            builder.append("lrv.record_id as recordId ");
            builder.append("from m_local_ref_value lrv ");
            builder.append("join m_local_ref lr on lr.id = lrv.local_ref_id ");
            builder.append("join m_code mc on mc.id = lr.code_id ");
            builder.append("join m_code_value mcv on mcv.id = lrv.value ");

            this.schema = builder.toString();
        }
        public String schema() {
            return this.schema;
        }

        @Override
        public LocalRefValueData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String columnName = rs.getString("columnName");
            final String value = rs.getString("value");
            final Long id = rs.getLong("id");
            final Long recordId = rs.getLong("recordId");
            final String codeValue = rs.getString("codeValue");

            LocalRefValueData localRefDataValue = new LocalRefValueData(id ,recordId ,value ,columnName ,codeValue);
            return localRefDataValue;
        }
    }

}
