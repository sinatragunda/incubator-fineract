/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 20:05
 */
package org.apache.fineract.presentation.menu.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.localref.enumerations.APPLICATION_ACTION;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.presentation.menu.data.MenuData;
import org.apache.fineract.presentation.menu.data.MenuItemData;
import org.apache.fineract.presentation.menu.domain.Menu;
import org.apache.fineract.presentation.menu.enumerations.MENU_PLACEMENT;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.helper.EnumeratedDataHelper;
import org.apache.fineract.utility.service.EnumeratedData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.RowMapper;

@Service
public class MenuReadPlatformServiceImpl implements MenuReadPlatformService{

    private RoutingDataSource routingDataSource;
    private MenuItemReadPlatformService menuItemReadPlatformService ;
    private JdbcTemplate jdbcTemplate;
    private MenuMapper menuMapper = new MenuMapper();

    private Consumer<MenuData> attachMenuItems = (e)->{
        Long id = e.getId();
        System.err.println("--------------attach menu item for id "+id);
        List<MenuItemData> menuItemDataList = menuItemReadPlatformService.retrieveAllByMenuId(id);
        e.setMenuItemDataList(menuItemDataList);
    };

    @Autowired
    public MenuReadPlatformServiceImpl(RoutingDataSource routingDataSource, MenuItemReadPlatformService menuItemReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.menuItemReadPlatformService = menuItemReadPlatformService;
    }

    public MenuData template(){
        List menuItemDataList = menuItemReadPlatformService.getDropdownData();
        List menuPlacementOptions = EnumTemplateHelper.template(MENU_PLACEMENT.values());
        return MenuData.template(menuItemDataList ,menuPlacementOptions);
    }

    @Override
    public List<EnumOptionData> getDropdownData() {
        Collection collection = retrieveAll();
        return EnumeratedDataHelper.enumeratedData(new ArrayList<>(collection));
    }

    @Override
    public MenuData retrieveOne(Long id){

        String sql = String.format("select %s" ,menuMapper.schema());
        MenuData menuData = this.jdbcTemplate.queryForObject(sql ,menuMapper ,new Object[]{id});
        attachMenuItems.accept(menuData);
        return menuData;
    }


    @Override
    public Collection<MenuData> retrieveAll(){
        String sql = String.format("select %s" ,menuMapper.schema());
        Collection<MenuData> collection = this.jdbcTemplate.query(sql ,menuMapper);
        collection.stream().forEach(attachMenuItems);
        return collection;
    }

    private static final class MenuMapper implements RowMapper<MenuData> {

        public String schema() {
            return " m.id as id, m.name as name , m.menu_placement as menuPlacement, " +
                    "m.parent_menu_id as parentMenuId " +
                    " from m_menu m";
        }

        @Override
        public MenuData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long parentMenuId = JdbcSupport.getLong(rs, "parentMenuId");
            final String name = rs.getString("name");
            final Integer menuPlacementInt = JdbcSupport.getInteger(rs ,"menuPlacement");
            final EnumOptionData menuPlacementData = EnumTemplateHelper.fromIntToTemplate(MENU_PLACEMENT.values() ,menuPlacementInt);

            return new MenuData(id ,name ,menuPlacementData);
        }
    }
}
