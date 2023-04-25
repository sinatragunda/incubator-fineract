/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:11
 */
package org.apache.fineract.presentation.screen.service;

import com.wese.component.defaults.enumerations.COMPARISON_GROUP;
import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.OPERAND_GATES;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.presentation.screen.data.ScreenData;
import org.apache.fineract.presentation.screen.data.ScreenElementData;
import org.apache.fineract.presentation.screen.domain.Screen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.exceptions.ScreenNotFoundException;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@Service
public class ScreenReadPlatformServiceImpl implements ScreenReadPlatformService {

    private PlatformSecurityContext context;
    private JdbcTemplate jdbcTemplate;
    private ScreenMapper screenMapper = new ScreenMapper();
    private ScreenElementMapper screenElementMapper = new ScreenElementMapper();

    @Autowired
    public ScreenReadPlatformServiceImpl(PlatformSecurityContext context, RoutingDataSource routingDataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }

    @Override
    public Collection<ScreenData> retrieveAll(){

        final String sql  = String.format("select %s ",screenMapper.schema());
        return this.jdbcTemplate.query(sql ,screenMapper);
    }


    @Override
    public ScreenData retrieveOne(Long id) {

        String sql = String.format("select %s where sc.id = ?" ,screenMapper.schema());
        ScreenData screenData = this.jdbcTemplate.queryForObject(sql ,screenMapper ,new Object[]{id});
        //System.err.println("-------------qeury one object now back "+ OptionalHelper.isPresent(screenData));

        linkScreenElementConsumer.accept(screenData);
        return  screenData;
    }


    private Consumer<ScreenData> linkScreenElementConsumer = (e)-> {
        Long id = e.getId();
        Collection screenElements = retrieveAllScreenElementData(id);
        e.setScreenElementDataList(screenElements);
    };


    @Override
    public ScreenData retrieveOneByName(String name) {

        String sql = String.format("select %s where sc.name = ?" ,screenMapper.schema());
        try {
            ScreenData screenData = this.jdbcTemplate.queryForObject(sql, screenMapper, new Object[]{name});
            linkScreenElementConsumer.accept(screenData);
            return screenData;
        }
        catch (Exception n){
            throw new ScreenNotFoundException(name);
        }
    }

    private Collection<ScreenElementData> retrieveAllScreenElementData(Long screenId){

        final String sql  = String.format("select %s where sce.screen_id = ? and parent_screen_element_id IS NULL ",screenElementMapper.schema());
        Collection<ScreenElementData> collection = jdbcTemplate.query(sql ,screenElementMapper ,new Object[]{screenId});
        collection.stream().forEach(setChildElements);
        return collection;
    }

    private Consumer<ScreenElementData> setChildElements = (e)->{
        
        final Long parentId = e.getId();
        final String sql  = String.format("select %s where sce.parent_screen_element_id = ?",screenElementMapper.schema());
        Collection<ScreenElementData> collection = jdbcTemplate.query(sql ,screenElementMapper ,new Object[]{parentId});
        if(!collection.isEmpty()){
            e.setChildElements(collection);
        }
    };

    private static final class ScreenMapper implements RowMapper<ScreenData> {

        public String schema() {
            return " sc.id as id, sc.name as name ,sc.short_name as shortName ," +
                    " sc.office_id as officeId,  " +
                    " sc.ref_table as refTable, " +
                    " sc.multirow as multirow ," +
                    " sc.active as active ," +
                    " sc.parent_screen_id "+
                    " from m_screen sc";
        }

        @Override
        public ScreenData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String shortName = rs.getString("shortName");
            final Boolean active = rs.getBoolean("active");
            final Boolean multirow = rs.getBoolean("multirow");
            final Integer refTableInt = JdbcSupport.getInteger(rs ,"refTable");
            final REF_TABLE refTable = REF_TABLE.fromInt(refTableInt);

            ScreenData screenData = new ScreenData(id ,name ,shortName ,active ,multirow ,refTable);
            return screenData;
        }
    }


    private static final class ScreenElementMapper implements RowMapper<ScreenElementData> {

        public String schema() {
            return " sce.id as id, sce.name as name ,sce.model_name as modelName ,sce.display_name as displayName ," +
                    " sce.comparison_type as comparisonType, " +
                    " sce.comparison_group as comparisonGroup, " +
                    " sce.gate as gate , sce.show_on_ui as showOnUi, " +
                    " sce.value as value," +
                    " sce.parent_screen_element_id as parentScreenElementId ," +
                    " sce.mandatory as mandatory, " +
                    " sce.screen_id as screenId " +
                    " from m_screen_element sce";

        }

        @Override
        public ScreenElementData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String displayName = rs.getString("displayName");
            final Boolean show = rs.getBoolean("showOnUi");
            final Boolean mandatory = rs.getBoolean("mandatory");
            final String value = JdbcSupport.getString(rs ,"value");
            final String modelName = rs.getString("modelName");
            final Long parentScreenElementId = JdbcSupport.getLong(rs ,"parentScreenElementId");


            final Integer comparisonTypeInt = JdbcSupport.getInteger(rs, "comparisonType");
            final COMPARISON_TYPE comparisonType = (COMPARISON_TYPE) EnumTemplateHelper.fromIntEx(COMPARISON_TYPE.values(), comparisonTypeInt);

            final Integer gateInt = JdbcSupport.getInteger(rs, "gate");
            final OPERAND_GATES operandGate = (OPERAND_GATES) EnumTemplateHelper.fromIntEx(OPERAND_GATES.values(), gateInt);

            final Integer comparisonGroupInt = JdbcSupport.getInteger(rs, "comparisonGroup");
            final COMPARISON_GROUP comparisonGroup = (COMPARISON_GROUP) EnumTemplateHelper.fromIntEx(COMPARISON_GROUP.values(), comparisonGroupInt);

            ScreenElementData screenElementData = new ScreenElementData(id, name, value, modelName, displayName, show, mandatory, operandGate, comparisonType, comparisonGroup, null ,parentScreenElementId);
            return screenElementData;
        }
    }

}
