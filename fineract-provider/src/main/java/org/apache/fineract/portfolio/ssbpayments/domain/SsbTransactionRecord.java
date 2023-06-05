/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 May 2023 at 07:17
 */
package org.apache.fineract.portfolio.ssbpayments.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.taat.wese.weseaddons.ssb.enumerations.PROCESS_STATUS;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
@Table(name = "m_ssb_transaction_record")
public class SsbTransactionRecord extends AbstractPersistableCustom {

    @Column(name = "filename")
    private String filename ;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy ;

    @Column(name = "process_details")
    private String processDetails ;

    @Enumerated(EnumType.ORDINAL)
    @Column(name ="process_status")
    private PROCESS_STATUS processStatus;


    @OneToMany(mappedBy="ssbTransactionRecord",cascade=CascadeType.ALL ,fetch = FetchType.EAGER)
    private List<SsbTransaction> ssbTransactionList;

    protected SsbTransactionRecord(){}

    public SsbTransactionRecord(AppUser createdBy , String filename) {
        this.filename = filename;
        this.createdBy = createdBy;
    }

    public void setProcessDetails(String processDetails) {
        this.processDetails = processDetails;
    }

    public void setProcessStatus(PROCESS_STATUS processStatus) {
        this.processStatus = processStatus;
    }

    public List<SsbTransaction> getSsbTransactionList() {
        return ssbTransactionList;
    }

    public PROCESS_STATUS getProcessStatus() {
        return processStatus;
    }
}
