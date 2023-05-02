/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 27 April 2023 at 08:02
 */
package org.apache.fineract.infrastructure.wsplugin.service;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.wsplugin.data.WsScriptContainerData;
import org.apache.fineract.infrastructure.wsplugin.data.WsScriptData;
import org.apache.fineract.infrastructure.wsplugin.enumerations.EXECUTION_LEVEL;
import org.apache.fineract.infrastructure.wsplugin.enumerations.RETURN_TYPE;
import org.apache.fineract.infrastructure.wsplugin.enumerations.SCRIPT_TYPE;
import org.apache.fineract.utility.helper.EnumTemplateHelper;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;
import org.springframework.context.ApplicationContext;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
public class WsScriptReadPlatformServiceImpl implements WsScriptReadPlatformService{


    private JdbcTemplate jdbcTemplate;
    private PlatformSecurityContext context;
    private WsSriptContainerMapper wsSriptContainerMapper = new WsSriptContainerMapper();
    private WsScriptMapper wsScriptMapper = new WsScriptMapper();

    @Autowired
    public WsScriptReadPlatformServiceImpl(PlatformSecurityContext context, RoutingDataSource routingDataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }

    private Consumer<WsScriptContainerData> attachWsScriptConsumer = (e)->{
        Long id = e.getId();
        Collection<WsScriptData> wsScriptDataCollection = retrieveAllScriptsByContainer(id);
        e.setWsScriptDataCollection(wsScriptDataCollection);
    };

    @Override
    public WsScriptContainerData retrieveOne(Long id) {

        String sql  = String.format("select %s where wsc.id = ?" ,wsSriptContainerMapper.schema());
        WsScriptContainerData wsScriptContainerData = this.jdbcTemplate.queryForObject(sql ,wsSriptContainerMapper ,new Object[]{id});
        attachWsScriptConsumer.accept(wsScriptContainerData);
        return wsScriptContainerData;

    }

    @Override
    public Collection<WsScriptContainerData> retrieveAll(Boolean eagerLoading) {

        String sql  = String.format("select %s" ,wsSriptContainerMapper.schema());
        Collection<WsScriptContainerData> wsScriptContainerData = this.jdbcTemplate.query(sql ,wsSriptContainerMapper);

        Predicate<WsScriptContainerData> isEagerLoading = (e)-> OptionalHelper.optionalOf(eagerLoading ,false);
        wsScriptContainerData.stream().filter(isEagerLoading).forEach(attachWsScriptConsumer);

        return wsScriptContainerData;

    }

    private Collection<WsScriptData> retrieveAllScriptsByContainer(Long containerId){

        String sql  = String.format("select %s where ws.ws_script_container_id = ?" ,wsScriptMapper.schema());
        Collection<WsScriptData> wsScriptContainerDataCollection = this.jdbcTemplate.query(sql ,wsScriptMapper ,new Object[]{containerId});
        return wsScriptContainerDataCollection;

    }


    private static final class WsScriptMapper implements RowMapper<WsScriptData> {

        private final String schema;

        public WsScriptMapper() {

            final StringBuilder builder = new StringBuilder(400);

            builder.append("ws.id as id, ");
            builder.append("ws.class_name as className, ");
            builder.append("ws.method_name as methodName, ");
            builder.append("ws.script_type as scriptType, ");
            builder.append("ws.return_type as returnType ,");
            builder.append("ws.execution_level as executionLevel ,");
            builder.append("ws.ws_script_container_id as containerId ");
            builder.append("from m_wsscript ws ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public WsScriptData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String className = rs.getString("className");
            final String methodName = rs.getString("methodName");
            final Integer returnTypeInt = rs.getInt("returnType");
            final Integer scriptTypeInt = rs.getInt("scriptType");

            final Long containerId = rs.getLong("containerId");

            final Integer executionLevelInt = rs.getInt("executionLevel");

            final EXECUTION_LEVEL executionLevel = (EXECUTION_LEVEL) EnumTemplateHelper.fromInt(EXECUTION_LEVEL.values() ,executionLevelInt);
            final SCRIPT_TYPE scriptType = (SCRIPT_TYPE) EnumTemplateHelper.fromInt(SCRIPT_TYPE.values() ,scriptTypeInt);
            final RETURN_TYPE returnType = (RETURN_TYPE) EnumTemplateHelper.fromInt(RETURN_TYPE.values() ,returnTypeInt);

            return new WsScriptData(id ,methodName ,className ,returnType ,scriptType ,executionLevel);
        }
    }


    private static final class WsSriptContainerMapper implements RowMapper<WsScriptContainerData> {

        private final String schema;

        public WsSriptContainerMapper() {

            final StringBuilder builder = new StringBuilder(400);

            builder.append("wsc.id as id, ");
            builder.append("wsc.name as name, ");
            builder.append("d.description as description, ");
            builder.append("d.file_name as filename, ");
            builder.append("d.location as fileLocation ,");
            builder.append("d.id as documentId ");
            builder.append("from m_wsscript_container wsc ");
            builder.append("left join m_document d on d.id = wsc.document_id ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public WsScriptContainerData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final Long documentId = rs.getLong("documentId");
            final String filename = rs.getString("filename");
            final String fileLocation = rs.getString("fileLocation");

            return new WsScriptContainerData(id ,name ,description ,filename ,fileLocation ,documentId);
        }
    }


}
