/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 June 2023 at 07:12
 */
package org.apache.fineract.infrastructure.dataqueries.data;

public class HybridEntityTableData {

    private Long id ;
    private Long refId ;
    private Long applicationRecordId ;

    public HybridEntityTableData(Long id, Long refId ,Long applicationRecordId) {
        this.id = id;
        this.refId = refId;
        this.applicationRecordId = applicationRecordId;
    }

    public Long getId() {
        return id;
    }

    public Long getRefId() {
        return refId;
    }

    public Long getApplicationRecordId() {
        return applicationRecordId;
    }
}
