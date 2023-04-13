/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:11
 */
package org.apache.fineract.presentation.screen.service;

import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import com.wese.component.defaults.enumerations.OPERAND_GATES;
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
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
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

        Consumer<ScreenData> consumer = (e)-> {
            Collection screenElements = retrieveAllScreenElementData(id);
            e.setScreenElementDataList(screenElements);
        };

        consumer.accept(screenData);

        return  screenData;

    }

    private Collection<ScreenElementData> retrieveAllScreenElementData(Long screenId){

        final String sql  = String.format("select %s ",screenElementMapper.schema());
        return this.jdbcTemplate.query(sql ,screenMapper ,new Object[]{screenId});
    }


    private static final class ScreenMapper implements RowMapper<ScreenData> {

        public String schema() {
            return " sc.id as id, sc.name as name ,sc.short_name as shortName ,sc.office_id as officeId" +
                    " sc.ref_table as refTable, " +
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
            return " sce.id as id, sce.name as name ,sce.model_name as modelName ,sce.display_name as displayName" +
                    " sce.comparison_type as comparisonType, " +
                    " sce.gate as gate , sce.show_on_ui as show " +
                    "sce.value as value " +
                    " sce.mandatory as mandatory" +
                    " sce.screen_id "+
                    " from m_screen_element sce";
        }

        }

        @Override
        public ScreenElementData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String displayName = rs.getString("displayName");
            final Boolean show = rs.getBoolean("show");
            final Boolean mandatory = rs.getBoolean("mandatory");
            final String value = rs.getString("value");
            final String modelName = rs.getString("modelName");


            final Integer comparisonTypeInt = JdbcSupport.getInteger(rs ,"comparisionType");
            final COMPARISON_TYPE comparisonType = (COMPARISON_TYPE) EnumTemplateHelper.fromIntEx(COMPARISON_TYPE.values() ,comparisonTypeInt);


            final Integer gateInt = JdbcSupport.getInteger(rs ,"comparisionType");
            final OPERAND_GATES operandGate = (OPERAND_GATES) EnumTemplateHelper.fromIntEx(OPERAND_GATES.values() ,gateInt);

            ScreenElementData screenElementData = new ScreenElementData(id ,name ,value ,modelName ,displayName ,show ,mandatory,operandGate ,comparisonType ,null);
            return screenElementData;
        }
    }



}
