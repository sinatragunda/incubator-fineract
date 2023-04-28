/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 10:39
 */
package org.apache.fineract.infrastructure.wsplugin.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.CascadeType;

@Entity
@Table(name="m_wsscript_container")
public class WsScriptContainer extends AbstractPersistableCustom<Long> {

    @Column(name="name")
    private String name ;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="document_id")
    private Document document;

    @OneToMany(fetch = FetchType.EAGER ,cascade = CascadeType.ALL ,mappedBy ="wsScriptContainer")
    private Set<WsScript> wsScriptSet;

    protected WsScriptContainer(){}

    public WsScriptContainer(String name, Document document, Set<WsScript> wsScriptSet) {
        this.name = name;
        this.document = document;
        this.wsScriptSet = wsScriptSet;
    }

    public String getName() {
        return name;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public Set<WsScript> getWsScriptSet() {
        return wsScriptSet;
    }
}
