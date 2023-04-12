/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 12:49
 */
package org.apache.fineract.presentation.menu.domain;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.localref.enumerations.APPLICATION_ACTION;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.helper.LocalRefConstants;
import org.apache.fineract.presentation.menu.helper.MenuConstants;
import org.apache.fineract.utility.helper.EnumTemplateHelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name="m_menu_item")
public class MenuItem extends AbstractPersistableCustom<Long> {

    @Column(name ="name")
    private String name ;

    @Enumerated(EnumType.ORDINAL)
    @Column(name ="application_action")
    private APPLICATION_ACTION applicationAction;

    @Enumerated(EnumType.ORDINAL)
    @Column(name ="ref_table")
    private REF_TABLE refTable;

    @Column(name ="shortcut")
    private String shortcut;


    @Column(name ="system_defined")
    private Boolean sysemDefined;


    public MenuItem(String name, APPLICATION_ACTION applicationAction, REF_TABLE refTable, String shortcut) {
        this.name = name;
        this.applicationAction = applicationAction;
        this.refTable = refTable;
        this.shortcut = shortcut;
        this.sysemDefined = false ;
    }

    public Map<String ,Object> update(JsonCommand command){

        final Map<String, Object> actualChanges = new LinkedHashMap<>(9);

        if (command.isChangeInIntegerParameterNamed(LocalRefConstants.refTableParam,this.refTable.ordinal())) {
            final Integer newValue = command.integerValueOfParameterNamed(LocalRefConstants.refTableParam);
            actualChanges.put(LocalRefConstants.refTableParam, newValue);
            this.refTable = REF_TABLE.fromInt(newValue);
        }


        if (command.isChangeInIntegerParameterNamed(MenuConstants.applicationActionParam,this.applicationAction.ordinal())) {
            final Integer newValue = command.integerValueOfParameterNamed(MenuConstants.applicationActionParam);
            actualChanges.put(MenuConstants.applicationActionParam, newValue);
            this.applicationAction = (APPLICATION_ACTION) EnumTemplateHelper.fromInt(APPLICATION_ACTION.values(), newValue);
        }


        if (command.isChangeInStringParameterNamed(GenericConstants.nameParam,this.name)){
            final String newValue = command.stringValueOfParameterNamed(GenericConstants.nameParam);
            actualChanges.put(GenericConstants.nameParam, newValue);
            this.name = newValue ;
        }

        if (command.isChangeInStringParameterNamed(MenuConstants.shortcutParam,this.shortcut)){
            final String newValue = command.stringValueOfParameterNamed(MenuConstants.shortcutParam);
            actualChanges.put(MenuConstants.shortcutParam, newValue);
            this.shortcut = newValue ;
        }
        return actualChanges ;
    }

    public APPLICATION_ACTION getApplicationAction() {
        return applicationAction;
    }

    public REF_TABLE getRefTable() {
        return refTable;
    }

    public String getShortcut() {
        return shortcut;
    }
}
