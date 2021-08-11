/*

    Created by Sinatra Gunda
    At 9:46 AM on 8/4/2021

*/
package org.apache.fineract.infrastructure.dataqueries.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


@Table(name ="m_scheduled_report")
@Entity
public class ScheduledReport extends AbstractPersistableCustom<Long> {

    @Column(name="report_name")
    private String reportName ;

    @Column(name="parameters")
    private String parameters ;

    @Column(name="job_id")
    private Long jobId;

    public ScheduledReport(){}

    public ScheduledReport(String reportName, String parameters, Long jobId) {
        this.reportName = reportName;
        this.parameters = parameters;
        this.jobId = jobId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

}
