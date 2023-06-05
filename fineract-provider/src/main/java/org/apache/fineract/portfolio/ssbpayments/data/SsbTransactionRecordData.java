/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 31 May 2023 at 07:19
 */
package org.apache.fineract.portfolio.ssbpayments.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.taat.wese.weseaddons.ssb.enumerations.PROCESS_STATUS;

import java.util.Collection;

public class SsbTransactionRecordData {

    private Long id ;
    private String filename ;
    private String createdBy ;
    private Long createdByUserId ;
    private String processDetails;
    private PROCESS_STATUS processStatus;
    private EnumOptionData processStatusData ;

    private Collection<SsbTransactionData> ssbTransactionDataCollection;

    public SsbTransactionRecordData(Long id, String filename, String createdBy, Long createdByUserId,String processDetails, PROCESS_STATUS processStatus) {
        this.id = id;
        this.filename = filename;
        this.createdBy = createdBy;
        this.createdByUserId = createdByUserId;
        this.processDetails = processDetails;
        this.processStatus = processStatus;
        this.processStatusData = EnumTemplateHelper.template(processStatus);
    }

    public Long getId() {
        return id;
    }

    public void setSsbTransactionDataCollection(Collection<SsbTransactionData> ssbTransactionDataCollection) {
        this.ssbTransactionDataCollection = ssbTransactionDataCollection;
    }
}
