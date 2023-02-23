/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 16:12
 */
package org.apache.fineract.portfolio.localref.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name="m_local_ref_value")
public class LocalRefValue  extends AbstractPersistableCustom {

    @Column(name="record_id")
    private Long recordId;

    @Column(name="value")
    private String value ;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="local_ref_id" ,nullable=false)
    private LocalRef localRef;

    public LocalRefValue(LocalRef localRef ,Long recordId, String value) {
        this.recordId = recordId;
        this.value = value;
        this.localRef = localRef;
    }

    public LocalRef getLocalRef(){
        return this.localRef;
    }
}
