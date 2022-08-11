/*

    Created by Sinatra Gunda
    At 10:48 AM on 8/10/2022

*/
package org.apache.fineract.portfolio.paymentvoucher.domain;



import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name="m_payment_voucher")
public class PaymentVoucher extends AbstractPersistableCustom{

    @Column(name="journal_entry_id")
    private Long journalEntryId ;

    @Column(name="reference_id")
    private Long referenceId ;

    public PaymentVoucher(){}

    public PaymentVoucher(Long journalEntryId){
        this.journalEntryId = journalEntryId;
    }

    public Long getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(Long journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}
