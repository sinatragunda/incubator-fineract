/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:41
 */
package org.apache.fineract.presentation.screen.domain;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "m_screen")
public class Screen extends AbstractPersistableCustom<Long> {

    @Column(name ="name")
    private String name ;


    @Column(name ="short_name")
    private String shortName ;

    @ManyToOne
    @Column(name ="office_id" ,nullable = false)
    private Office office;

    @ManyToOne
    @Column(name = "parent_screen_id" ,nullable = true)
    private Screen parentScreen;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ref_table")
    private REF_TABLE refTable;

    // still need to add start and end date

    @Column(name="active")
    private Boolean active ;

    @OneToMany(fetch = FetchType.EAGER  ,cascade = CascadeType.ALL ,mappedBy = "screen")
    private Set<ScreenElement> screenElementSet;

    protected Screen(){}

    public Screen(String name,String shortName , Office office, Screen parentScreen, REF_TABLE refTable, Boolean active, Set<ScreenElement> screenElementSet) {
        this.name = name;
        this.shortName = shortName;
        this.office = office;
        this.parentScreen = parentScreen;
        this.refTable = refTable;
        this.active = active;
        this.screenElementSet = screenElementSet;
    }

    public REF_TABLE getRefTable() {
        return refTable;
    }

    public Set<ScreenElement> getScreenElementSet() {
        return screenElementSet;
    }

    public void setScreenElementSet(Set<ScreenElement> screenElementSet) {

        boolean has = OptionalHelper.isPresent(this.screenElementSet);
        if(has){
            updateScreenElements(screenElementSet);
            return;
        }
        this.screenElementSet = screenElementSet;
    }

    public void updateScreenElements(Set<ScreenElement> screenElementSet){

        for(ScreenElement screenElement : screenElementSet){
            boolean updated = this.screenElementSet.add(screenElement);

            System.err.println("-----------------------------has updated "+updated);
            if(!updated){
                System.err.println("---------------remove and add element "+screenElement);
                this.screenElementSet.remove(screenElement);
                this.screenElementSet.add(screenElement);
            }
        }
    }


}
