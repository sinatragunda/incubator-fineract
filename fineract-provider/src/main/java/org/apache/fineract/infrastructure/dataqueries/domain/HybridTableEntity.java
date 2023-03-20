/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 07 March 2023 at 03:03
 */
package org.apache.fineract.infrastructure.dataqueries.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.*;

@Entity
@Table(name ="m_hybrid_entity_table")
public class HybridTableEntity extends AbstractPersistableCustom<Long>{

    @Enumerated(EnumType.ORDINAL)
    @Column(name="ref_table" ,nullable=false)
    private REF_TABLE refTable;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="application_record_id")
    private ApplicationRecord applicationRecord;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name ="ref_id")
    private AbstractPersistableCustom abstractPersistableCustom;

    protected HybridTableEntity(){}

    public HybridTableEntity(REF_TABLE refTable, ApplicationRecord applicationRecord, AbstractPersistableCustom abstractPersistableCustom) {
        this.refTable = refTable;
        this.applicationRecord = applicationRecord;
        this.abstractPersistableCustom = abstractPersistableCustom;
    }

    public REF_TABLE getRefTable() {
        return refTable;
    }

    public ApplicationRecord getApplicationRecord() {
        return applicationRecord;
    }

    public AbstractPersistableCustom getAbstractPersistableCustom() {
        return abstractPersistableCustom;
    }
}
