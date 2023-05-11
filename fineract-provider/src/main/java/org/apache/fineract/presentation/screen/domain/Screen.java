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
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

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


    @Column(name="multirow")
    private Boolean multirow ;

    @OneToMany(fetch = FetchType.EAGER  ,cascade = CascadeType.ALL ,mappedBy = "screen")
    private Set<ScreenElement> screenElementSet;

    protected Screen(){}

    public Screen(String name,String shortName , Office office, Screen parentScreen, REF_TABLE refTable, Boolean active, Set<ScreenElement> screenElementSet ,Boolean multirow) {
        this.name = name;
        this.shortName = shortName;
        this.office = office;
        this.parentScreen = parentScreen;
        this.refTable = refTable;
        this.active = active;
        this.screenElementSet = screenElementSet;
        this.multirow = multirow;
    }

    public REF_TABLE getRefTable() {
        return refTable;
    }

    public Set<ScreenElement> getScreenElementSet() {
        return screenElementSet;
    }

    public void setScreenElementSet(Set<ScreenElement> screenElementSet) {
        this.screenElementSet = screenElementSet;
    }

    /**
     * Modified 22/04/2023 at 1523
     * This has an error concurrent modification
     */
    public void updateScreenElements(Set<ScreenElement> screenElementSet){

        synchronized (this.screenElementSet) {

            for (Iterator<ScreenElement> iter = screenElementSet.iterator(); iter.hasNext(); ) {
                ScreenElement screenElement = iter.next();
                boolean updated = this.screenElementSet.add(screenElement);
                if(!updated){
                    iter.remove();
                    updated = this.screenElementSet.add(screenElement);
                    System.err.println("----------what's the status now ? "+updated);
                }
            }
        }
    }

}
