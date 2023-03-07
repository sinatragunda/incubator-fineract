/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 February 2023 at 12:28
 */
package org.apache.fineract.infrastructure.dataqueries.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.persistence.Column;

@Entity
@Table(name ="m_application")
public class ApplicationRecord extends AbstractPersistableCustom<Long> {

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="x_registered_table_id")
    private XRegisteredTable xRegisteredTable;

    @Column(name ="primary_identifier")
    private String primaryIdentifier ;

    private ApplicationRecord() {}

    public ApplicationRecord(XRegisteredTable xRegisteredTable ,String primaryIdentifier) {
        this.xRegisteredTable = xRegisteredTable;
        this.primaryIdentifier = primaryIdentifier;
    }

    public XRegisteredTable getxRegisteredTable() {
        return xRegisteredTable;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }
}
