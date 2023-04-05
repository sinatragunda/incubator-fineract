/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 12:52
 */
package org.apache.fineract.presentation.menu.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.localref.enumerations.APPLICATION_ACTION;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.List;

public class MenuItemData implements EnumeratedData {

    private Long id ;
    private String name ;
    private String shortcut;
    private REF_TABLE refTable;
    private APPLICATION_ACTION applicationAction;

    private EnumOptionData applicationActionData;
    private EnumOptionData refTableData ;
    private List<EnumOptionData> refTableOptions ;
    private List<EnumOptionData> applicationActionOptions;

    public static MenuItemData template(){
        List refTableOptions = EnumTemplateHelper.template(REF_TABLE.values());
        List applicationsOptions = EnumTemplateHelper.template(APPLICATION_ACTION.values());
        return new MenuItemData(refTableOptions ,applicationsOptions);
    }
    public MenuItemData(List<EnumOptionData> refTableOptions, List<EnumOptionData> applicationActionOptions) {
        this.refTableOptions = refTableOptions;
        this.applicationActionOptions = applicationActionOptions;
    }

    public MenuItemData(Long id, String name, String shortcut, REF_TABLE refTable, APPLICATION_ACTION applicationAction) {
        this.id = id;
        this.name = name;
        this.shortcut = shortcut;
        this.refTable = refTable;
        this.applicationAction = applicationAction;
    }

    public MenuItemData(Long id, String name, String shortcut, EnumOptionData applicationActionData, EnumOptionData refTableData) {
        this.id = id;
        this.name = name;
        this.shortcut = shortcut;
        this.applicationActionData = applicationActionData;
        this.refTableData = refTableData;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
