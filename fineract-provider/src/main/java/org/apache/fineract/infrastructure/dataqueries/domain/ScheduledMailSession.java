/*

    Created by Sinatra Gunda
    At 1:56 AM on 9/3/2021

*/
package org.apache.fineract.infrastructure.dataqueries.domain;

import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;

public class ScheduledMailSession {

    private ScheduledJobDetail scheduledJobDetail;

    public ScheduledJobDetail getScheduledJobDetail() {
        return scheduledJobDetail;
    }

    public void setScheduledJobDetail(ScheduledJobDetail scheduledJobDetail) {
        this.scheduledJobDetail = scheduledJobDetail;
    }
}
