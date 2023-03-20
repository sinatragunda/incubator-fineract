/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 07 March 2023 at 02:17
 */
package org.apache.fineract.infrastructure.dataqueries.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

@Entity
@Table(name ="x_registered_table")
public class XRegisteredTable extends AbstractPersistableCustom<Long> {

    @Column(name="registered_table_name")
    private String registeredTableName ;


    @Column(name="application_table_name")
    private String applicationTableName ;


    protected  XRegisteredTable(){}

    public XRegisteredTable(String registeredTableName ,String applicationTableName) {
        this.registeredTableName = registeredTableName;
        this.applicationTableName = applicationTableName;
    }

    public String getRegisteredTableName() {
        return registeredTableName;
    }

    public String getApplicationTableName(){
        return this.applicationTableName;
    }
}
