/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 12:57
 */
package org.apache.fineract.presentation.menu.service;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.localref.enumerations.APPLICATION_ACTION;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.presentation.menu.data.MenuItemData;
import org.apache.fineract.presentation.menu.domain.Menu;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.helper.EnumeratedDataHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class MenuItemReadPlatformServiceImpl implements MenuItemReadPlatformService{

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final MenuItemMapper menuItemMapper = new MenuItemMapper();

    @Autowired
    public MenuItemReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource routingDataSource){
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
    }

    @Override
    public MenuItemData template() {
        return MenuItemData.template();
    }

    public MenuItemData retrieveOne(Long id){
        String sql = String.format("select %s where mi.id = ? ",menuItemMapper.schema());
        return this.jdbcTemplate.queryForObject(sql ,menuItemMapper, new Object[]{id});
    }

    @Override
    public List<MenuItemData> retrieveAll() {

        String sql = String.format("select %s",menuItemMapper.schema());
        return this.jdbcTemplate.query(sql ,menuItemMapper);
    }

    @Override
    public List<MenuItemData> retrieveAllByMenuId(Long menuId) {

        String sql = String.format("select %s join m_menu_table mmt on mmt.menu_item_id = mi.id where mmt.menu_id = ?",menuItemMapper.schema());
        

        //System.err.println("=====================query is "+sql);

        return this.jdbcTemplate.query(sql ,menuItemMapper ,new Object[]{menuId});
    }


    @Override
    public List<EnumOptionData> getDropdownData() {
        List<MenuItemData> menuItemDataList = retrieveAll();
        return EnumeratedDataHelper.enumeratedData(menuItemDataList);
    }

    private static final class MenuItemMapper implements RowMapper<MenuItemData> {

        public String schema() {
            return " mi.id as id, mi.name as name ,mi.param as param ,mi.system_defined as systemDefined , mi.ref_table as refTable, " +
                    "mi.application_action as applicationAction ,mi.shortcut as shortcut " +
                    " from m_menu_item mi";
        }

        @Override
        public MenuItemData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String shortcut = rs.getString("shortcut");
            final String param = rs.getString("param");
            final Integer refTableInt = JdbcSupport.getInteger(rs ,"refTable");
            final Integer applicationActionInt = JdbcSupport.getInteger(rs ,"applicationAction");

            final EnumOptionData refTableData = EnumTemplateHelper.template(REF_TABLE.fromInt(refTableInt));
            final APPLICATION_ACTION applicationAction = (APPLICATION_ACTION) EnumTemplateHelper.fromInt(APPLICATION_ACTION.values() ,applicationActionInt);

            final EnumOptionData applicationActionData = EnumTemplateHelper.template(applicationAction);

            return new MenuItemData(id ,name ,shortcut ,applicationActionData ,refTableData ,param);
        }
    }




}
