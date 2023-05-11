/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:11
 */
package org.apache.fineract.presentation.screen.service;

import com.wese.component.defaults.enumerations.*;
import com.wese.component.defaults.helper.FieldReflectionHelper;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.enumerations.REF_VALUE_TYPE;
import org.apache.fineract.portfolio.localref.helper.LocalRefToFieldValidationData;
import org.apache.fineract.portfolio.localref.service.LocalRefReadPlatformService;
import org.apache.fineract.presentation.screen.data.ScreenData;
import org.apache.fineract.presentation.screen.data.ScreenElementData;
import org.apache.fineract.presentation.screen.domain.Screen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.exceptions.ScreenNotFoundException;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.helper.EnumeratedDataHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.context.ApplicationContext;

@Service
public class ScreenReadPlatformServiceImpl implements ScreenReadPlatformService {

    private PlatformSecurityContext context;
    private JdbcTemplate jdbcTemplate;
    private ScreenMapper screenMapper = new ScreenMapper();
    private ScreenElementMapper screenElementMapper = new ScreenElementMapper();
    private ApplicationContext applicationContext;
    private LocalRefReadPlatformService localRefReadPlatformService;

    @Autowired
    public ScreenReadPlatformServiceImpl(PlatformSecurityContext context, RoutingDataSource routingDataSource ,final ApplicationContext applicationContext ,final LocalRefReadPlatformService localRefReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.applicationContext = applicationContext;
        this.localRefReadPlatformService = localRefReadPlatformService;
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

    /**
     * Added 02/05/2023 at 1011
     * Resolves list elements to get Dropdown data from respective BeanLoaders
     */
    private Consumer<ScreenElementData> listResolverConsumer = (e)-> {
        String key = e.getName();
        Class cl = e.getRefTable().getClassLoader().getCl();
        List<EnumOptionData> enumOptionDataList  = FieldReflectionHelper.dataTemplateList(applicationContext ,cl ,key);
        e.setSelectOptions(enumOptionDataList);
    };

    /**
     * Resolves list values from CodeValues ,templates
     * LocalRefs can only carry Virtual list values rather than an application itself
     */
    private Consumer<ScreenElementData> localRefListResolver = (e)->{
        Long id = e.getLocalRefData().getId();

        System.err.println("----------------------------local ref id is "+id);

        LocalRefData localRefData = localRefReadPlatformService.retrieveOneForTemplate(id);

        System.err.println("----------------do we have localrefdata ? "+ OptionalHelper.isPresent(localRefData));

        List<EnumOptionData> enumOptionDataList = LocalRefToFieldValidationData.getTemplateData(localRefData);

        System.err.println("------------------enum option list here is "+OptionalHelper.isPresent(enumOptionDataList));

        e.setSelectOptions(enumOptionDataList);
    };

    private Consumer<ScreenData> linkScreenElementConsumer = (e)-> {

        Long id = e.getId();
        Collection<ScreenElementData> screenElementsCollection = retrieveAllScreenElementData(id);
        Predicate<ScreenElementData> isGroupList = (v)-> v.getComparisonGroup().equals(COMPARISON_GROUP.LIST);

        Predicate<ScreenElementData> isSystemElement = (v)-> v.getElementType()==ELEMENT_TYPE.SYSTEM;

        /**
         * Avoid Reflection Errors so need to filter only elements that are of System Type
         * So another negate option will filter those of type LocalRef to resolve its list values
         */
        screenElementsCollection.stream().filter(isGroupList.and(isSystemElement)).forEach(listResolverConsumer);

        screenElementsCollection.stream().filter(isGroupList.and(isSystemElement.negate())).forEach(localRefListResolver);

        e.setScreenElementDataList(screenElementsCollection);
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
                    " sce.element_type as elementType, " +
                    " sce.local_ref_id as localRefId ," +
                    " mlr.ref_value_type as refValueType ," +
                    " sce.screen_id as screenId, " +
                    " ms.ref_table as refTable " +
                    " from m_screen_element sce " +
                    " join m_screen ms on ms.id = sce.screen_id " +
                    " left join m_local_ref mlr on mlr.id = sce.local_ref_id ";

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
            COMPARISON_GROUP comparisonGroup = (COMPARISON_GROUP) EnumTemplateHelper.fromIntEx(COMPARISON_GROUP.values(), comparisonGroupInt);

            final Integer refTableInt = JdbcSupport.getInteger(rs,"refTable");
            final REF_TABLE refTable = (REF_TABLE)EnumTemplateHelper.fromIntEx(REF_TABLE.values() ,refTableInt);

            final Integer elementTypeInt = JdbcSupport.getInteger(rs ,"elementType");
            final ELEMENT_TYPE elementType = (ELEMENT_TYPE)EnumTemplateHelper.fromIntEx(ELEMENT_TYPE.values() ,elementTypeInt);

            LocalRefData localRefData = null;

            if(elementType== ELEMENT_TYPE.LOCAL_REF){
                System.err.println("----------------local ref data collection here ----------");
                final Long localRefId = rs.getLong("localRefId");
                final Integer refValueTypeInt = JdbcSupport.getInteger(rs ,"refValueType");
                final REF_VALUE_TYPE refValueType = (REF_VALUE_TYPE) EnumTemplateHelper.fromIntEx(REF_VALUE_TYPE.values() ,refValueTypeInt);
                comparisonGroup = refValueType.group();

                System.err.println("---------------------comparison group for local ref value is "+comparisonGroup);
                localRefData = new LocalRefData(localRefId ,refValueType);
            }

            ScreenElementData screenElementData = new ScreenElementData(id, name, value, modelName, displayName, show, mandatory, operandGate, comparisonType, comparisonGroup, null ,parentScreenElementId ,refTable ,elementType ,localRefData);

            return screenElementData;
        }
    }

}
